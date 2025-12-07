package com.surnekev.ticketing.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_phone")
    private String buyerPhone;

    @Column(name = "ticket_token")
    private String ticketToken;

    @Column(name = "qr_code_url", columnDefinition = "text")
    private String qrCodeUrl;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "checked_in_at")
    private Instant checkedInAt;

    @Column(name = "checked_in_by")
    private String checkedInBy;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "pdf_document", columnDefinition = "bytea")
    private byte[] pdfDocument;
}

