package com.surnekev.ticketing.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.surnekev.ticketing.dto.CancelReservationRequest;
import com.surnekev.ticketing.service.ReservationService;
import com.surnekev.ticketing.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Webhook для Telegram Bot API.
 * Принимает raw Update от Telegram: сообщения (/start, /myid) и callback_query (подтверждение/отмена брони).
 */
@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhookController {

    private final ReservationService reservationService;
    private final TelegramService telegramService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody JsonNode update) {
        if (update == null) {
            return ResponseEntity.badRequest().build();
        }

        // Обработка текстовых команд (message)
        JsonNode message = update.get("message");
        if (message != null) {
            handleMessage(message);
            return ResponseEntity.ok().build();
        }

        // Обработка нажатий на inline-кнопки (callback_query)
        JsonNode callbackQuery = update.get("callback_query");
        if (callbackQuery != null) {
            handleCallbackQuery(callbackQuery);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok().build();
    }

    private void handleMessage(JsonNode message) {
        JsonNode chat = message.get("chat");
        JsonNode textNode = message.get("text");
        if (chat == null || textNode == null) {
            return;
        }

        String chatId = chat.get("id").asText();
        String text = textNode.asText().trim();

        if ("/start".equalsIgnoreCase(text) || "/myid".equalsIgnoreCase(text)) {
            String response = """
                    👋 <b>App Ticket Bot</b>

                    Ваш Chat ID: <code>%s</code>

                    Добавьте этот ID в поле «Telegram Chat ID менеджеров концерта» при создании или редактировании концерта в админ-панели. Тогда вы будете получать уведомления о бронях и билетах по этому концерту.
                    """.formatted(chatId);
            telegramService.sendMessageToChat(chatId, response);
            log.debug("Sent chat ID {} to user", chatId);
        }
    }

    private void handleCallbackQuery(JsonNode callbackQuery) {
        JsonNode callbackQueryIdNode = callbackQuery.get("id");
        if (callbackQueryIdNode != null) {
            telegramService.answerCallbackQuery(callbackQueryIdNode.asText());
        }

        JsonNode dataNode = callbackQuery.get("data");
        if (dataNode == null) {
            return;
        }

        String data = dataNode.asText();
        String[] parts = data.split(":", 2);
        if (parts.length != 2) {
            log.warn("Invalid callback_data format: {}", data);
            return;
        }

        String action = parts[0].toLowerCase();
        String reservationIdStr = parts[1].trim();

        try {
            UUID reservationId = UUID.fromString(reservationIdStr);
            if ("confirm".equals(action)) {
                reservationService.confirmReservation(reservationId, "telegram");
            } else if ("cancel".equals(action)) {
                reservationService.cancelReservation(reservationId,
                        new CancelReservationRequest("telegram", "telegram cancel"), false);
            }
        } catch (Exception ex) {
            log.error("Failed to process callback for reservation {}", reservationIdStr, ex);
        }
    }
}
