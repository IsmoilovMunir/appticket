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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Value("${telegram.enabled:true}")
    private boolean telegramEnabled;

    public void notifyReservationHold(Reservation reservation) {
        if (!isConfigured()) {
            log.debug("Telegram bot token or chat id not configured, skipping notification");
            return;
        }

        String text = buildReservationText("Новая бронь", reservation);
        Object replyMarkup = buildInlineKeyboard(reservation);
        for (String chatId : getChatIdsForConcert(reservation.getConcert())) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chat_id", chatId);
            payload.put("text", text);
            payload.put("parse_mode", "HTML");
            payload.put("reply_markup", replyMarkup);
            sendRequest("sendMessage", payload, reservation.getId(), TelegramLog.Direction.OUTBOUND);
        }
    }

    public void notifyReservationStatus(Reservation reservation) {
        if (!isConfigured()) {
            return;
        }

        String statusText = translateStatus(reservation.getStatus());
        String text = buildReservationText("Статус обновлён: " + statusText, reservation);
        for (String chatId : getChatIdsForConcert(reservation.getConcert())) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chat_id", chatId);
            payload.put("text", text);
            payload.put("parse_mode", "HTML");
            sendRequest("sendMessage", payload, reservation.getId(), TelegramLog.Direction.OUTBOUND);
        }
    }

    /**
     * Возвращает все chat_id для уведомлений: глобальный + привязанные к концерту.
     */
    private Set<String> getChatIdsForConcert(com.surnekev.ticketing.domain.Concert concert) {
        Set<String> ids = new HashSet<>();
        if (StringUtils.hasText(managerChatId)) {
            ids.add(managerChatId.trim());
        }
        if (concert != null && StringUtils.hasText(concert.getTelegramManagerChatIds())) {
            Arrays.stream(concert.getTelegramManagerChatIds().split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(ids::add);
        }
        return ids;
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
        Set<String> chatIds = getChatIdsForConcert(
                ticket.getReservation() != null ? ticket.getReservation().getConcert() : null);
        for (String chatId : chatIds) {
            try {
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("chat_id", chatId);
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
                log.error("Failed to send ticket QR {} to chat {}", ticket.getId(), chatId, ex);
            }
        }
    }

    public void sendTicketPdf(Ticket ticket, byte[] pdfBytes) {
        if (!isConfigured()) {
            return;
        }
        Set<String> chatIds = getChatIdsForConcert(
                ticket.getReservation() != null ? ticket.getReservation().getConcert() : null);
        for (String chatId : chatIds) {
            try {
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("chat_id", chatId);
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
                log.error("Failed to send ticket PDF {} to chat {}", ticket.getId(), chatId, ex);
            }
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
        boolean simpleMode = reservation.getConcert() != null && reservation.getConcert().isSimpleMode();
        String seats = reservation.getSeats().stream()
                .map(seat -> simpleMode && seat.getCategory() != null
                        ? seat.getCategory().getName()
                        : "Стол " + seat.getTableNumber() + ", место " + seat.getChairNumber())
                .collect(Collectors.joining("\n"));
        
        // Рассчитываем итоговую цену
        int totalPriceCents = reservation.getSeats().stream()
                .mapToInt(seat -> {
                    if (seat.getPriceOverrideCents() != null) {
                        return seat.getPriceOverrideCents();
                    } else {
                        return seat.getCategory() != null ? seat.getCategory().getPriceCents() : 0;
                    }
                })
                .sum();
        
        String totalPriceFormatted = formatPrice(totalPriceCents);
        String placesLabel = simpleMode ? "Билеты:" : "Места:";
        
        return """
                <b>%s</b>
                ID: %s
                Клиент: %s (%s)

                %s
                %s

                Итоговая цена: <b>%s</b>
                Истекает: %s
                """.formatted(prefix,
                reservation.getId(),
                defaultString(reservation.getBuyerName(), "—"),
                defaultString(reservation.getBuyerPhone(), "—"),
                placesLabel,
                seats,
                totalPriceFormatted,
                reservation.getExpiresAt());
    }

    public void sendVerificationCode(String username, String verificationCode) {
        if (!isConfigured() || !StringUtils.hasText(managerChatId)) {
            log.warn("Telegram bot or manager chat id not configured, cannot send verification code");
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
        if (!isConfigured() || !StringUtils.hasText(managerChatId)) {
            log.warn("Telegram bot or manager chat id not configured, cannot send admin credentials");
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
        } catch (HttpClientErrorException ex) {
            String msg = ex.getResponseBodyAsString();
            if (msg != null && msg.contains("chat not found")) {
                log.warn("Telegram: chat not found (TELEGRAM_CHAT_ID={}). Напишите боту /start и укажите полученный Chat ID в настройках.", managerChatId);
            } else {
                log.error("Failed to send admin credentials to Telegram: {}", msg != null ? msg : ex.getMessage());
            }
        } catch (Exception ex) {
            log.error("Failed to send admin credentials to Telegram", ex);
        }
    }

    public void sendPartnerRequest(String fullName, String company, String phone, String email) {
        if (!isConfigured() || !StringUtils.hasText(managerChatId)) {
            log.warn("Telegram bot or manager chat id not configured, cannot send partner request");
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

    /**
     * Подтверждает получение callback_query (убирает индикатор загрузки на кнопке).
     */
    public void answerCallbackQuery(String callbackQueryId) {
        if (!isConfigured() || !StringUtils.hasText(callbackQueryId)) {
            return;
        }
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("callback_query_id", callbackQueryId);
            restTemplate.postForEntity(apiUrl("answerCallbackQuery"), payload, String.class);
        } catch (Exception ex) {
            log.debug("Failed to answer callback query {}", callbackQueryId, ex);
        }
    }

    /**
     * Отправляет сообщение в указанный чат (для ответа на команды бота).
     */
    public void sendMessageToChat(String chatId, String text) {
        if (!isConfigured() || !StringUtils.hasText(chatId)) {
            return;
        }
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("chat_id", chatId);
            payload.put("text", text);
            payload.put("parse_mode", "HTML");
            persistLog(TelegramLog.Direction.OUTBOUND, payload);
            ResponseEntity<TelegramMessageResponse> response = restTemplate.postForEntity(
                    apiUrl("sendMessage"), payload, TelegramMessageResponse.class);
            persistLog(TelegramLog.Direction.INBOUND, response.getBody());
        } catch (Exception ex) {
            log.error("Failed to send message to chat {}", chatId, ex);
        }
    }

    private boolean isConfigured() {
        return telegramEnabled && StringUtils.hasText(botToken);
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

    private String translateStatus(com.surnekev.ticketing.domain.ReservationStatus status) {
        return switch (status) {
            case HELD -> "Забронировано";
            case PARTIALLY_CONFIRMED -> "Частично подтверждено";
            case PARTIALLY_CANCELLED -> "Частично отменено";
            case CONFIRMED -> "Подтверждено";
            case CANCELLED -> "Отменено";
            case EXPIRED -> "Истекло";
        };
    }

    private String formatPrice(int priceCents) {
        double priceRubles = priceCents / 100.0;
        return String.format("%.2f ₽", priceRubles);
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

