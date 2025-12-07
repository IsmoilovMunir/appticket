package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.Concert;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatStatus;
import com.surnekev.ticketing.dto.ConcertDto;
import com.surnekev.ticketing.dto.SeatDto;
import com.surnekev.ticketing.mapper.SeatMapper;
import com.surnekev.ticketing.repository.ConcertRepository;
import com.surnekev.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

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
        return new ConcertDto(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getConcertDate(),
                concert.getVenue(),
                concert.getPosterUrl()
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
}

