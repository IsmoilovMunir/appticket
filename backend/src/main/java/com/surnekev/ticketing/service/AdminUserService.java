package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.AdminRole;
import com.surnekev.ticketing.domain.AdminUser;
import com.surnekev.ticketing.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.surnekev.ticketing.dto.AdminUserDto;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TelegramService telegramService;

    @Value("${admin.default-password:}")
    private String defaultAdminPassword;

    public void ensureDefaultAdmin() {
        adminUserRepository.findByUsernameIgnoreCase("admin").orElseGet(() -> {
            // Если пароль не задан через переменную окружения, не создаем дефолтного админа
            if (defaultAdminPassword == null || defaultAdminPassword.isBlank()) {
                log.warn("ADMIN_DEFAULT_PASSWORD не задан. Дефолтный админ не будет создан. " +
                        "Создайте пользователя вручную через базу данных.");
                return null;
            }
            
            AdminUser admin = AdminUser.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                    .roles(Set.of(AdminRole.ADMIN, AdminRole.MANAGER, AdminRole.CHECKIN))
                    .enabled(true)
                    .createdAt(Instant.now())
                    .build();
            AdminUser savedAdmin = adminUserRepository.save(admin);
            
            log.info("Создан дефолтный админ пользователь с ролями: {}. Пожалуйста, измените пароль после первого входа!", 
                    savedAdmin.getRoles());
            
            // Отправляем пароль в Telegram, если настроен бот
            telegramService.sendAdminCredentials("admin", defaultAdminPassword);
            
            return savedAdmin;
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser user = adminUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        Set<AdminRole> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(AdminRole.MANAGER);
        }
        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                user.isEnabled(),
                true,
                true,
                true,
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .toList()
        );
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        AdminUser user = adminUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный текущий пароль");
        }

        // Проверяем, что новый пароль отличается от текущего
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Новый пароль должен отличаться от текущего");
        }

        // Обновляем пароль
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        adminUserRepository.save(user);
        log.info("Пароль успешно изменен для пользователя: {}", username);
    }

    public List<AdminUserDto> getAllUsers() {
        return adminUserRepository.findAll().stream()
                .map(user -> new AdminUserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles() != null ? user.getRoles() : Set.of(),
                        user.isEnabled(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}

