package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.TelegramLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramLogRepository extends JpaRepository<TelegramLog, Long> {
}

