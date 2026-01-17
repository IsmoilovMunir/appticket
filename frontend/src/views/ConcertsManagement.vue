<template>
  <div class="concerts-management">
    <div class="container-fluid py-4">
      <!-- Отладочная информация -->
      <div v-if="false" class="alert alert-info">
        Страница управления концертами загружена
      </div>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="h4 mb-1">
            <i class="bi bi-music-note-beamed me-2"></i>
            Управление концертами
          </h2>
          <p class="text-body-secondary small mb-0">Создание и редактирование концертов</p>
        </div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-primary" :disabled="loading" @click="loadConcerts">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            <i class="bi bi-arrow-clockwise me-1"></i>
            Обновить
          </button>
          <button class="btn btn-primary" @click="showCreateForm = true">
            <i class="bi bi-plus-lg me-2"></i>
            Создать концерт
          </button>
        </div>
      </div>

      <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
        <div class="d-flex align-items-start">
          <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
          <div class="flex-grow-1">
            <strong>Ошибка доступа</strong>
            <p class="mb-0 mt-1">{{ error }}</p>
            <div v-if="error.includes('403') || error.includes('Доступ запрещен')" class="mt-2">
              <small>
                <strong>Решение:</strong> Войдите под учетной записью с ролью <strong>ADMIN</strong> или <strong>MANAGER</strong>.
                Роль <strong>CHECKIN</strong> не имеет доступа к управлению концертами.
              </small>
            </div>
          </div>
          <button type="button" class="btn-close" @click="error = ''"></button>
        </div>
      </div>

      <div v-if="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle me-2"></i>
        {{ successMessage }}
        <button type="button" class="btn-close" @click="successMessage = ''"></button>
      </div>

      <!-- Форма создания/редактирования -->
      <div v-if="showCreateForm || editingConcert" class="card mb-4">
        <div class="card-header bg-primary text-white">
          <h5 class="mb-0">
            <i class="bi bi-music-note-beamed me-2"></i>
            {{ editingConcert ? 'Редактировать концерт' : 'Создать новый концерт' }}
          </h5>
        </div>
        <div class="card-body">
          <form @submit.prevent="saveConcert">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">Название концерта *</label>
                <input
                  type="text"
                  v-model="concertForm.title"
                  @input="handleTitleChange"
                  class="form-control"
                  required
                  placeholder="Введите название концерта"
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Slug (URL) *</label>
                <input
                  type="text"
                  v-model="concertForm.slug"
                  class="form-control"
                  required
                  pattern="[a-z0-9-]+"
                  placeholder="name-concert"
                />
                <small class="text-body-secondary">Только латинские буквы, цифры и дефисы</small>
              </div>
              <div class="col-12">
                <label class="form-label">Описание</label>
                <textarea
                  v-model="concertForm.description"
                  class="form-control"
                  rows="4"
                  placeholder="Описание концерта"
                ></textarea>
              </div>
              <div class="col-md-4">
                <label class="form-label">Дата и время концерта *</label>
                <input
                  type="datetime-local"
                  v-model="concertForm.concertDate"
                  class="form-control"
                  required
                />
              </div>
              <div class="col-md-4">
                <label class="form-label">Дата и время начала события</label>
                <input
                  type="datetime-local"
                  v-model="concertForm.eventStartTime"
                  class="form-control"
                />
              </div>
              <div class="col-md-4">
                <label class="form-label">Дата и время начала запуска гостей</label>
                <input
                  type="datetime-local"
                  v-model="concertForm.guestStartTime"
                  class="form-control"
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Место проведения</label>
                <input
                  type="text"
                  v-model="concertForm.venue"
                  class="form-control"
                  placeholder="Название места"
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Город мероприятия</label>
                <input
                  type="text"
                  v-model="concertForm.city"
                  class="form-control"
                  placeholder="Город"
                />
              </div>
              <div class="col-md-4">
                <label class="form-label">Валюта</label>
                <select v-model="concertForm.currency" class="form-select">
                  <option value="RUB">RUB</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
              <div class="col-md-4">
                <label class="form-label">Возрастное ограничение</label>
                <input
                  type="text"
                  v-model="concertForm.ageRestriction"
                  class="form-control"
                  placeholder="18+"
                />
              </div>
              <div class="col-md-4">
                <label class="form-label">Тип мероприятия</label>
                <input
                  type="text"
                  v-model="concertForm.eventType"
                  class="form-control"
                  placeholder="Концерт"
                />
              </div>
              <div class="col-12">
                <label class="form-label">Афиша</label>
                <div v-if="concertForm.posterUrl" class="mb-2">
                  <img :src="concertForm.posterUrl" alt="Poster" style="max-width: 300px; max-height: 300px;" class="img-thumbnail" />
                  <button type="button" class="btn btn-sm btn-outline-danger ms-2" @click="concertForm.posterUrl = ''">
                    <i class="bi bi-trash"></i> Удалить
                  </button>
                </div>
                <input
                  type="file"
                  accept="image/*"
                  @change="handlePosterUpload"
                  class="form-control"
                  :disabled="posterUploading"
                />
                <small v-if="posterUploading" class="text-body-secondary">Загрузка...</small>
              </div>
              <div class="col-12">
                <label class="form-label">Схема продаж (SVG)</label>
                <div v-if="concertForm.salesSchemeUrl" class="mb-2">
                  <a :href="concertForm.salesSchemeUrl" target="_blank" class="btn btn-sm btn-outline-primary me-2">
                    <i class="bi bi-eye me-1"></i>
                    Просмотреть схему
                  </a>
                  <button type="button" class="btn btn-sm btn-outline-info me-2" @click="showSchemeEditor = true">
                    <i class="bi bi-pencil me-1"></i>
                    Инструкция по разметке
                  </button>
                  <button type="button" class="btn btn-sm btn-outline-danger" @click="concertForm.salesSchemeUrl = ''">
                    <i class="bi bi-trash"></i> Удалить
                  </button>
                </div>
                <input
                  type="file"
                  accept=".svg,image/svg+xml"
                  @change="handleSchemeUpload"
                  class="form-control"
                  :disabled="schemeUploading"
                />
                <small v-if="schemeUploading" class="text-body-secondary">Загрузка...</small>
                <small class="text-body-secondary d-block">Только SVG файлы. После загрузки добавьте атрибуты data-table и data-chair к элементам схемы.</small>
              </div>
            </div>
            <div class="mt-4 d-flex gap-2">
              <button type="submit" class="btn btn-primary" :disabled="saving">
                <span v-if="saving" class="spinner-border spinner-border-sm me-2"></span>
                <i class="bi bi-check-lg me-1"></i>
                {{ editingConcert ? 'Сохранить изменения' : 'Создать концерт' }}
              </button>
              <button type="button" class="btn btn-outline-secondary" @click="cancelEdit">
                <i class="bi bi-x-lg me-1"></i>
                Отмена
              </button>
            </div>
            <p v-if="saveError" class="text-danger small mt-2 mb-0">
              <i class="bi bi-exclamation-circle me-1"></i>{{ saveError }}
            </p>
          </form>
        </div>
      </div>

      <!-- Форма создания мест -->
      <div v-if="showCreateSeatsForm && selectedConcertForSeats" class="card mb-4">
        <div class="card-header bg-success text-white">
          <h5 class="mb-0">
            <i class="bi bi-grid-3x3-gap me-2"></i>
            Управление местами для концерта: {{ selectedConcertForSeats.title }}
            <span v-if="!existingSeatsLoading && existingSeats.length > 0" class="badge bg-light text-dark ms-2">
              {{ existingSeats.length }} мест
            </span>
          </h5>
        </div>
        <div class="card-body">
          <div v-if="seatCategories.length === 0 && !seatCategoriesLoading" class="alert alert-warning">
            <i class="bi bi-exclamation-triangle me-2"></i>
            Сначала создайте категории мест в разделе "Настройка мест" в админ-панели.
            <div class="mt-3">
              <button
                class="btn btn-primary btn-sm me-2"
                @click="router.push({ path: '/admin', query: { section: 'seat-config' } })"
              >
                <i class="bi bi-gear me-2"></i>
                Перейти к настройке мест
              </button>
              <button
                class="btn btn-outline-secondary btn-sm"
                @click="loadSeatCategories"
                :disabled="seatCategoriesLoading"
              >
                <span v-if="seatCategoriesLoading" class="spinner-border spinner-border-sm me-2"></span>
                <i class="bi bi-arrow-repeat me-2"></i>
                Обновить категории
              </button>
            </div>
          </div>

          <!-- Существующие места -->
          <div v-if="!existingSeatsLoading && existingSeats.length > 0" class="mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="mb-0">
                <button
                  class="btn btn-link p-0 text-decoration-none fw-bold"
                  @click="showExistingSeats = !showExistingSeats"
                >
                  <i class="bi me-2" :class="showExistingSeats ? 'bi-chevron-down' : 'bi-chevron-right'"></i>
                  <i class="bi bi-check-circle-fill text-success me-2"></i>
                  Существующие места ({{ existingSeats.length }})
                </button>
              </h5>
              <div class="d-flex gap-2">
                <button
                  class="btn btn-outline-primary btn-sm"
                  @click="loadExistingSeats(selectedConcertForSeats!.id)"
                  :disabled="existingSeatsLoading"
                >
                  <span v-if="existingSeatsLoading" class="spinner-border spinner-border-sm me-1"></span>
                  <i class="bi bi-arrow-clockwise me-1"></i>
                  Обновить
                </button>
              </div>
            </div>

            <!-- Содержимое секции (показывается только если развернуто) -->
            <div v-show="showExistingSeats">

            <div class="table-responsive">
              <table class="table table-sm">
                <thead class="table-light">
                  <tr>
                    <th>Тип</th>
                    <th>Таблица</th>
                    <th>Место</th>
                    <th>Категория</th>
                    <th>Цена</th>
                    <th>Статус</th>
                    <th>Действия</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="seat in existingSeats" :key="seat.id">
                    <td>
                      <span v-if="seat.tableNumber === 0" class="badge bg-warning">
                        <i class="bi bi-music-note me-1"></i>Танцпол
                      </span>
                      <span v-else class="badge bg-primary">
                        <i class="bi bi-table me-1"></i>Стол
                      </span>
                    </td>
                    <td>{{ seat.tableNumber === 0 ? '-' : seat.tableNumber }}</td>
                    <td>{{ seat.chairNumber }}</td>
                    <td>{{ seat.categoryName }}</td>
                    <td>{{ formatPrice(seat.priceCents) }}</td>
                    <td>
                      <span class="badge"
                            :class="{
                              'bg-success': seat.status === 'AVAILABLE',
                              'bg-warning': seat.status === 'HELD',
                              'bg-danger': seat.status === 'SOLD',
                              'bg-secondary': seat.status === 'BLOCKED'
                            }">
                        {{ seat.status === 'AVAILABLE' ? 'Свободно' :
                           seat.status === 'HELD' ? 'Забронировано' :
                           seat.status === 'SOLD' ? 'Продано' : 'Заблокировано' }}
                      </span>
                    </td>
                    <td>
                      <button
                        class="btn btn-sm btn-outline-danger"
                        @click="handleDeleteSeat(seat)"
                        :disabled="deletingSeats[seat.id] || seat.status === 'SOLD'"
                        :title="seat.status === 'SOLD' ? 'Нельзя удалить проданное место' : 'Удалить место'"
                      >
                        <span v-if="deletingSeats[seat.id]" class="spinner-border spinner-border-sm me-1"></span>
                        <i class="bi bi-trash" v-else></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Статистика -->
            <div class="row g-3 mt-2">
              <div class="col-md-3">
                <div class="card border-success">
                  <div class="card-body p-2 text-center">
                    <div class="h6 mb-0">{{ existingSeats.filter(s => s.status === 'AVAILABLE').length }}</div>
                    <small class="text-success">Свободно</small>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card border-warning">
                  <div class="card-body p-2 text-center">
                    <div class="h6 mb-0">{{ existingSeats.filter(s => s.status === 'HELD').length }}</div>
                    <small class="text-warning">Забронировано</small>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card border-danger">
                  <div class="card-body p-2 text-center">
                    <div class="h6 mb-0">{{ existingSeats.filter(s => s.status === 'SOLD').length }}</div>
                    <small class="text-danger">Продано</small>
                  </div>
                </div>
              </div>
              <div class="col-md-3">
                <div class="card border-secondary">
                  <div class="card-body p-2 text-center">
                    <div class="h6 mb-0">{{ existingSeats.filter(s => s.status === 'BLOCKED').length }}</div>
                    <small class="text-secondary">Заблокировано</small>
                  </div>
                </div>
              </div>
            </div>
            </div> <!-- Закрываем сворачиваемую секцию -->
          </div>

          <div v-else-if="!existingSeatsLoading && existingSeats.length === 0" class="alert alert-info mb-4">
            <i class="bi bi-info-circle me-2"></i>
            Для этого концерта еще не создано ни одного места. Создайте места ниже.
          </div>

          <div v-else class="text-center mb-4">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Загрузка существующих мест...</span>
            </div>
          </div>

          <form v-if="seatCategories.length > 0" @submit.prevent="createSeatsForConcert">
            <div class="mb-3">
              <label class="form-label">Категория мест *</label>
              <select v-model="seatsForm.categoryId" class="form-select" required>
                <option value="">Выберите категорию</option>
                <option v-for="category in seatCategories" :key="category.id" :value="category.id">
                  {{ category.name }}{{ category.priceCents != null && !isNaN(category.priceCents) ? ' - ' + formatPrice(category.priceCents) : '' }}
                </option>
              </select>
            </div>
            <!-- Танцпол -->
            <div class="mb-3">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <label class="form-label mb-0">Танцпол</label>
                <button 
                  type="button" 
                  class="btn btn-sm"
                  :class="showDanceFloor ? 'btn-outline-danger' : 'btn-outline-success'"
                  @click="toggleDanceFloor"
                >
                  <i class="bi" :class="showDanceFloor ? 'bi-x-lg' : 'bi-plus-lg'"></i>
                  {{ showDanceFloor ? 'Убрать танцпол' : 'Добавить танцпол' }}
                </button>
              </div>
              <div v-if="showDanceFloor" class="card border-success">
                <div class="card-body">
                  <div class="alert alert-info small mb-3">
                    <i class="bi bi-info-circle me-1"></i>
                    <strong>Информация:</strong> Все круги в SVG схеме с атрибутом
                    <code>data-table="0"</code> автоматически считаются танцполом.
                    <code>data-seat-id</code> нужен только для визуального различения кругов.
                  </div>
                  <div class="mb-2">
                    <label class="form-label small">Количество мест на танцполе *</label>
                    <input 
                      type="number" 
                      v-model.number="danceFloorCapacity" 
                      class="form-control form-control-sm" 
                      min="1" 
                      placeholder="250"
                      required
                    />
                    <small class="text-body-secondary">Места будут созданы с tableNumber = 0 и присвоены к указанному кружку танцпола</small>
                  </div>
                  <button
                    type="button"
                    class="btn btn-sm btn-success w-100 mt-2"
                    :disabled="!seatsForm.categoryId || !danceFloorCapacity || creatingSeats"
                    @click="createDanceFloorOnly"
                  >
                    <span v-if="creatingSeats" class="spinner-border spinner-border-sm me-2"></span>
                    <i class="bi bi-plus-circle me-1"></i>
                    Создать только танцпол
                  </button>
                </div>
              </div>
            </div>

            <!-- Конфигурация столов -->
            <div class="mb-3">
              <label class="form-label">Конфигурация столов</label>
              <div class="table-responsive">
                <table class="table table-sm">
                  <thead>
                    <tr>
                      <th>Номер стола</th>
                      <th>Количество стульев</th>
                      <th>Действия</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(table, index) in seatsForm.tables" :key="index">
                      <td>
                        <input 
                          type="number" 
                          v-model.number="table.tableNumber" 
                          class="form-control form-control-sm" 
                          min="1" 
                          required
                        />
                      </td>
                      <td>
                        <input 
                          type="number" 
                          v-model.number="table.chairsCount" 
                          class="form-control form-control-sm" 
                          min="1" 
                          required
                        />
                      </td>
                      <td>
                        <button 
                          type="button" 
                          class="btn btn-sm btn-outline-danger" 
                          @click="seatsForm.tables.splice(index, 1)"
                        >
                          <i class="bi bi-trash"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <button 
                type="button" 
                class="btn btn-sm btn-outline-primary mt-2" 
                @click="seatsForm.tables.push({ tableNumber: seatsForm.tables.length + 1, chairsCount: 6 })"
              >
                <i class="bi bi-plus-lg me-1"></i>
                Добавить стол
              </button>
            </div>
            <div class="d-flex gap-2">
              <button type="submit" class="btn btn-success" :disabled="creatingSeats">
                <span v-if="creatingSeats" class="spinner-border spinner-border-sm me-2"></span>
                <i class="bi bi-check-lg me-1"></i>
                Создать места
              </button>
              <button type="button" class="btn btn-outline-secondary" @click="cancelCreateSeats">
                Отмена
              </button>
            </div>
            <p v-if="createSeatsError" class="text-danger small mt-2 mb-0">
              <i class="bi bi-exclamation-circle me-1"></i>{{ createSeatsError }}
            </p>
          </form>
        </div>
      </div>

      <!-- Список концертов -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">Список концертов</h5>
        </div>
        <div class="card-body">
          <div v-if="loading && concerts.length === 0" class="text-center py-5">
            <div class="spinner-border" role="status">
              <span class="visually-hidden">Загрузка...</span>
            </div>
          </div>
          <div v-else-if="concerts.length === 0" class="text-center py-5 text-body-secondary">
            <i class="bi bi-music-note-beamed" style="font-size: 3rem;"></i>
            <p class="mt-3">Концерты не найдены</p>
            <button class="btn btn-primary" @click="showCreateForm = true">
              <i class="bi bi-plus-lg me-2"></i>
              Создать первый концерт
            </button>
          </div>
          <div v-else class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Название</th>
                  <th>Slug</th>
                  <th>Город</th>
                  <th>Место</th>
                  <th>Дата</th>
                  <th>Действия</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="concert in concerts" :key="concert.id">
                  <td>
                    <div class="fw-semibold">{{ concert.title }}</div>
                    <small class="text-body-secondary">{{ concert.eventType || 'Концерт' }}</small>
                  </td>
                  <td>
                    <code class="small">/{{ concert.slug }}</code>
                  </td>
                  <td>{{ concert.city || '-' }}</td>
                  <td>{{ concert.venue || '-' }}</td>
                  <td>
                    <div v-if="concert.concertDate">
                      {{ formatDate(new Date(concert.concertDate)) }}
                    </div>
                    <span v-else class="text-body-secondary">-</span>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-primary" @click="editConcert(concert)" title="Редактировать">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-outline-success" @click="openCreateSeatsForm(concert)" title="Создать места">
                        <i class="bi bi-grid-3x3-gap"></i>
                      </button>
                      <button class="btn btn-outline-danger" @click="deleteConcertHandler(concert.id)" title="Удалить">
                        <i class="bi bi-trash"></i>
                      </button>
                      <a :href="`/${concert.slug}`" target="_blank" class="btn btn-outline-info" title="Открыть на сайте">
                        <i class="bi bi-box-arrow-up-right"></i>
                      </a>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchAllConcerts, createConcert, updateConcert, deleteConcert, uploadPoster, uploadScheme, bulkCreateSeats, fetchSeatCategories, fetchSeats, deleteSeat } from '../services/api';
import type { Concert, CreateConcertRequest, UpdateConcertRequest, SeatCategorySummary, BulkCreateSeatsRequest, Seat } from '../types';
import SchemeEditor from '../components/SchemeEditor.vue';

const router = useRouter();

const concerts = ref<Concert[]>([]);
const loading = ref(false);
const error = ref('');
const seatCategoriesLoading = ref(false);
const successMessage = ref('');
const showCreateForm = ref(false);
const editingConcert = ref<Concert | null>(null);
const saving = ref(false);
const saveError = ref('');
const posterUploading = ref(false);
const schemeUploading = ref(false);
const showSchemeEditor = ref(false);
const showCreateSeatsForm = ref(false);
const selectedConcertForSeats = ref<Concert | null>(null);
const seatCategories = ref<SeatCategorySummary[]>([]);

// Функция загрузки категорий мест
const loadSeatCategories = async () => {
  seatCategoriesLoading.value = true;
  try {
    const categories = await fetchSeatCategories();
    seatCategories.value = categories.map(cat => ({
      ...cat,
      priceCents: typeof cat.priceCents === 'number' ? cat.priceCents : (cat.priceCents != null ? Number(cat.priceCents) : 0)
    }));
  } catch (err: any) {
    console.error('Error loading seat categories:', err);
    // Не показываем ошибку, просто оставляем пустым
  } finally {
    seatCategoriesLoading.value = false;
  }
};

const seatsForm = ref<BulkCreateSeatsRequest>({
  concertId: 0,
  categoryId: 0,
  tables: [],
  danceFloor: undefined
});
const showDanceFloor = ref(false);
const danceFloorCapacity = ref(250);
const creatingSeats = ref(false);
const createSeatsError = ref('');
const existingSeats = ref<Seat[]>([]);
const existingSeatsLoading = ref(false);
const showExistingSeats = ref(true);
const deletingSeats = ref<Record<number, boolean>>({});

const concertForm = ref<CreateConcertRequest & { id?: number }>({
  title: '',
  slug: '',
  description: '',
  concertDate: '',
  eventStartTime: '',
  guestStartTime: '',
  venue: '',
  city: '',
  currency: 'RUB',
  ageRestriction: '',
  eventType: '',
  posterUrl: '',
  salesSchemeUrl: ''
});

const loadConcerts = async () => {
  loading.value = true;
  error.value = '';
  
  // Проверяем наличие токена
  const token = localStorage.getItem('ticketing-token');
  if (!token) {
    error.value = 'Требуется авторизация. Пожалуйста, войдите в систему.';
    loading.value = false;
    return;
  }
  
  try {
    concerts.value = await fetchAllConcerts();
  } catch (err: any) {
    console.error('Error loading concerts:', err);
    console.error('Response status:', err.response?.status);
    console.error('Response data:', err.response?.data);
    
    if (err.response?.status === 403) {
      error.value = 'Доступ запрещен. Для управления концертами требуется роль ADMIN или MANAGER. ' +
                    'Текущий пользователь не имеет необходимых прав. Обратитесь к администратору для получения доступа.';
    } else if (err.response?.status === 401) {
      error.value = 'Сессия истекла. Пожалуйста, войдите в систему снова.';
      // Очищаем токен и перенаправляем на страницу входа
      localStorage.removeItem('ticketing-token');
      setTimeout(() => {
        window.location.href = '/admin';
      }, 2000);
    } else {
      error.value = err.response?.data?.error || err.message || 'Не удалось загрузить концерты';
    }
  } finally {
    loading.value = false;
  }
};

const editConcert = (concert: Concert) => {
  editingConcert.value = concert;
  concertForm.value = {
    title: concert.title,
    slug: concert.slug,
    description: concert.description || '',
    concertDate: concert.concertDate ? new Date(concert.concertDate).toISOString().slice(0, 16) : '',
    eventStartTime: concert.eventStartTime ? new Date(concert.eventStartTime).toISOString().slice(0, 16) : '',
    guestStartTime: concert.guestStartTime ? new Date(concert.guestStartTime).toISOString().slice(0, 16) : '',
    venue: concert.venue || '',
    city: concert.city || '',
    currency: concert.currency || 'RUB',
    ageRestriction: concert.ageRestriction || '',
    eventType: concert.eventType || '',
    posterUrl: concert.posterUrl || '',
    salesSchemeUrl: concert.salesSchemeUrl || '',
    id: concert.id
  };
  showCreateForm.value = true;
  // Прокручиваем к форме
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const cancelEdit = () => {
  showCreateForm.value = false;
  editingConcert.value = null;
  concertForm.value = {
    title: '',
    slug: '',
    description: '',
    concertDate: '',
    eventStartTime: '',
    guestStartTime: '',
    venue: '',
    city: '',
    currency: 'RUB',
    ageRestriction: '',
    eventType: '',
    posterUrl: '',
    salesSchemeUrl: ''
  };
  saveError.value = '';
  successMessage.value = '';
};

const generateSlug = (title: string) => {
  return title
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');
};

const handleTitleChange = () => {
  if (!editingConcert.value && !concertForm.value.slug) {
    concertForm.value.slug = generateSlug(concertForm.value.title);
  }
};

const handlePosterUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;
  
  posterUploading.value = true;
  saveError.value = '';
  try {
    const url = await uploadPoster(file);
    concertForm.value.posterUrl = url;
    successMessage.value = 'Афиша успешно загружена';
    setTimeout(() => successMessage.value = '', 3000);
  } catch (err: any) {
    saveError.value = 'Не удалось загрузить афишу: ' + (err.response?.data?.error || err.message);
  } finally {
    posterUploading.value = false;
    input.value = '';
  }
};

const handleSchemeUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;
  
  if (!file.name.toLowerCase().endsWith('.svg')) {
    saveError.value = 'Только SVG файлы разрешены для схемы';
    return;
  }
  
  schemeUploading.value = true;
  saveError.value = '';
  try {
    const url = await uploadScheme(file);
    concertForm.value.salesSchemeUrl = url;
    successMessage.value = 'Схема успешно загружена';
    setTimeout(() => successMessage.value = '', 3000);
  } catch (err: any) {
    saveError.value = 'Не удалось загрузить схему: ' + (err.response?.data?.error || err.message);
  } finally {
    schemeUploading.value = false;
    input.value = '';
  }
};

const saveConcert = async () => {
  if (!concertForm.value.title || !concertForm.value.slug) {
    saveError.value = 'Название и slug обязательны';
    return;
  }
  
  saving.value = true;
  saveError.value = '';
  successMessage.value = '';
  
  try {
    if (editingConcert.value) {
      const updateData: UpdateConcertRequest = {
        title: concertForm.value.title,
        slug: concertForm.value.slug,
        description: concertForm.value.description || null,
        concertDate: concertForm.value.concertDate ? new Date(concertForm.value.concertDate).toISOString() : null,
        eventStartTime: concertForm.value.eventStartTime ? new Date(concertForm.value.eventStartTime).toISOString() : null,
        guestStartTime: concertForm.value.guestStartTime ? new Date(concertForm.value.guestStartTime).toISOString() : null,
        venue: concertForm.value.venue || null,
        city: concertForm.value.city || null,
        currency: concertForm.value.currency || null,
        ageRestriction: concertForm.value.ageRestriction || null,
        eventType: concertForm.value.eventType || null,
        posterUrl: concertForm.value.posterUrl || null,
        salesSchemeUrl: concertForm.value.salesSchemeUrl || null
      };
      await updateConcert(editingConcert.value.id, updateData);
      successMessage.value = 'Концерт успешно обновлен';
    } else {
      const createData: CreateConcertRequest = {
        title: concertForm.value.title,
        slug: concertForm.value.slug,
        description: concertForm.value.description || null,
        concertDate: new Date(concertForm.value.concertDate).toISOString(),
        eventStartTime: concertForm.value.eventStartTime ? new Date(concertForm.value.eventStartTime).toISOString() : null,
        guestStartTime: concertForm.value.guestStartTime ? new Date(concertForm.value.guestStartTime).toISOString() : null,
        venue: concertForm.value.venue || null,
        city: concertForm.value.city || null,
        currency: concertForm.value.currency || null,
        ageRestriction: concertForm.value.ageRestriction || null,
        eventType: concertForm.value.eventType || null,
        posterUrl: concertForm.value.posterUrl || null,
        salesSchemeUrl: concertForm.value.salesSchemeUrl || null
      };
      await createConcert(createData);
      successMessage.value = 'Концерт успешно создан';
    }
    
    await loadConcerts();
    cancelEdit();
    
    setTimeout(() => {
      successMessage.value = '';
    }, 5000);
  } catch (err: any) {
    console.error('Error saving concert:', err);
    const errorMessage = err.response?.data?.error || err.response?.data?.message || err.message || 'Не удалось сохранить концерт';
    saveError.value = errorMessage;
  } finally {
    saving.value = false;
  }
};

const deleteConcertHandler = async (id: number) => {
  if (!confirm('Вы уверены, что хотите удалить этот концерт? Это действие нельзя отменить.')) {
    return;
  }
  
  try {
    await deleteConcert(id);
    successMessage.value = 'Концерт успешно удален';
    await loadConcerts();
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (err: any) {
    console.error('Error deleting concert:', err);
    error.value = err.response?.data?.error || err.message || 'Не удалось удалить концерт';
  }
};

const formatDate = (date: Date) => {
  return new Intl.DateTimeFormat('ru-RU', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

const formatPrice = (cents: number | null | undefined) => {
  if (cents == null || cents === undefined || isNaN(Number(cents)) || cents < 0) {
    return '';
  }
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB'
  }).format(Number(cents) / 100);
};

const toggleDanceFloor = () => {
  showDanceFloor.value = !showDanceFloor.value;
  if (!showDanceFloor.value) {
    seatsForm.value.danceFloor = undefined;
    danceFloorCapacity.value = 250;
  }
};

const loadExistingSeats = async (concertId: number) => {
  existingSeatsLoading.value = true;
  try {
    existingSeats.value = await fetchSeats(concertId);
  } catch (err: any) {
    console.error('Error loading existing seats:', err);
    existingSeats.value = [];
  } finally {
    existingSeatsLoading.value = false;
  }
};

const handleDeleteSeat = async (seat: Seat) => {
  if (!confirm(`Вы уверены, что хотите удалить место ${seat.tableNumber === 0 ? 'Танцпол' : 'Стол ' + seat.tableNumber} №${seat.chairNumber}? Это действие нельзя отменить.`)) {
    return;
  }

  deletingSeats.value[seat.id] = true;
  try {
    await deleteSeat(seat.id);

    // Обновляем список мест
    if (selectedConcertForSeats.value) {
      await loadExistingSeats(selectedConcertForSeats.value.id);
    }

    successMessage.value = 'Место успешно удалено';
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);
  } catch (err: any) {
    console.error('Error deleting seat:', err);
    createSeatsError.value = err.response?.data?.error || err.message || 'Не удалось удалить место';
  } finally {
    deletingSeats.value[seat.id] = false;
  }
};

const openCreateSeatsForm = async (concert: Concert) => {
  selectedConcertForSeats.value = concert;
  showCreateSeatsForm.value = true;
  createSeatsError.value = '';
  showDanceFloor.value = false;
  danceFloorCapacity.value = 250;
  showExistingSeats.value = true;
  deletingSeats.value = {};
  seatsForm.value = {
    concertId: concert.id,
    categoryId: 0,
    tables: [{ tableNumber: 1, chairsCount: 6 }],
    danceFloor: undefined
  };

  // Загружаем существующие места
  await loadExistingSeats(concert.id);

  // Загружаем категории, если они еще не загружены
  if (seatCategories.value.length === 0) {
    await loadSeatCategories();
  }
};

const cancelCreateSeats = () => {
  showCreateSeatsForm.value = false;
  selectedConcertForSeats.value = null;
  createSeatsError.value = '';
  showDanceFloor.value = false;
  danceFloorCapacity.value = 250;
};

const createDanceFloorOnly = async () => {
  if (!seatsForm.value.categoryId) {
    createSeatsError.value = 'Выберите категорию мест';
    return;
  }

  if (!danceFloorCapacity.value || danceFloorCapacity.value < 1) {
    createSeatsError.value = 'Укажите количество мест на танцполе';
    return;
  }

  creatingSeats.value = true;
  createSeatsError.value = '';

  try {
    // Создаем только танцпол
    const requestData: BulkCreateSeatsRequest = {
      concertId: seatsForm.value.concertId,
      categoryId: seatsForm.value.categoryId,
      tables: [{
        tableNumber: 0,
        chairsCount: danceFloorCapacity.value
      }]
    };

    const result = await bulkCreateSeats(requestData);
    successMessage.value = `Успешно создано ${result.created} мест для танцпола`;

    // Перезагружаем существующие места
    if (selectedConcertForSeats.value) {
      await loadExistingSeats(selectedConcertForSeats.value.id);
    }

    setTimeout(() => {
      successMessage.value = '';
    }, 5000);
  } catch (err: any) {
    console.error('Error creating dance floor seats:', err);
    createSeatsError.value = err.response?.data?.error || err.message || 'Не удалось создать места для танцпола';
  } finally {
    creatingSeats.value = false;
  }
};

const createSeatsForConcert = async () => {
  if (!seatsForm.value.categoryId) {
    createSeatsError.value = 'Выберите категорию мест';
    return;
  }

  if (seatsForm.value.tables.length === 0 && !showDanceFloor.value) {
    createSeatsError.value = 'Добавьте хотя бы один стол или включите танцпол';
    return;
  }

  if (showDanceFloor.value) {
    // Проверка не нужна - все круги с data-table="0" автоматически считаются танцполом
    if (!danceFloorCapacity.value || danceFloorCapacity.value < 1) {
      createSeatsError.value = 'Укажите количество мест на танцполе';
      return;
    }
  }

  creatingSeats.value = true;
  createSeatsError.value = '';

  try {
    // Подготавливаем данные для отправки
    const requestData: BulkCreateSeatsRequest = {
      concertId: seatsForm.value.concertId,
      categoryId: seatsForm.value.categoryId,
      tables: [...seatsForm.value.tables]
    };

    // Если танцпол включен, добавляем его как стол с номером 0
    if (showDanceFloor.value && danceFloorCapacity.value > 0) {
      requestData.tables.push({
        tableNumber: 0,
        chairsCount: danceFloorCapacity.value
      });
    }

    const result = await bulkCreateSeats(requestData);
    successMessage.value = `Успешно создано ${result.created} мест`;

    // Перезагружаем существующие места
    if (selectedConcertForSeats.value) {
      await loadExistingSeats(selectedConcertForSeats.value.id);
    }

    setTimeout(() => {
      successMessage.value = '';
    }, 5000);
  } catch (err: any) {
    console.error('Error creating seats:', err);
    createSeatsError.value = err.response?.data?.error || err.message || 'Не удалось создать места';
  } finally {
    creatingSeats.value = false;
  }
};

onMounted(() => {
  // Проверяем наличие токена перед загрузкой
  const token = localStorage.getItem('ticketing-token');
  if (!token) {
    error.value = 'Требуется авторизация. Пожалуйста, войдите в систему через /admin';
    return;
  }
  loadConcerts();
  loadSeatCategories(); // Загружаем категории при монтировании
});
</script>

<style scoped>
.concerts-management {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.table {
  margin-bottom: 0;
}

.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
  text-transform: uppercase;
  color: #6c757d;
}

code {
  background-color: #f8f9fa;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
}
</style>
