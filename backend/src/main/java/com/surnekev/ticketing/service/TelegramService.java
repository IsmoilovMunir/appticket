package com.surnekev.ticketing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surnekev.ticketing.domain.Reservation;
import com.surnekev.ticketing.domain.Ticket;
import com.surnekev.ticketing.domain.TelegramLog;
import com.surnekev.ticketing.repository.TelegramLogRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final TelegramLogRepository telegramLogRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${telegram.bot-token:}")
    private String botToken;

    @Value("${telegram.manager-chat-id:}")
    private String managerChatId;

    public void notifyReservationHold(Reservation reservation) {
        if (!isConfigured()) {
            log.debug("Telegram bot token or chat id not configured, skipping notification");
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", managerChatId);
        payload.put("text", buildReservationText("Новая бронь", reservation));
        payload.put("parse_mode", "HTML");
        payload.put("reply_markup", buildInlineKeyboard(reservation));

        sendRequest("sendMessage", payload, reservation.getId(), TelegramLog.Direction.OUTBOUND);
    }

    public void notifyReservationStatus(Reservation reservation) {
        if (!isConfigured()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", managerChatId);
        payload.put("text", buildReservationText("Статус обновлён: " + reservation.getStatus(), reservation));
        payload.put("parse_mode", "HTML");

        sendRequest("sendMessage", payload, reservation.getId(), TelegramLog.Direction.OUTBOUND);
    }

    private void sendRequest(String method,
                             Map<String, Object> payload,
                             java.util.UUID reservationId,
                             TelegramLog.Direction direction) {
        try {
            persistLog(direction, payload);
            ResponseEntity<TelegramMessageResponse> response = restTemplate.postForEntity(
                    apiUrl(method), payload, TelegramMessageResponse.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
            if (response.getBody() == null || !response.getBody().isOk()) {
                log.warn("Telegram API call failed for reservation {}: {}", reservationId, response);
            }
        } catch (Exception ex) {
            log.error("Failed to send telegram notification for reservation {}", reservationId, ex);
        }
    }

    public void sendTicketQr(Ticket ticket, byte[] qrBytes) {
        if (!isConfigured()) {
            return;
        }
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("chat_id", managerChatId);
            body.add("caption", "Билет подтверждён: " + ticket.getId());
            body.add("photo", new ByteArrayResource(qrBytes) {
                @Override
                public String getFilename() {
                    return "ticket-" + ticket.getId() + ".png";
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            persistLog(TelegramLog.Direction.OUTBOUND, Map.of(
                    "method", "sendPhoto",
                    "ticketId", ticket.getId()
            ));
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl("sendPhoto"), new HttpEntity<>(body, headers), String.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
        } catch (Exception ex) {
            log.error("Failed to send ticket QR {}", ticket.getId(), ex);
        }
    }

    public void sendTicketPdf(Ticket ticket, byte[] pdfBytes) {
        if (!isConfigured()) {
            return;
        }
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("chat_id", managerChatId);
            body.add("caption", "PDF билет: " + ticket.getId());
            body.add("document", new ByteArrayResource(pdfBytes) {
                @Override
                public String getFilename() {
                    return "ticket-" + ticket.getId() + ".pdf";
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            persistLog(TelegramLog.Direction.OUTBOUND, Map.of(
                    "method", "sendDocument",
                    "ticketId", ticket.getId()
            ));
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl("sendDocument"), new HttpEntity<>(body, headers), String.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
        } catch (Exception ex) {
            log.error("Failed to send ticket PDF {}", ticket.getId(), ex);
        }
    }

    private String apiUrl(String method) {
        return "https://api.telegram.org/bot" + botToken + "/" + method;
    }

    private Object buildInlineKeyboard(Reservation reservation) {
        Map<String, Object> confirm = new HashMap<>();
        confirm.put("text", "Подтвердить");
        confirm.put("callback_data", "confirm:" + reservation.getId());

        Map<String, Object> cancel = new HashMap<>();
        cancel.put("text", "Отменить");
        cancel.put("callback_data", "cancel:" + reservation.getId());

        return Map.of("inline_keyboard", List.of(List.of(confirm, cancel)));
    }

    private String buildReservationText(String prefix, Reservation reservation) {
        String seats = reservation.getSeats().stream()
                .map(seat -> "Стол " + seat.getTableNumber() + ", место " + seat.getChairNumber())
                .collect(Collectors.joining("\n"));
        return """
                <b>%s</b>
                ID: %s
                Клиент: %s (%s)

                Места:
                %s

                Истекает: %s
                """.formatted(prefix,
                reservation.getId(),
                defaultString(reservation.getBuyerName(), "—"),
                defaultString(reservation.getBuyerPhone(), "—"),
                seats,
                reservation.getExpiresAt());
    }

    public void sendVerificationCode(String username, String verificationCode) {
        if (!isConfigured()) {
            log.warn("Telegram bot not configured, cannot send verification code");
            return;
        }

        String message = String.format("""
                🔐 <b>Запрос на регистрацию нового менеджера</b>
                
                Имя пользователя: <code>%s</code>
                Код подтверждения: <code>%s</code>
                
                Код действителен 10 минут.
                """, username, verificationCode);

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", managerChatId);
        payload.put("text", message);
        payload.put("parse_mode", "HTML");

        try {
            persistLog(TelegramLog.Direction.OUTBOUND, Map.of(
                    "method", "sendMessage",
                    "type", "verification_code",
                    "username", username
            ));
            ResponseEntity<TelegramMessageResponse> response = restTemplate.postForEntity(
                    apiUrl("sendMessage"), payload, TelegramMessageResponse.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
            if (response.getBody() == null || !response.getBody().isOk()) {
                log.warn("Failed to send verification code to Telegram: {}", response);
            }
        } catch (Exception ex) {
            log.error("Failed to send verification code to Telegram", ex);
            throw new RuntimeException("Не удалось отправить код подтверждения в Telegram", ex);
        }
    }

    public void sendAdminCredentials(String username, String password) {
        if (!isConfigured()) {
            log.warn("Telegram bot not configured, cannot send admin credentials");
            return;
        }

        String message = String.format("""
                🔑 <b>Создан администратор системы</b>
                
                Имя пользователя: <code>%s</code>
                Пароль: <code>%s</code>
                
                ⚠️ <b>ВАЖНО:</b> Обязательно измените пароль после первого входа!
                """, username, password);

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", managerChatId);
        payload.put("text", message);
        payload.put("parse_mode", "HTML");

        try {
            persistLog(TelegramLog.Direction.OUTBOUND, Map.of(
                    "method", "sendMessage",
                    "type", "admin_credentials",
                    "username", username
            ));
            ResponseEntity<TelegramMessageResponse> response = restTemplate.postForEntity(
                    apiUrl("sendMessage"), payload, TelegramMessageResponse.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
            if (response.getBody() == null || !response.getBody().isOk()) {
                log.warn("Failed to send admin credentials to Telegram: {}", response);
            } else {
                log.info("Admin credentials sent to Telegram successfully");
            }
        } catch (Exception ex) {
            log.error("Failed to send admin credentials to Telegram", ex);
            // Не бросаем исключение, чтобы не блокировать создание админа
        }
    }

    public void sendPartnerRequest(String fullName, String company, String phone, String email) {
        if (!isConfigured()) {
            log.warn("Telegram bot not configured, cannot send partner request");
            return;
        }

        String message = String.format("""
                🤝 <b>Новая заявка на партнёрство</b>
                
                <b>ФИО:</b> %s
                <b>Компания:</b> %s
                <b>Телефон:</b> <code>%s</code>
                <b>Email:</b> <code>%s</code>
                
                📅 <i>Время заявки: %s</i>
                """, 
                fullName, 
                company, 
                phone, 
                email,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", managerChatId);
        payload.put("text", message);
        payload.put("parse_mode", "HTML");

        try {
            persistLog(TelegramLog.Direction.OUTBOUND, Map.of(
                    "method", "sendMessage",
                    "type", "partner_request",
                    "company", company,
                    "fullName", fullName
            ));
            ResponseEntity<TelegramMessageResponse> response = restTemplate.postForEntity(
                    apiUrl("sendMessage"), payload, TelegramMessageResponse.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
            if (response.getBody() == null || !response.getBody().isOk()) {
                log.warn("Failed to send partner request to Telegram: {}", response);
                throw new RuntimeException("Не удалось отправить заявку в Telegram");
            }
        } catch (Exception ex) {
            log.error("Failed to send partner request to Telegram", ex);
            throw new RuntimeException("Не удалось отправить заявку в Telegram", ex);
        }
    }

    private boolean isConfigured() {
        return StringUtils.hasText(botToken) && StringUtils.hasText(managerChatId);
    }

    private void persistLog(TelegramLog.Direction direction, Object payload) {
        if (payload == null) {
            return;
        }
        try {
            JsonNode node = objectMapper.valueToTree(payload);
            telegramLogRepository.save(TelegramLog.builder()
                    .direction(direction)
                    .payload(node)
                    .createdAt(Instant.now())
                    .build());
        } catch (Exception ex) {
            log.debug("Unable to persist telegram log", ex);
        }
    }

    private String defaultString(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TelegramMessageResponse {
        private boolean ok;
        private Result result;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Result {
            private Long message_id;
        }
    }
}

