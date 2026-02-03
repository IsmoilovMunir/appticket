package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.ConcertDto;
import com.surnekev.ticketing.dto.SeatCategoryDto;
import com.surnekev.ticketing.dto.SeatCategoryWithAvailabilityDto;
import com.surnekev.ticketing.dto.SeatDto;
import com.surnekev.ticketing.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping
    public List<ConcertDto> getAllConcerts() {
        return concertService.getAllConcerts();
    }

    @GetMapping("/{id}")
    public ConcertDto getConcert(@PathVariable Long id) {
        return concertService.getConcert(id);
    }

    @GetMapping("/slug/{slug}")
    public ConcertDto getConcertBySlug(@PathVariable String slug) {
        return concertService.getConcertBySlug(slug);
    }

    @GetMapping("/{id}/seats")
    public List<SeatDto> getSeats(@PathVariable Long id) {
        return concertService.listSeats(id);
    }

    @GetMapping("/slug/{slug}/seats")
    public List<SeatDto> getSeatsBySlug(@PathVariable String slug) {
        ConcertDto concert = concertService.getConcertBySlug(slug);
        return concertService.listSeats(concert.id());
    }

    @GetMapping("/{id}/categories")
    public List<SeatCategoryDto> getCategories(@PathVariable Long id) {
        return concertService.listCategories(id);
    }

    @GetMapping("/{id}/categories-with-availability")
    public List<SeatCategoryWithAvailabilityDto> getCategoriesWithAvailability(@PathVariable Long id) {
        return concertService.listCategoriesWithAvailability(id);
    }

    @GetMapping("/slug/{slug}/categories")
    public List<SeatCategoryDto> getCategoriesBySlug(@PathVariable String slug) {
        ConcertDto concert = concertService.getConcertBySlug(slug);
        return concertService.listCategories(concert.id());
    }

    @GetMapping("/slug/{slug}/categories-with-availability")
    public List<SeatCategoryWithAvailabilityDto> getCategoriesWithAvailabilityBySlug(@PathVariable String slug) {
        ConcertDto concert = concertService.getConcertBySlug(slug);
        return concertService.listCategoriesWithAvailability(concert.id());
    }
}

