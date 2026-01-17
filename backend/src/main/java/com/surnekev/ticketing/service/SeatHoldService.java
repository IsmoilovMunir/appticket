package com.surnekev.ticketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class SeatHoldService implements SeatHoldServiceInterface {

    private final StringRedisTemplate redisTemplate;

    @Value("${seatmap.hold-ttl-seconds:1800}")
    private long holdTtlSeconds;

    public SeatHoldService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long seatId) {
        return "seat:" + seatId;
    }

    @Override
    public boolean acquire(Set<Long> seatIds, String reservationId) {
        Set<String> acquiredKeys = new HashSet<>();

        try {
            for (Long seatId : seatIds) {
                String key = key(seatId);
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, reservationId, Duration.ofSeconds(holdTtlSeconds));
                if (success == null || !success) {
                    return false;
                }
                acquiredKeys.add(key);
            }
            return true;
        } finally {
            if (acquiredKeys.size() != seatIds.size()) {
                // rollback partial locks
                redisTemplate.delete(acquiredKeys);
            }
        }
    }

    @Override
    public void release(Set<Long> seatIds) {
        Set<String> keys = seatIds.stream().map(this::key).collect(Collectors.toSet());
        redisTemplate.delete(keys);
    }

    @Override
    public long remainingHoldSeconds(Long seatId) {
        Long expire = redisTemplate.getExpire(key(seatId));
        return expire == null ? -1 : expire;
    }

    @Override
    public Optional<UUID> currentReservationForSeat(Long seatId) {
        String value = redisTemplate.opsForValue().get(key(seatId));
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void refresh(Set<Long> seatIds) {
        seatIds.forEach(seatId -> redisTemplate.expire(key(seatId), Duration.ofSeconds(holdTtlSeconds)));
    }
}
