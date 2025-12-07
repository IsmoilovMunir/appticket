package com.surnekev.ticketing.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;
    private static final String RATE_LIMIT_PREFIX = "rate_limit:login:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Применяем rate limiting только к эндпоинту логина
        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            try {
                String clientIp = getClientIp(request);
                String key = RATE_LIMIT_PREFIX + clientIp;
                
                ValueOperations<String, String> ops = redisTemplate.opsForValue();
                String attemptsStr = ops.get(key);
                int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
                
                if (attempts >= MAX_ATTEMPTS) {
                    log.warn("Rate limit exceeded for IP: {}", clientIp);
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Too many login attempts. Please try again later.\"}");
                    return;
                }
                
                // Увеличиваем счетчик попыток
                if (attempts == 0) {
                    ops.set(key, "1", BLOCK_DURATION_MINUTES, TimeUnit.MINUTES);
                } else {
                    ops.increment(key);
                }
            } catch (Exception e) {
                // Если Redis недоступен, логируем предупреждение и пропускаем запрос
                // Это позволяет приложению работать даже без Redis (graceful degradation)
                log.warn("Redis unavailable for rate limiting: {}. Allowing request to proceed.", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

