package com.surnekev.ticketing.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class SchemaMaintenanceRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    private boolean isPostgreSQL() {
        try (Connection conn = dataSource.getConnection()) {
            String product = conn.getMetaData().getDatabaseProductName();
            return product != null && product.toLowerCase().contains("postgresql");
        } catch (Exception ex) {
            log.debug("Could not detect database type: {}", ex.getMessage());
            return false;
        }
    }

    @Override
    public void run(String... args) {
        ensureReservationStatusConstraint();
        ensureTicketStatusConstraint();
        ensureQrColumnType();
        ensurePdfColumnType();
    }

    private void ensureReservationStatusConstraint() {
        String dropConstraint = "ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_status_check";
        String addConstraint = """
                ALTER TABLE reservations ADD CONSTRAINT reservations_status_check
                CHECK (status IN ('HELD', 'PARTIALLY_CONFIRMED', 'PARTIALLY_CANCELLED', 'CONFIRMED', 'CANCELLED', 'EXPIRED'))
                """;

        try {
            jdbcTemplate.execute(dropConstraint);
            jdbcTemplate.execute(addConstraint);
            log.info("Reservation status constraint refreshed to include partial states.");
        } catch (DataAccessException ex) {
            log.warn("Unable to refresh reservation status constraint: {}", ex.getMessage());
        }
    }

    private void ensureTicketStatusConstraint() {
        String dropConstraint = "ALTER TABLE tickets DROP CONSTRAINT IF EXISTS tickets_status_check;";

        String addConstraint = """
                        ALTER TABLE tickets
                        ADD CONSTRAINT tickets_status_check
                        CHECK (status IN ('RESERVED','SOLD','USED','CANCELLED'));
                """;

        try {
            jdbcTemplate.execute(dropConstraint);
            jdbcTemplate.execute(addConstraint);
            log.info("Ticket status constraint refreshed.");
        } catch (DataAccessException ex) {
            log.warn("Unable to refresh ticket status constraint: {}", ex.getMessage());
        }
    }

    private void ensureQrColumnType() {
        String alterColumn = """
                ALTER TABLE tickets
                ALTER COLUMN qr_code_url TYPE TEXT;
                """;
        try {
            jdbcTemplate.execute(alterColumn);
            log.info("Ticket qr_code_url column ensured as TEXT.");
        } catch (DataAccessException ex) {
            log.warn("Unable to alter qr_code_url column (already TEXT?): {}", ex.getMessage());
        }
    }

    private void ensurePdfColumnType() {
        if (!isPostgreSQL()) {
            log.debug("Skipping pdf_document migration (PostgreSQL-only).");
            return;
        }
        String migrateColumn = """
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.columns
                        WHERE table_name = 'tickets'
                          AND column_name = 'pdf_document'
                          AND data_type = 'oid'
                    ) THEN
                        ALTER TABLE tickets
                        ALTER COLUMN pdf_document TYPE bytea
                        USING pg_catalog.lo_get(pdf_document);
                    END IF;
                END $$;
                """;
        try {
            jdbcTemplate.execute(migrateColumn);
            log.info("Ticket pdf_document column converted to BYTEA.");
        } catch (DataAccessException ex) {
            log.warn("Unable to ensure pdf_document column type: {}", ex.getMessage());
        }
    }
}

