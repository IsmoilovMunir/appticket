package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.ConfirmRegistrationRequest;
import com.surnekev.ticketing.dto.RegisterManagerRequest;
import com.surnekev.ticketing.service.ManagerRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/registration")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ManagerRegistrationController {

    private final ManagerRegistrationService registrationService;

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestRegistration(
            @RequestBody @Valid RegisterManagerRequest request) {
        try {
            registrationService.requestRegistration(request.username(), request.password());
            return ResponseEntity.ok(Map.of(
                    "message", "Код подтверждения отправлен в Telegram бот. Проверьте сообщения."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to request registration", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Не удалось отправить код подтверждения. Проверьте настройки Telegram бота."));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmRegistration(
            @RequestBody @Valid ConfirmRegistrationRequest request) {
        try {
            registrationService.confirmRegistration(request.username(), request.verificationCode());
            return ResponseEntity.ok(Map.of(
                    "message", "Менеджер успешно зарегистрирован!"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to confirm registration", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Не удалось подтвердить регистрацию"));
        }
    }
}

