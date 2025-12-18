package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.PartnerRequest;
import com.surnekev.ticketing.service.TelegramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/partner-request")
@RequiredArgsConstructor
public class PartnerRequestController {

    private final TelegramService telegramService;

    @PostMapping
    public ResponseEntity<Map<String, String>> submitPartnerRequest(@RequestBody @Valid PartnerRequest request) {
        try {
            telegramService.sendPartnerRequest(
                    request.fullName(),
                    request.company(),
                    request.phone(),
                    request.email()
            );
            return ResponseEntity.ok(Map.of("message", "Заявка успешно отправлена"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Не удалось отправить заявку. Попробуйте позже."));
        }
    }
}
