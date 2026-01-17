package com.surnekev.ticketing.config;

import com.surnekev.ticketing.domain.Concert;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatCategory;
import com.surnekev.ticketing.domain.SeatStatus;
import com.surnekev.ticketing.repository.ConcertRepository;
import com.surnekev.ticketing.repository.SeatCategoryRepository;
import com.surnekev.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ConcertRepository concertRepository;
    private final SeatCategoryRepository seatCategoryRepository;
    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        if (concertRepository.count() > 0) {
            return;
        }

        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        Instant concertDate = LocalDateTime.of(2026, 1, 3, 19, 0)
                .atZone(moscowZone)
                .toInstant();
        String description = """
                Новогодний вечер с SAFARMUHAMMAD
                • Живое исполнение
                • Шоу-программа
                • Новогодний ужин

                Сбор гостей в 18:00, начало в 19:00.
                """;

        Concert concert = concertRepository.save(Concert.builder()
                .title("Новогодний вечер с SAFARMUHAMMAD")
                .slug("novogodniy-vecher-s-safarmuhammad")
                .description(description)
                .concertDate(concertDate)
                .venue("м. Текстильщики, улица Юных Ленинцев, 12, ресторан «АСАКИ»")
                .posterUrl("https://images.unsplash.com/photo-1489515217757-5fd1be406fef?auto=format&fit=crop&w=800&q=80")
                .createdAt(Instant.now())
                .build());

        List<CategorySeed> categorySeeds = List.of(
                new CategorySeed("Golden 10000 ₽", 1_000_000, "#D4AF37"),
                new CategorySeed("Silver 7000 ₽", 700_000, "#1F9D6C"),
                new CategorySeed("Bronze 5000 ₽", 500_000, "#6F42C1")
        );

        List<SeatCategory> categories = categorySeeds.stream()
                .map(seed -> seatCategoryRepository.save(SeatCategory.builder()
                        .name(seed.name())
                        .priceCents(seed.priceCents())
                        .description(seed.name() + " зона")
                        .colorHex(seed.colorHex())
                        .build()))
                .toList();

        List<Seat> seats = new ArrayList<>();
        for (int table = 0; table < 50; table++) {
            SeatCategory category;
            if (table < 10) {
                category = findCategory(categories, "Golden 10000 ₽");
            } else if (table < 30) {
                category = findCategory(categories, "Silver 7000 ₽");
            } else {
                category = findCategory(categories, "Bronze 5000 ₽");
            }

            for (int chair = 0; chair < 10; chair++) {
                seats.add(Seat.builder()
                        .concert(concert)
                        .tableNumber(table + 1)
                        .chairNumber(chair + 1)
                        .category(category)
                        .status(SeatStatus.AVAILABLE)
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }

    private SeatCategory findCategory(List<SeatCategory> categories, String name) {
        return categories.stream()
                .filter(cat -> cat.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();
    }

    private record CategorySeed(String name, int priceCents, String colorHex) {
    }
}

