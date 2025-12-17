import axios from 'axios';
import type {
  Concert,
  Seat,
  ReservationResponse,
  Ticket,
  TicketStatus,
  SeatCategorySummary,
  SeatTableAssignment,
  AdminUser
} from '../types';

const api = axios.create({
  baseURL: '/api'
});

// Interceptor для автоматической передачи токена
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('ticketing-token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const fetchConcert = async (concertId: number) => {
  const { data } = await api.get<Concert>(`/concerts/${concertId}`);
  return data;
};

export const fetchSeats = async (concertId: number) => {
  const { data } = await api.get<Seat[]>(`/concerts/${concertId}/seats`);
  return data;
};

export interface CreateReservationPayload {
  concertId: number;
  seatIds: number[];
  buyerName: string;
  buyerPhone: string;
  buyerEmail: string;
}

export const createReservation = async (payload: CreateReservationPayload) => {
  const { data } = await api.post<ReservationResponse>('/reservations', payload);
  return data;
};

export interface LoginPayload {
  username: string;
  password: string;
}

export const login = async (payload: LoginPayload) => {
  const { data } = await api.post<{ accessToken: string; expiresIn: number }>('/auth/login', payload);
  return data;
};

export const fetchTickets = async (status?: TicketStatus) => {
  const { data } = await api.get<Ticket[]>('/admin/tickets', {
    params: status ? { status } : undefined
  });
  return data;
};

export const confirmTicket = async (ticketId: string) => {
  const { data } = await api.post<Ticket>(`/admin/tickets/${ticketId}/confirm`);
  return data;
};

export const confirmTicketsBulk = async (ticketIds: string[]) => {
  const { data } = await api.post<Ticket[]>('/admin/tickets/confirm', { ticketIds });
  return data;
};

export const cancelTicket = async (ticketId: string, reason: string) => {
  const { data } = await api.post<Ticket>(`/admin/tickets/${ticketId}/cancel`, {
    operator: 'admin-ui',
    reason
  });
  return data;
};

export const cancelTicketsBulk = async (ticketIds: string[], reason: string) => {
  const { data } = await api.post<Ticket[]>('/admin/tickets/cancel', {
    ticketIds,
    reason
  });
  return data;
};

export const fetchSeatCategories = async () => {
  const { data } = await api.get<SeatCategorySummary[]>('/admin/seat-config/categories');
  return data;
};

export const updateSeatCategory = async (
  categoryId: number,
  payload: { name: string; priceCents: number; description?: string | null; colorHex?: string | null }
) => {
  const { data } = await api.put<SeatCategorySummary>(`/admin/seat-config/categories/${categoryId}`, payload);
  return data;
};

export const fetchSeatTableAssignments = async (concertId?: number) => {
  const { data } = await api.get<SeatTableAssignment[]>('/admin/seat-config/tables', {
    params: concertId ? { concertId } : undefined
  });
  return data;
};

export const assignSeatCategory = async (payload: { seatCategoryId: number; tableNumbers: number[]; concertId?: number }) => {
  await api.post('/admin/seat-config/tables/assign-category', payload);
};

export const overrideSeatPrice = async (payload: {
  tableNumber: number;
  chairNumber?: number | null;
  priceCents?: number | null;
  concertId?: number;
}) => {
  await api.post('/admin/seat-config/tables/override-price', payload);
};

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export const changePassword = async (request: ChangePasswordRequest) => {
  await api.post('/admin/users/change-password', request);
};

export interface RegisterManagerRequest {
  username: string;
  password: string;
}

export interface ConfirmRegistrationRequest {
  username: string;
  verificationCode: string;
}

export const requestManagerRegistration = async (request: RegisterManagerRequest) => {
  const { data } = await api.post<{ message: string }>('/admin/registration/request', request);
  return data;
};

export const confirmManagerRegistration = async (request: ConfirmRegistrationRequest) => {
  const { data } = await api.post<{ message: string }>('/admin/registration/confirm', request);
  return data;
};

export const fetchAdminUsers = async () => {
  const { data } = await apiClient.get<AdminUser[]>('/admin/users');
  return data;
};

export const refundTicket = async (ticketId: string, reason?: string) => {
  const { data } = await apiClient.post<Ticket>(`/admin/tickets/${ticketId}/refund`, reason ? {
    reason: reason
  } : {});
  return data;
};

export const refundTicketsBulk = async (ticketIds: string[], reason?: string) => {
  const { data } = await apiClient.post<Ticket[]>('/admin/tickets/refund', {
    ticketIds,
    reason: reason || 'ticket refund'
  });
  return data;
};

export const searchTickets = async (phone?: string, name?: string, ticketId?: string) => {
  const { data } = await apiClient.get<Ticket[]>('/admin/tickets/search', {
    params: {
      ...(phone && { phone }),
      ...(name && { name }),
      ...(ticketId && { ticketId })
    }
  });
  return data;
};

export const fetchUsedTickets = async () => {
  const { data } = await apiClient.get<Ticket[]>('/admin/tickets/used');
  return data;
};

export const revertTicketStatus = async (ticketId: string) => {
  const { data } = await apiClient.post<Ticket>(`/admin/tickets/${ticketId}/revert`);
  return data;
};

export const apiClient = api;

