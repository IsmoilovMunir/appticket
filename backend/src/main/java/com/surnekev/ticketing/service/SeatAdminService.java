package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.Concert;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatCategory;
import com.surnekev.ticketing.dto.AssignSeatCategoryRequest;
import com.surnekev.ticketing.dto.CreateSeatCategoryRequest;
import com.surnekev.ticketing.dto.SeatCategoryDto;
import com.surnekev.ticketing.dto.SeatCategoryUpdateRequest;
import com.surnekev.ticketing.dto.SeatPriceOverrideRequest;
import com.surnekev.ticketing.dto.SeatTableAssignmentDto;
import com.surnekev.ticketing.repository.ConcertRepository;
import com.surnekev.ticketing.repository.SeatCategoryRepository;
import com.surnekev.ticketing.repository.SeatRepository;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatAdminService {

    private final SeatCategoryRepository seatCategoryRepository;
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<SeatCategoryDto> listCategories() {
        return seatCategoryRepository.findAll().stream()
                .sorted(Comparator.comparing(SeatCategory::getId))
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SeatCategoryDto createCategory(CreateSeatCategoryRequest request) {
        // Проверяем, что категория с таким именем не существует
        if (seatCategoryRepository.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }
        SeatCategory category = SeatCategory.builder()
                .name(request.name())
                .priceCents(request.priceCents())
                .description(request.description())
                .colorHex(request.colorHex() != null ? request.colorHex().toUpperCase() : null)
                .build();
        SeatCategory saved = seatCategoryRepository.save(category);
        seatCategoryRepository.flush();
        entityManager.detach(saved);
        SeatCategory created = seatCategoryRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalStateException("Category not found after creation"));
        return toDto(created);
    }

    @Transactional
    public SeatCategoryDto updateCategory(Long id, SeatCategoryUpdateRequest request) {
        SeatCategory category = seatCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat category not found"));
        category.setName(request.name());
        if (request.priceCents() != null) {
            category.setPriceCents(request.priceCents());
        }
        category.setDescription(request.description());
        if (request.colorHex() != null) {
            category.setColorHex(request.colorHex().toUpperCase());
        }
        seatCategoryRepository.save(category);
        // Принудительно сохраняем изменения в БД
        seatCategoryRepository.flush();
        // Очищаем кэш EntityManager для этой сущности, чтобы гарантировать свежие данные при следующем запросе
        entityManager.detach(category);
        // Перезагружаем категорию из БД, чтобы получить актуальные данные
        SeatCategory updatedCategory = seatCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat category not found after update"));
        return toDto(updatedCategory);
    }

    @Transactional(readOnly = true)
    public List<SeatTableAssignmentDto> listTableAssignments(Long concertId) {
        Long resolvedConcertId = resolveConcertId(concertId);
        // Очищаем контекст persistence, чтобы избежать устаревших данных категорий/мест
        entityManager.clear();
        List<Seat> seats = seatRepository.findByConcertIdOrderByTableNumberAscChairNumberAsc(resolvedConcertId);
        Map<Integer, List<Seat>> grouped = seats.stream()
                .collect(Collectors.groupingBy(Seat::getTableNumber, Collectors.toCollection(ArrayList::new)));
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> buildAssignment(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    public void assignCategoryToTables(AssignSeatCategoryRequest request) {
        Long concertId = resolveConcertId(request.concertId());
        SeatCategory category = seatCategoryRepository.findById(request.seatCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Seat category not found"));
        List<Seat> seats = seatRepository.findByConcertIdAndTableNumberIn(concertId, request.tableNumbers());
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("No seats found for provided tables");
        }
        seats.forEach(seat -> {
            seat.setCategory(category);
            seat.setPriceOverrideCents(null);
        });
        seatRepository.saveAll(seats);
    }

    @Transactional
    public void applyPriceOverride(SeatPriceOverrideRequest request) {
        Long concertId = resolveConcertId(request.concertId());
        if (request.priceCents() != null && request.priceCents() < 0) {
            throw new IllegalArgumentException("Price must be greater or equal to zero");
        }
        List<Seat> seats;
        if (request.chairNumber() != null) {
            Seat seat = seatRepository.findByConcertIdAndTableNumberAndChairNumber(
                    concertId, request.tableNumber(), request.chairNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found for provided coordinates"));
            seats = List.of(seat);
        } else {
            seats = seatRepository.findByConcertIdAndTableNumber(concertId, request.tableNumber());
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("No seats found for provided table");
        }
        seats.forEach(seat -> seat.setPriceOverrideCents(request.priceCents()));
        seatRepository.saveAll(seats);
    }

    private SeatTableAssignmentDto buildAssignment(int tableNumber, List<Seat> seats) {
        SeatCategory categoryFromSeat = seats.getFirst().getCategory();
        // Всегда перезагружаем категорию из БД через JPQL запрос, чтобы гарантировать актуальные данные
        // Это критично для получения обновленной цены после изменения категории
        // Используем подсказки для обхода кэша Hibernate
        TypedQuery<SeatCategory> query = entityManager.createQuery(
                "SELECT c FROM SeatCategory c WHERE c.id = :id", SeatCategory.class);
        query.setParameter("id", categoryFromSeat.getId());
        query.setHint("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
        query.setHint("jakarta.persistence.cache.storeMode", CacheStoreMode.BYPASS);
        SeatCategory category = query.getSingleResult();
        boolean hasOverrides = seats.stream().anyMatch(seat -> seat.getPriceOverrideCents() != null);
        Integer overrideValue = null;
        if (hasOverrides) {
            var overrides = seats.stream()
                    .map(Seat::getPriceOverrideCents)
                    .filter(value -> value != null)
                    .collect(Collectors.toSet());
            boolean allSeatsOverridden = seats.stream().allMatch(seat -> seat.getPriceOverrideCents() != null);
            if (allSeatsOverridden && overrides.size() == 1) {
                overrideValue = overrides.iterator().next();
            }
        }
        return new SeatTableAssignmentDto(
                tableNumber,
                category.getId(),
                category.getName(),
                category.getPriceCents(),
                hasOverrides,
                overrideValue,
                category.getColorHex()
        );
    }

    private SeatCategoryDto toDto(SeatCategory category) {
        return new SeatCategoryDto(
                category.getId(),
                category.getName(),
                category.getPriceCents(),
                category.getDescription(),
                category.getColorHex()
        );
    }

    private Long resolveConcertId(Long concertId) {
        if (concertId != null) {
            return concertId;
        }
        return concertRepository.findFirstByOrderByConcertDateAsc()
                .map(Concert::getId)
                .orElseThrow(() -> new IllegalStateException("No concerts configured"));
    }
}

