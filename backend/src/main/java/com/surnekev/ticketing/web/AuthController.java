package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.AuthRequest;
import com.surnekev.ticketing.dto.AuthResponse;
import com.surnekev.ticketing.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request, 
                                              HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            // Успешный логин - сбрасываем счетчик попыток
            String clientIp = getClientIp(httpRequest);
            String key = "rate_limit:login:" + clientIp;
            redisTemplate.delete(key);

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());

            String token = jwtService.generateToken(authentication.getName(), claims);
            log.info("Successful login for user: {}", request.username());
            return ResponseEntity.ok(new AuthResponse(token, jwtService.getTtlSeconds()));
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for user: {} from IP: {}", request.username(), getClientIp(httpRequest));
            throw e;
        }
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

