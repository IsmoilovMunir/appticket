package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.SeatCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatCategoryRepository extends JpaRepository<SeatCategory, Long> {
    Optional<SeatCategory> findByNameIgnoreCase(String name);
}

