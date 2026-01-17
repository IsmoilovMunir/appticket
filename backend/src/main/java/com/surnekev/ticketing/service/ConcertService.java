package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.Concert;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatCategory;
import com.surnekev.ticketing.domain.SeatStatus;
import com.surnekev.ticketing.dto.ConcertDto;
import com.surnekev.ticketing.dto.CreateConcertRequest;
import com.surnekev.ticketing.dto.SeatCategoryDto;
import com.surnekev.ticketing.dto.SeatDto;
import com.surnekev.ticketing.dto.UpdateConcertRequest;
import com.surnekev.ticketing.mapper.SeatMapper;
import com.surnekev.ticketing.repository.ConcertRepository;
import com.surnekev.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final SeatHoldService seatHoldService;

    @Transactional(readOnly = true)
    public ConcertDto getConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));
        return toDto(concert);
    }

    @Transactional(readOnly = true)
    public ConcertDto getConcertBySlug(String slug) {
        Concert concert = concertRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));
        return toDto(concert);
    }

    @Transactional(readOnly = true)
    public List<ConcertDto> getAllConcerts() {
        return concertRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConcertDto createConcert(CreateConcertRequest request) {
        // Проверяем уникальность slug
        if (concertRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalArgumentException("Concert with slug '" + request.slug() + "' already exists");
        }

        Concert concert = Concert.builder()
                .title(request.title())
                .slug(request.slug())
                .description(request.description())
                .concertDate(request.concertDate())
                .eventStartTime(request.eventStartTime())
                .guestStartTime(request.guestStartTime())
                .venue(request.venue())
                .city(request.city())
                .currency(request.currency())
                .ageRestriction(request.ageRestriction())
                .eventType(request.eventType())
                .posterUrl(request.posterUrl())
                .salesSchemeUrl(request.salesSchemeUrl())
                .createdAt(Instant.now())
                .build();

        concert = concertRepository.save(concert);
        return toDto(concert);
    }

    @Transactional
    public ConcertDto updateConcert(Long id, UpdateConcertRequest request) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));

        // Проверяем уникальность slug если он изменяется
        if (request.slug() != null && !request.slug().equals(concert.getSlug())) {
            if (concertRepository.findBySlug(request.slug()).isPresent()) {
                throw new IllegalArgumentException("Concert with slug '" + request.slug() + "' already exists");
            }
            concert.setSlug(request.slug());
        }

        if (request.title() != null) concert.setTitle(request.title());
        if (request.description() != null) concert.setDescription(request.description());
        if (request.concertDate() != null) concert.setConcertDate(request.concertDate());
        if (request.eventStartTime() != null) concert.setEventStartTime(request.eventStartTime());
        if (request.guestStartTime() != null) concert.setGuestStartTime(request.guestStartTime());
        if (request.venue() != null) concert.setVenue(request.venue());
        if (request.city() != null) concert.setCity(request.city());
        if (request.currency() != null) concert.setCurrency(request.currency());
        if (request.ageRestriction() != null) concert.setAgeRestriction(request.ageRestriction());
        if (request.eventType() != null) concert.setEventType(request.eventType());
        if (request.posterUrl() != null) concert.setPosterUrl(request.posterUrl());
        if (request.salesSchemeUrl() != null) concert.setSalesSchemeUrl(request.salesSchemeUrl());

        concert = concertRepository.save(concert);
        return toDto(concert);
    }

    @Transactional
    public void deleteConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));
        concertRepository.delete(concert);
    }

    private ConcertDto toDto(Concert concert) {
        // Вычисляем минимальную цену билетов из всех мест с категориями
        List<Seat> seats = seatRepository.findAllByConcertId(concert.getId());
        Integer minPriceCents = seats.stream()
                .filter(seat -> seat.getCategory() != null) // Игнорируем места без категории
                .map(seat -> {
                    // Используем ту же логику, что и в SeatMapper
                    return seat.getPriceOverrideCents() != null
                            ? seat.getPriceOverrideCents()
                            : seat.getCategory().getPriceCents();
                })
                .min(Integer::compareTo)
                .orElse(null); // null если нет мест с категориями

        return new ConcertDto(
                concert.getId(),
                concert.getTitle(),
                concert.getSlug(),
                concert.getDescription(),
                concert.getConcertDate(),
                concert.getEventStartTime(),
                concert.getGuestStartTime(),
                concert.getVenue(),
                concert.getCity(),
                concert.getCurrency(),
                concert.getAgeRestriction(),
                concert.getEventType(),
                concert.getPosterUrl(),
                concert.getSalesSchemeUrl(),
                minPriceCents
        );
    }

    @Transactional(readOnly = true)
    public List<SeatDto> listSeats(Long concertId) {
        List<Seat> seats = seatRepository.findAllByConcertId(concertId);
        Instant now = Instant.now();
        return seats.stream()
                .sorted(Comparator.comparingInt(Seat::getTableNumber).thenComparingInt(Seat::getChairNumber))
                .map(seat -> {
            Instant holdExpiresAt = null;
            if (seat.getStatus() == SeatStatus.HELD) {
                long remaining = seatHoldService.remainingHoldSeconds(seat.getId());
                if (remaining > 0) {
                    holdExpiresAt = now.plusSeconds(remaining);
                }
            }
            return seatMapper.toDto(seat, holdExpiresAt);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<SeatCategoryDto> listCategories(Long concertId) {
        List<Seat> seats = seatRepository.findAllByConcertId(concertId);
        // Получаем уникальные категории из мест концерта
        Map<SeatCategory, Integer> categoryPrices = seats.stream()
                .filter(seat -> seat.getCategory() != null)
                .collect(Collectors.toMap(
                        Seat::getCategory,
                        seat -> seat.getPriceOverrideCents() != null
                                ? seat.getPriceOverrideCents()
                                : seat.getCategory().getPriceCents(),
                        (price1, price2) -> Math.min(price1, price2) // Берем минимальную цену если есть override
                ));

        return categoryPrices.entrySet().stream()
                .map(entry -> {
                    SeatCategory category = entry.getKey();
                    int priceCents = entry.getValue();
                    return new SeatCategoryDto(
                            category.getId(),
                            category.getName(),
                            priceCents,
                            category.getDescription(),
                            category.getColorHex()
                    );
                })
                .sorted(Comparator.comparingInt(SeatCategoryDto::priceCents))
                .collect(Collectors.toList());
    }
}

