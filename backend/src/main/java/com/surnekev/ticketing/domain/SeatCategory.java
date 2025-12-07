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

@Entity
@Table(name = "seat_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "price_cents")
    private int priceCents;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "color_hex", length = 7)
    private String colorHex;
}

