export type SeatStatus = 'AVAILABLE' | 'HELD' | 'SOLD' | 'BLOCKED';

export interface Seat {
  id: number;
  tableNumber: number;
  chairNumber: number;
  categoryName: string;
  categoryColorHex?: string | null;
  priceCents: number;
  priceOverrideCents?: number | null;
  status: SeatStatus;
  blockedReason?: string | null;
  holdExpiresAt?: string | null;
}

export type TicketStatus = 'RESERVED' | 'SOLD' | 'USED' | 'CANCELLED';

export interface Ticket {
  id: string;
  reservationId: string;
  status: TicketStatus;
  seat: Seat;
  buyerName: string;
  buyerPhone: string;
  qrCodeUrl?: string | null;
  issuedAt: string;
  checkedInAt?: string | null;
}

export interface Concert {
  id: number;
  title: string;
  slug: string;
  description: string;
  concertDate: string;
  eventStartTime?: string | null;
  guestStartTime?: string | null;
  venue: string;
  city?: string | null;
  currency?: string | null;
  ageRestriction?: string | null;
  eventType?: string | null;
  posterUrl?: string | null;
  salesSchemeUrl?: string | null;
  minTicketPriceCents?: number | null;
}

export interface CreateConcertRequest {
  title: string;
  slug: string;
  description?: string | null;
  concertDate: string;
  eventStartTime?: string | null;
  guestStartTime?: string | null;
  venue?: string | null;
  city?: string | null;
  currency?: string | null;
  ageRestriction?: string | null;
  eventType?: string | null;
  posterUrl?: string | null;
  salesSchemeUrl?: string | null;
}

export interface UpdateConcertRequest {
  title?: string | null;
  slug?: string | null;
  description?: string | null;
  concertDate?: string | null;
  eventStartTime?: string | null;
  guestStartTime?: string | null;
  venue?: string | null;
  city?: string | null;
  currency?: string | null;
  ageRestriction?: string | null;
  eventType?: string | null;
  posterUrl?: string | null;
  salesSchemeUrl?: string | null;
}

export interface ReservationResponse {
  reservationId: string;
  status: 'HELD' | 'PARTIALLY_CONFIRMED' | 'PARTIALLY_CANCELLED' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';
  expiresAt: string;
  seats: Seat[];
}

export interface SeatCategorySummary {
  id: number;
  name: string;
  priceCents: number;
  description?: string | null;
  colorHex?: string | null;
}

export interface SeatTableAssignment {
  tableNumber: number;
  seatCategoryId: number;
  seatCategoryName: string;
  basePriceCents: number;
  hasOverrides: boolean;
  overridePriceCents?: number | null;
  seatCategoryColorHex?: string | null;
}

export interface BulkCreateSeatsRequest {
  concertId: number;
  categoryId: number;
  tables: Array<{
    tableNumber: number;
    chairsCount: number;
  }>;
  danceFloor?: {
    capacity: number;
  };
}

export interface SeatStatusEvent {
  seatId: number;
  oldStatus: SeatStatus;
  newStatus: SeatStatus;
  expiresAt?: string | null;
}

export interface ReservationCreatedEvent {
  reservationId: string;
  seatIds: number[];
  expiresAt: string;
}

export type AdminRole = 'ADMIN' | 'MANAGER' | 'CHECKIN';

export interface AdminUser {
  id: string;
  username: string;
  roles: AdminRole[];
  enabled: boolean;
  createdAt: string;
}

