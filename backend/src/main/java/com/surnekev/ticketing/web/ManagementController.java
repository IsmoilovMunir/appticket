package com.surnekev.ticketing.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/management")
@RequiredArgsConstructor
@Slf4j
public class ManagementController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/fix-reservation-constraint")
    @GetMapping("/fix-reservation-constraint")
    public ResponseEntity<Map<String, String>> fixReservationConstraint() {
        String updateConstraint = """
                DO $$
                BEGIN
                    ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_status_check;
                    
                    ALTER TABLE reservations
                    ADD CONSTRAINT reservations_status_check
                    CHECK (status IN (
                        'HELD',
                        'PARTIALLY_CONFIRMED',
                        'PARTIALLY_CANCELLED',
                        'CONFIRMED',
                        'CANCELLED',
                        'EXPIRED'
                    ));
                END $$;
                """;

        try {
            jdbcTemplate.execute(updateConstraint);
            log.info("Reservation status constraint fixed successfully");
            return ResponseEntity.ok(Map.of("status", "success", "message", "Reservation status constraint has been updated"));
        } catch (DataAccessException ex) {
            log.error("Unable to fix reservation status constraint: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Failed to update constraint: " + ex.getMessage()));
        }
    }
}

