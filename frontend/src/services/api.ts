import axios from 'axios';
import type {
  Concert,
  CreateConcertRequest,
  UpdateConcertRequest,
  Seat,
  ReservationResponse,
  Ticket,
  TicketStatus,
  SeatCategorySummary,
  SeatCategoryWithAvailability,
  SeatTableAssignment,
  AdminUser,
  BulkCreateSeatsRequest
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

// Interceptor для обработки истекших токенов
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Токен истек или недействителен - очищаем его
      localStorage.removeItem('ticketing-token');
      // Для админских эндпоинтов перенаправляем на страницу входа
      if (error.config?.url?.startsWith('/admin')) {
        window.location.href = '/admin';
      }
    }
    return Promise.reject(error);
  }
);

export const fetchConcertsList = async () => {
  const { data } = await api.get<Concert[]>('/concerts');
  return data;
};

export const fetchConcert = async (concertId: number) => {
  const { data } = await api.get<Concert>(`/concerts/${concertId}`);
  return data;
};

export const fetchConcertBySlug = async (slug: string) => {
  // Добавляем timestamp для предотвращения кеширования
  const { data } = await api.get<Concert>(`/concerts/slug/${slug}`, {
    params: { _t: Date.now() }
  });
  return data;
};

export const fetchConcertCategories = async (concertId: number) => {
  const { data } = await api.get<SeatCategorySummary[]>(`/concerts/${concertId}/categories`);
  return data;
};

export const fetchConcertCategoriesWithAvailability = async (concertId: number) => {
  const { data } = await api.get<SeatCategoryWithAvailability[]>(`/concerts/${concertId}/categories-with-availability`);
  return data;
};

export const fetchConcertCategoriesWithAvailabilityBySlug = async (slug: string) => {
  const { data } = await api.get<SeatCategoryWithAvailability[]>(`/concerts/slug/${slug}/categories-with-availability`);
  return data;
};

export const fetchSeats = async (concertId: number) => {
  const { data } = await api.get<Seat[]>(`/concerts/${concertId}/seats`);
  return data;
};

export interface CreateReservationPayload {
  concertId: number;
  seatIds?: number[];
  categoryQuantities?: Array<{ categoryId: number; quantity: number }>;
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

export const createSeatCategory = async (
  payload: { name: string; priceCents: number; description?: string | null; colorHex?: string | null }
) => {
  const { data } = await api.post<SeatCategorySummary>('/admin/seat-config/categories', payload);
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
  tableNumber?: number;
  chairNumber?: number | null;
  priceCents?: number | null;
  concertId?: number;
  seatId?: number;
}) => {
  await api.post('/admin/seat-config/tables/override-price', payload);
};

export const bulkCreateSeats = async (payload: BulkCreateSeatsRequest) => {
  const { data } = await api.post<{ created: number; message: string }>('/admin/seat-config/seats/bulk-create', payload);
  return data;
};

export interface SimpleCreateSeatsPayload {
  concertId: number;
  categories: Array<{ categoryId: number; quantity: number }>;
}

export const simpleCreateSeats = async (payload: SimpleCreateSeatsPayload) => {
  const { data } = await api.post<{ created: number; message: string }>('/admin/seat-config/seats/simple-create', payload);
  return data;
};

export interface AddCategorySeatsPayload {
  concertId: number;
  categoryId: number;
  quantity: number;
}

export const addCategorySeats = async (payload: AddCategorySeatsPayload) => {
  const { data } = await api.post<{ created: number; message: string }>('/admin/seat-config/seats/add-category', payload);
  return data;
};

export const blockSeat = async (seatId: number) => {
  await api.post(`/admin/seat-config/seats/${seatId}/block`);
};

export const unblockSeat = async (seatId: number) => {
  await api.post(`/admin/seat-config/seats/${seatId}/unblock`);
};

export const deleteSeat = async (seatId: number) => {
  await api.delete(`/admin/seat-config/seats/${seatId}`);
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

// Partner Request API
export const submitPartnerRequest = async (request: {
  fullName: string;
  company: string;
  phone: string;
  email: string;
}) => {
  const { data } = await apiClient.post<{ message: string }>('/partner-request', request);
  return data;
};

// Promo Code API
export interface PromoCodeDto {
  id: number;
  code: string;
  discountPercent: number;
  applicableCategoryIds: number[] | null;
  active: boolean;
  validFrom: string; // ISO 8601 date string
  validTo: string | null; // ISO 8601 date string
  usageLimit: number | null;
  usedCount: number;
  createdAt: string; // ISO 8601 date string
  updatedAt: string; // ISO 8601 date string
}

export interface CreatePromoCodeRequest {
  code: string;
  discountPercent: number;
  applicableCategoryIds: number[] | null;
  active: boolean;
  validFrom: string | null; // ISO 8601 date string
  validTo: string | null; // ISO 8601 date string
  usageLimit: number | null;
}

export interface UpdatePromoCodeRequest {
  code?: string | null;
  discountPercent?: number | null;
  applicableCategoryIds?: number[] | null;
  active?: boolean | null;
  validFrom?: string | null; // ISO 8601 date string
  validTo?: string | null; // ISO 8601 date string
  usageLimit?: number | null;
}

export const fetchPromoCodes = async () => {
  const { data } = await api.get<PromoCodeDto[]>('/promo-codes/admin');
  return data;
};

export const createPromoCode = async (request: CreatePromoCodeRequest) => {
  const { data } = await api.post<PromoCodeDto>('/promo-codes/admin', request);
  return data;
};

export const updatePromoCode = async (id: number, request: UpdatePromoCodeRequest) => {
  const { data } = await api.put<PromoCodeDto>(`/promo-codes/admin/${id}`, request);
  return data;
};

export const deletePromoCode = async (id: number) => {
  await api.delete(`/promo-codes/admin/${id}`);
};

// Admin Concert API
export const fetchAllConcerts = async () => {
  const { data } = await api.get<Concert[]>('/admin/concerts');
  return data;
};

export const createConcert = async (request: CreateConcertRequest) => {
  const { data } = await api.post<Concert>('/admin/concerts', request);
  return data;
};

export const updateConcert = async (id: number, request: UpdateConcertRequest) => {
  const { data } = await api.put<Concert>(`/admin/concerts/${id}`, request);
  return data;
};

export const deleteConcert = async (id: number) => {
  await api.delete(`/admin/concerts/${id}`);
};

export const uploadPoster = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  const { data } = await api.post<{ url: string }>('/admin/concerts/upload/poster', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return data.url;
};

export const uploadScheme = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  const { data } = await api.post<{ url: string }>('/admin/concerts/upload/scheme', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return data.url;
};

export const apiClient = api;

