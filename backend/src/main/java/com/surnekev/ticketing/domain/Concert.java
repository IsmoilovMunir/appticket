package com.surnekev.ticketing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "concerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "concert_date")
    private Instant concertDate;

    @Column(name = "event_start_time")
    private Instant eventStartTime;

    @Column(name = "guest_start_time")
    private Instant guestStartTime;

    private String venue;

    @Column(name = "city")
    private String city;

    @Column(name = "venue_lat")
    private Double venueLat;

    @Column(name = "venue_lon")
    private Double venueLon;

    @Column(name = "currency")
    private String currency;

    @Column(name = "age_restriction")
    private String ageRestriction;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "sales_scheme_url", columnDefinition = "TEXT")
    private String salesSchemeUrl;

    @Column(name = "simple_mode", nullable = false)
    @Builder.Default
    private boolean simpleMode = false;

    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Telegram Chat IDs менеджеров концерта (через запятую).
     * Подписавшиеся получают уведомления о бронях и статусах этого концерта.
     */
    @Column(name = "telegram_manager_chat_ids", length = 500)
    private String telegramManagerChatIds;
}

