package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.ConcertDto;
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

    @GetMapping("/{id}")
    public ConcertDto getConcert(@PathVariable Long id) {
        return concertService.getConcert(id);
    }

    @GetMapping("/{id}/seats")
    public List<SeatDto> getSeats(@PathVariable Long id) {
        return concertService.listSeats(id);
    }
}

