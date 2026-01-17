package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.AdminRole;
import com.surnekev.ticketing.domain.AdminUser;
import com.surnekev.ticketing.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class ManagerRegistrationService implements ManagerRegistrationServiceInterface {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TelegramService telegramService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SecureRandom random = new SecureRandom();
    
    private static final String VERIFICATION_CODE_PREFIX = "manager_registration:";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_TTL_MINUTES = 10;

    /**
     * Генерирует код подтверждения и отправляет его в Telegram
     */
    public void requestRegistration(String username, String password) {
        // Проверяем, что пользователь не существует
        if (adminUserRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        // Генерируем код подтверждения
        String verificationCode = generateVerificationCode();
        
        // Сохраняем в Redis: ключ = username, значение = password:code
        String key = VERIFICATION_CODE_PREFIX + username.toLowerCase();
        String value = passwordEncoder.encode(password) + ":" + verificationCode;
        
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        
        log.info("Verification code generated for username: {}", username);
        
        // Отправляем код в Telegram
        telegramService.sendVerificationCode(username, verificationCode);
    }

    /**
     * Подтверждает регистрацию по коду и создает пользователя
     */
    public void confirmRegistration(String username, String verificationCode) {
        String key = VERIFICATION_CODE_PREFIX + username.toLowerCase();
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedValue = ops.get(key);
        
        if (storedValue == null) {
            throw new IllegalArgumentException("Код подтверждения истек или не найден. Запросите новый код.");
        }
        
        // Формат: passwordHash:code
        String[] parts = storedValue.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalStateException("Неверный формат данных регистрации");
        }
        
        String storedPasswordHash = parts[0];
        String storedCode = parts[1];
        
        // Проверяем код
        if (!storedCode.equals(verificationCode)) {
            throw new IllegalArgumentException("Неверный код подтверждения");
        }
        
        // Создаем пользователя
        AdminUser newUser = AdminUser.builder()
                .username(username)
                .passwordHash(storedPasswordHash)
                .roles(Set.of(AdminRole.MANAGER)) // По умолчанию только MANAGER
                .enabled(true)
                .createdAt(Instant.now())
                .build();
        
        adminUserRepository.save(newUser);
        
        // Удаляем временные данные из Redis
        redisTemplate.delete(key);
        
        log.info("New manager registered: {}", username);
    }

    @Override
    public void cleanupExpiredCodes() {
        // Redis автоматически удаляет истекшие ключи, дополнительная очистка не требуется
        log.debug("Redis automatically cleans up expired registration codes");
    }

    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}

