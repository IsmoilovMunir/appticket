package com.surnekev.ticketing.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class TicketPdfService {

    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(MOSCOW_ZONE);
    
    private static final String ORGANIZER_NAME = "App Ticket";
    
    private static final String FULL_RULES = """
            <div class="rules-content">
              <p class="rules-intro">Данный документ является билетом и предоставляет право на посещение мероприятия.</p>
              <p class="rules-intro">Билет действителен на одно лицо.</p>
              <p class="rules-intro">Билет необходимо предъявить на входе.</p>
              
              <div class="rules-divider">⸻</div>
              
              <h3 class="rules-subtitle">Билеты</h3>
              <ul class="rules-list">
                <li>Не копируйте и не перепродавайте билет.</li>
                <li>Сохраняйте в целостности все штрихкоды и QR-коды на билете.</li>
                <li>Не размещайте фото билета в социальных сетях — злоумышленники могут воспользоваться им.</li>
                <li>Организатор не несёт ответственности за поддельные билеты или билеты, приобретённые вне официальных каналов.</li>
                <li>В случае утери билета вы можете скачать его повторно из письма или запросить у службы поддержки.</li>
              </ul>
              
              <div class="rules-divider">⸻</div>
              
              <h3 class="rules-subtitle">Поведение на мероприятии</h3>
              <ul class="rules-list">
                <li>Соблюдайте правила поведения в общественных местах и нормы этикета ресторанов.</li>
                <li>Занимайте места согласно купленному билету.</li>
                <li>Сохраняйте билет до окончания мероприятия и предъявляйте его по требованию администрации или службы охраны.</li>
              </ul>
              
              <h3 class="rules-subtitle">Запрещено приносить:</h3>
              <ul class="rules-list">
                <li>огнестрельное, газовое или холодное оружие;</li>
                <li>пиротехнику любых видов;</li>
                <li>легковоспламеняющиеся жидкости;</li>
                <li>алкогольные напитки и продукты питания</li>
                <li>крупногабаритные предметы, мешающие размещению гостей.</li>
              </ul>
              
              <div class="rules-divider">⸻</div>
              
              <h3 class="rules-subtitle">Дети</h3>
              <ul class="rules-list">
                <li>Дети с 14 до 16 лет допускаются на мероприятие только по наличию отдельного билета</li>
                <li>Родители или сопровождающие несут полную ответственность за безопасность и поведение детей.</li>
              </ul>
              
              <div class="rules-divider">⸻</div>
              
              <h3 class="rules-subtitle">Фото- и видеосъёмка</h3>
              <ul class="rules-list">
                <li>Фото- и видеосъёмка на мероприятии разрешена.</li>
                <li>Покупая билет и посещая мероприятие, вы автоматически соглашаетесь на фото - и видеосъёмку, а также на использование материалов с вашим участием в публикациях организатора и партнёрских проектов (социальные сети, рекламные материалы и др.).</li>
              </ul>
              
              <div class="rules-divider">⸻</div>
              
              <h3 class="rules-subtitle">Дополнительные условия</h3>
              <ul class="rules-list">
                <li>Уважайте персонал и имущество ресторана.</li>
                <li>Запрещается самостоятельно перемещать мебель без разрешения администрации.</li>
                <li>В случае нарушения правил администрация вправе попросить гостя покинуть мероприятие без возврата стоимости билета.</li>
              </ul>
              
              <div class="rules-divider">⸻</div>
              
              <p class="rules-footer">Информация о возврате билетов доступна на сайте: www.appticket.ru</p>
            </div>
            """;

    private static final List<String> FONT_CANDIDATE_PATHS = List.of(
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/noto/NotoSans-Regular.ttf",
            "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
            "/Library/Fonts/Arial Unicode.ttf"
    );
    private static final String BUNDLED_FONT = "/fonts/DejaVuSans.ttf";

    private static final float PAGE_WIDTH_MM = 210f;  // A4 width
    private static final float PAGE_HEIGHT_MM = 297f; // A4 height

    private String cachedFontFaceCss;
    private boolean fontWarningLogged;

    public byte[] renderTicket(Ticket ticket, byte[] qrPng) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useDefaultPageSize(PAGE_WIDTH_MM, PAGE_HEIGHT_MM, PdfRendererBuilder.PageSizeUnits.MM);
            builder.withHtmlContent(buildHtml(ticket, qrPng), null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception ex) {
            Seat seat = ticket.getSeat();
            var reservation = ticket.getReservation();
            log.error(
                    "Failed to render PDF for ticket {} (reservation {}, seat table {}, chair {}, QR present: {}). Cause: {}",
                    ticket.getId(),
                    reservation != null ? reservation.getId() : null,
                    seat != null ? seat.getTableNumber() : null,
                    seat != null ? seat.getChairNumber() : null,
                    qrPng != null,
                    ex.getMessage(),
                    ex
            );
            throw new IllegalStateException("Не удалось сформировать PDF билет", ex);
        }
    }

    private String buildHtml(Ticket ticket, byte[] qrPng) {
        var reservation = ticket.getReservation();
        var concert = reservation.getConcert();
        var seat = ticket.getSeat();

        String concertTitle = escape(concert.getTitle());
        String buyer = escape(defaultValue(reservation.getBuyerName(), "—"));
        String dateTime = concert.getConcertDate() != null
                ? DATE_TIME_FORMATTER.format(concert.getConcertDate())
                : "Уточняется";
        String venue = escape(defaultValue(concert.getVenue(), "—"));
        String price = formatPrice(centsToRubles(seat));
        String qrBase64 = Base64.getEncoder().encodeToString(qrPng);

        String fontCss = fontFaceCss();
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    %s
                    @page {
                      size: A4;
                      margin: 0;
                    }
                    html, body {
                      width: 210mm;
                      height: 297mm;
                      margin: 0;
                      padding: 0;
                      font-family: 'TicketFont', 'Arial', sans-serif;
                      color: #1a1a1a;
                      background: #ffffff;
                    }
                    * { box-sizing: border-box; }
                    
                    .ticket-container {
                      width: 100%%;
                      height: 100%%;
                      padding: 20mm;
                      display: flex;
                      flex-direction: column;
                    }
                    
                    /* Header Section */
                    .header {
                      border-bottom: 3px solid #18723F;
                      padding-bottom: 3mm;
                      margin-bottom: 3mm;
                    }
                    .organizer {
                      font-size: 12pt;
                      color: #18723F;
                      font-weight: 600;
                      text-transform: uppercase;
                      letter-spacing: 1px;
                      margin-bottom: 2mm;
                    }
                    .concert-title {
                      font-size: 28pt;
                      font-weight: 700;
                      color: #1a1a1a;
                      line-height: 1.2;
                      margin: 0 0 1mm 0;
                    }
                    .ticket-id {
                      font-size: 9pt;
                      color: #666;
                      margin-top: 1mm;
                    }
                    
                    /* Info Section */
                    .info-section {
                      display: grid;
                      grid-template-columns: 1fr 1fr;
                      gap: 4mm;
                      margin-bottom: 4mm;
                    }
                    .info-item {
                      display: flex;
                      flex-direction: column;
                    }
                    .info-label {
                      font-size: 8pt;
                      color: #666;
                      text-transform: uppercase;
                      letter-spacing: 0.5px;
                      margin-bottom: 0.5mm;
                      font-weight: 600;
                    }
                    .info-value {
                      font-size: 12pt;
                      font-weight: 600;
                      color: #1a1a1a;
                    }
                    .info-value-large {
                      font-size: 16pt;
                      font-weight: 700;
                      color: #18723F;
                    }
                    
                    /* Seat Card */
                    .seat-card {
                      background: linear-gradient(135deg, #f0f9f4 0%%, #e8f5e9 100%%);
                      border: 2px solid #18723F;
                      border-radius: 6mm;
                      padding: 4mm;
                      margin-bottom: 4mm;
                      display: grid;
                      grid-template-columns: 1fr 1fr;
                      gap: 2mm;
                    }
                    .seat-item {
                      display: flex;
                      flex-direction: column;
                    }
                    .seat-label {
                      font-size: 8pt;
                      color: #18723F;
                      text-transform: uppercase;
                      margin-bottom: 0.5mm;
                      font-weight: 600;
                    }
                    .seat-value {
                      font-size: 14pt;
                      font-weight: 700;
                      color: #1a1a1a;
                    }
                    
                    /* QR Section */
                    .qr-section {
                      text-align: center;
                      margin-bottom: 4mm;
                      padding: 4mm;
                      background: #fafafa;
                      border-radius: 6mm;
                    }
                    .qr-section img {
                      width: 70mm;
                      height: 70mm;
                      margin: 0 auto 2mm;
                      display: block;
                    }
                    .qr-note {
                      font-size: 9pt;
                      color: #666;
                      margin-top: 1mm;
                    }
                    
                    /* Rules Section */
                    .rules-section {
                      flex: 1;
                      overflow: hidden;
                      page-break-before: always;
                    }
                    .rules-title {
                      font-size: 14pt;
                      font-weight: 700;
                      color: #18723F;
                      text-transform: uppercase;
                      letter-spacing: 1px;
                      margin-bottom: 2mm;
                      border-bottom: 2px solid #18723F;
                      padding-bottom: 1mm;
                    }
                    .rules-content {
                      font-size: 7pt;
                      line-height: 1.3;
                      color: #333;
                    }
                    .rules-intro {
                      font-weight: 600;
                      margin-bottom: 1mm;
                    }
                    .rules-divider {
                      text-align: center;
                      color: #999;
                      margin: 1mm 0;
                      font-size: 10pt;
                    }
                    .rules-subtitle {
                      font-size: 8pt;
                      font-weight: 700;
                      color: #18723F;
                      margin: 1mm 0 0.5mm 0;
                      text-transform: uppercase;
                    }
                    .rules-list {
                      margin: 0 0 1mm 0;
                      padding-left: 4mm;
                    }
                    .rules-list li {
                      margin-bottom: 0.5mm;
                    }
                    .rules-footer {
                      font-weight: 600;
                      color: #18723F;
                      margin-top: 1mm;
                      text-align: center;
                      font-size: 7pt;
                    }
                  </style>
                </head>
                <body>
                  <div class="ticket-container">
                    <!-- Header -->
                    <div class="header">
                      <div class="organizer">%s</div>
                      <h1 class="concert-title">%s</h1>
                      <div class="ticket-id">Билет № %s</div>
                    </div>
                    
                    <!-- Info Section -->
                    <div class="info-section">
                      <div class="info-item">
                        <span class="info-label">Клиент</span>
                        <span class="info-value">%s</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">Дата и время</span>
                        <span class="info-value">%s</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">Площадка</span>
                        <span class="info-value">%s</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">Организатор</span>
                        <span class="info-value">%s</span>
                      </div>
                    </div>
                    
                    <!-- Seat Card -->
                    <div class="seat-card">
                      <div class="seat-item">
                        <span class="seat-label">Стол</span>
                        <span class="seat-value">%d</span>
                      </div>
                      <div class="seat-item">
                        <span class="seat-label">Место</span>
                        <span class="seat-value">%d</span>
                      </div>
                      <div class="seat-item">
                        <span class="seat-label">Стоимость</span>
                        <span class="seat-value">%s</span>
                      </div>
                      <div class="seat-item">
                        <span class="seat-label">Идентификатор</span>
                        <span class="seat-value" style="font-size: 10pt; word-break: break-all;">%s</span>
                      </div>
                    </div>
                    
                    <!-- QR Code Section -->
                    <div class="qr-section">
                      <img src="data:image/png;base64,%s" alt="QR код" />
                      <p class="qr-note">Покажите QR-код или распечатанный билет на входе</p>
                    </div>
                    
                    <!-- Rules Section -->
                    <div class="rules-section">
                      <h2 class="rules-title">Правила посещения мероприятия</h2>
                      %s
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                fontCss,
                ORGANIZER_NAME,
                concertTitle,
                ticket.getId().toString().substring(0, 8).toUpperCase(),
                buyer,
                dateTime,
                venue,
                ORGANIZER_NAME,
                seat.getTableNumber(),
                seat.getChairNumber(),
                price,
                ticket.getTicketToken(),
                qrBase64,
                FULL_RULES
        );
    }

    private double centsToRubles(Seat seat) {
        Integer override = seat.getPriceOverrideCents();
        if (override != null) {
            return override / 100.0;
        }
        if (seat.getCategory() != null) {
            return seat.getCategory().getPriceCents() / 100.0;
        }
        return 0;
    }

    private String escape(String value) {
        return HtmlUtils.htmlEscapeDecimal(value != null ? value : "");
    }

    private String defaultValue(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }

    private String formatPrice(double rubles) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("ru-RU"));
        return formatter.format(rubles);
    }

    private String fontFaceCss() {
        if (cachedFontFaceCss != null) {
            return cachedFontFaceCss;
        }
        byte[] fontBytes = loadFontBytes();
        if (fontBytes == null) {
            if (!fontWarningLogged) {
                log.warn("Cyrillic-capable font not found. PDF may show placeholders for Russian text.");
                fontWarningLogged = true;
            }
            cachedFontFaceCss = "";
            return cachedFontFaceCss;
        }
        String base64 = Base64.getEncoder().encodeToString(fontBytes);
        cachedFontFaceCss = """
                @font-face {
                  font-family: 'TicketFont';
                  src: url('data:font/ttf;base64,%s') format('truetype');
                  font-weight: normal;
                  font-style: normal;
                }
                """.formatted(base64);
        return cachedFontFaceCss;
    }

    private byte[] loadFontBytes() {
        for (String candidate : FONT_CANDIDATE_PATHS) {
            Path path = Path.of(candidate);
            if (Files.exists(path)) {
                try {
                    log.info("Embedding font for PDF from {}", candidate);
                    return Files.readAllBytes(path);
                } catch (IOException e) {
                    log.warn("Unable to read font {}: {}", candidate, e.getMessage());
                }
            }
        }
        try (var resource = TicketPdfService.class.getResourceAsStream(BUNDLED_FONT)) {
            if (resource != null) {
                log.info("Embedding bundled font resource {}", BUNDLED_FONT);
                return resource.readAllBytes();
            }
        } catch (IOException e) {
            log.warn("Unable to read bundled font {}: {}", BUNDLED_FONT, e.getMessage());
        }
        return null;
    }
}
