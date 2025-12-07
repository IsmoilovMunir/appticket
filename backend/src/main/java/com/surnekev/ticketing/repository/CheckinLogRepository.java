package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.CheckinLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckinLogRepository extends JpaRepository<CheckinLog, Long> {
}

