package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    Optional<Concert> findFirstByOrderByConcertDateAsc();
    
    Optional<Concert> findBySlug(String slug);
}

