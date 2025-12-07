package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.AdminUserDto;
import com.surnekev.ticketing.dto.ChangePasswordRequest;
import com.surnekev.ticketing.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CHECKIN')")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            adminUserService.changePassword(username, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok(Map.of("message", "Пароль успешно изменен"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDto>> getAllUsers(Authentication authentication) {
        log.debug("Getting all users. Current user: {}, Authorities: {}", 
                authentication != null ? authentication.getName() : "null",
                authentication != null ? authentication.getAuthorities() : "null");
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }
}

