package com.surnekev.ticketing.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * No-op rate limit filter that does nothing when Redis is disabled.
 */
@Component
@Slf4j
public class NoOpRateLimitFilter extends OncePerRequestFilter {

    private static volatile boolean loggedOnce;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!loggedOnce) {
            loggedOnce = true;
            log.info("Redis disabled: rate limiting is not available, allowing all requests");
        }
        filterChain.doFilter(request, response);
    }
}