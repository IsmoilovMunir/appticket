<template>
  <section class="py-5">
    <div class="container-fluid">
      <!-- Форма входа -->
      <div v-if="!isAuthenticated" class="card shadow-sm border-0 rounded-4 mb-4" style="max-width: 600px; margin: 0 auto;">
        <div class="card-body">
          <h2 class="h5 mb-3">Вход для менеджеров</h2>
          <form class="row g-3" @submit.prevent="handleLogin">
            <div class="col-md-4">
              <label class="form-label">Логин</label>
              <input v-model="username" class="form-control" required />
            </div>
            <div class="col-md-4">
              <label class="form-label">Пароль</label>
              <input type="password" v-model="password" class="form-control" required />
            </div>
            <div class="col-md-4 d-flex align-items-end">
              <button class="btn btn-primary w-100" :disabled="adminStore.loading">
                <span v-if="adminStore.loading" class="spinner-border spinner-border-sm me-2"></span>
                Войти
              </button>
            </div>
          </form>
          <p v-if="adminStore.error" class="text-danger mt-3">{{ adminStore.error }}</p>
        </div>
      </div>

      <!-- Дашборд -->
      <div v-else class="admin-dashboard">
        <div class="row g-4">
          <!-- Левая часть: Дашборд с карточками -->
          <div class="col-lg-4" :class="{ 'col-lg-8': activeSection }">
            <div class="mb-4">
              <h2 class="h4 mb-1">Панель управления</h2>
              <p class="text-body-secondary small mb-0">Выберите раздел для работы</p>
            </div>
            
          <div class="row g-3">
              <div class="col-md-6 col-lg-12" v-for="section in dashboardSections" :key="section.key">
                <RouterLink v-if="section.link" :to="section.link" class="text-decoration-none">
                  <div 
                    class="dashboard-card card h-100 border-0 shadow-sm"
                  >
                    <div class="card-body p-4">
                      <div class="d-flex align-items-center mb-3">
                        <div class="section-icon me-3" :class="section.iconClass">
                          <i :class="section.icon"></i>
                        </div>
                        <div class="flex-grow-1">
                          <h3 class="h6 mb-1">{{ section.title }}</h3>
                          <p class="text-body-secondary small mb-0">{{ section.description }}</p>
                        </div>
                      </div>
                      <div class="d-flex align-items-center justify-content-between">
                        <span class="badge bg-secondary">{{ getSectionCount(section.key) }}</span>
                        <i class="bi bi-chevron-right"></i>
                      </div>
                    </div>
                  </div>
                </RouterLink>
                <div 
                  v-else
                  class="dashboard-card card h-100 border-0 shadow-sm"
                  :class="{ 'active': activeSection === section.key }"
                  @click="openSection(section.key)"
                >
                  <div class="card-body p-4">
                    <div class="d-flex align-items-center mb-3">
                      <div class="section-icon me-3" :class="section.iconClass">
                        <i :class="section.icon"></i>
              </div>
                      <div class="flex-grow-1">
                        <h3 class="h6 mb-1">{{ section.title }}</h3>
                        <p class="text-body-secondary small mb-0">{{ section.description }}</p>
            </div>
              </div>
                    <div class="d-flex align-items-center justify-content-between">
                      <span class="badge bg-secondary">{{ getSectionCount(section.key) }}</span>
                      <i class="bi bi-chevron-right"></i>
            </div>
          </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Правая часть: Боковая панель с контентом -->
          <div v-if="activeSection" class="col-lg-4 sidebar-panel">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center">
              <div>
                  <h3 class="h5 mb-0">{{ getSectionTitle(activeSection) }}</h3>
                  <p class="text-body-secondary small mb-0">{{ getSectionDescription(activeSection) }}</p>
              </div>
                <button class="btn btn-sm btn-outline-secondary" @click="closeSection">
                  <i class="bi bi-x-lg"></i>
                </button>
              </div>
              <div class="card-body p-4" style="max-height: calc(100vh - 200px); overflow-y: auto;">
                <!-- Билеты -->
                <div v-if="activeSection === 'tickets'">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-body-secondary small">Всего билетов: {{ tickets.length }}</span>
              <button class="btn btn-outline-primary btn-sm" :disabled="ticketsLoading" @click="refreshTickets">
                <span v-if="ticketsLoading" class="spinner-border spinner-border-sm me-2"></span>
                Обновить
              </button>
            </div>
                  <p v-if="ticketError" class="text-danger small mb-3">{{ ticketError }}</p>
                  
                  <div class="mb-4" v-for="section in ticketSections" :key="section.key">
                  <div class="d-flex justify-content-between align-items-center mb-2">
                    <h4 class="h6 mb-0">{{ section.label }}</h4>
                    <span class="badge bg-secondary">{{ ticketsByStatus[section.key].length }}</span>
                  </div>
                    
                <div
                  v-if="section.key === 'RESERVED' && ticketsByStatus[section.key].length > 0"
                  class="mb-3"
                >
                  <div
                    v-if="hasSelection"
                    class="bg-light rounded-3 px-3 py-2 mb-2 d-flex flex-wrap gap-2 align-items-center"
                  >
                    <span class="small fw-semibold mb-0">Выбрано: {{ selectedCount }}</span>
                    <button
                      class="btn btn-success btn-sm"
                      :disabled="ticketsLoading"
                      @click="confirmSelectedTickets"
                    >
                          Подтвердить
                    </button>
                    <button
                      class="btn btn-outline-danger btn-sm"
                      :disabled="ticketsLoading"
                      @click="cancelSelectedTickets"
                    >
                          Отменить
                    </button>
                    <button class="btn btn-link btn-sm px-2" @click="clearSelection">Сбросить</button>
                  </div>
                      <div class="d-flex justify-content-between align-items-center text-body-secondary small mb-2">
                    <button class="btn btn-link btn-sm px-0" @click="selectAllReserved" :disabled="ticketsLoading">
                      Выбрать все
                    </button>
                    <button
                      class="btn btn-link btn-sm px-0"
                      @click="clearSelection"
                      :disabled="selectedCount === 0"
                    >
                      Очистить
                    </button>
                  </div>
                </div>
                    
                    <div v-if="ticketsByStatus[section.key].length === 0" class="text-body-secondary small mb-3">
                    Нет билетов
                  </div>
                  <div
                    v-for="ticket in ticketsByStatus[section.key]"
                    :key="ticket.id"
                    class="border rounded-3 p-2 mb-2"
                  >
                  <div class="d-flex justify-content-between align-items-start gap-2">
                    <div class="flex-grow-1">
                      <div class="fw-semibold small">{{ formatSeat(ticket) }}</div>
                      <div class="text-body-secondary small">Покупатель: {{ ticket.buyerName || '—' }}</div>
                      <div class="text-body-secondary small mb-2">Телефон: {{ ticket.buyerPhone || '—' }}</div>
                    </div>
                    <div v-if="section.key === 'RESERVED'" class="form-check ms-1">
                      <input
                        class="form-check-input"
                        type="checkbox"
                        :id="`ticket-select-${ticket.id}`"
                        :checked="isTicketSelected(ticket.id)"
                        @change="toggleTicketSelection(ticket.id)"
                      />
                    </div>
                  </div>
                    <div v-if="section.key === 'RESERVED'" class="d-flex gap-2">
                      <button class="btn btn-success btn-sm" @click="handleTicketConfirm(ticket.id)" :disabled="ticketsLoading">
                        Подтвердить
                      </button>
                      <button class="btn btn-outline-danger btn-sm" @click="handleTicketCancel(ticket.id)" :disabled="ticketsLoading">
                        Отменить
                      </button>
                    </div>
                    <div v-else-if="section.key === 'SOLD'" class="text-center">
                      <div v-if="ticket.qrCodeUrl" class="qr-preview mb-2">
                        <img :src="ticket.qrCodeUrl" alt="QR" class="img-fluid rounded-3" />
                      </div>
                      <div class="text-body-secondary small">QR отправлен в Telegram</div>
                    </div>
                    <div v-else class="text-body-secondary small">Билет отменён</div>
                  </div>
                </div>
              </div>

                <!-- Возвраты -->
                <div v-if="activeSection === 'refunds'">
                  <div class="mb-4">
                    <h4 class="h6 mb-3">Поиск билетов для возврата</h4>
                    <div class="card border-danger mb-4">
                      <div class="card-body">
                        <form @submit.prevent="searchTicketsForRefund" class="row g-3">
                          <div class="col-12">
                            <label class="form-label small">Телефон покупателя</label>
                            <input 
                              v-model="refundSearch.phone" 
                              class="form-control form-control-sm" 
                              placeholder="+79776699758"
                              @input="refundSearch.name = ''; refundSearch.ticketId = ''"
                            />
                          </div>
                          <div class="col-12">
                            <label class="form-label small">Имя покупателя</label>
                            <input 
                              v-model="refundSearch.name" 
                              class="form-control form-control-sm" 
                              placeholder="Иван Иванов"
                              @input="refundSearch.phone = ''; refundSearch.ticketId = ''"
                            />
                          </div>
                          <div class="col-12">
                            <label class="form-label small">ID билета</label>
                            <input 
                              v-model="refundSearch.ticketId" 
                              class="form-control form-control-sm" 
                              placeholder="UUID билета"
                              @input="refundSearch.phone = ''; refundSearch.name = ''"
                            />
                          </div>
                          <div class="col-12">
                            <button 
                              class="btn btn-danger btn-sm w-100" 
                              :disabled="refundSearchLoading"
                            >
                              <span v-if="refundSearchLoading" class="spinner-border spinner-border-sm me-2"></span>
                              Найти билеты
                            </button>
                          </div>
                        </form>
                        <p v-if="refundSearchError" class="text-danger small mt-3 mb-0">{{ refundSearchError }}</p>
                      </div>
                    </div>

                    <div v-if="refundSearchResults.length > 0">
                      <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="h6 mb-0">Найдено билетов: {{ refundSearchResults.length }}</h4>
                        <button class="btn btn-outline-secondary btn-sm" @click="clearRefundSearch">
                          Очистить
                        </button>
                      </div>
                      <div
                        v-for="ticket in refundSearchResults"
                        :key="ticket.id"
                        class="border rounded-3 p-3 mb-3"
                        :class="{ 'border-danger': ticket.status === 'SOLD', 'border-secondary': ticket.status !== 'SOLD' }"
                      >
                        <div class="d-flex justify-content-between align-items-start mb-2">
                          <div class="flex-grow-1">
                            <div class="fw-semibold small">{{ formatSeat(ticket) }}</div>
                            <div class="text-body-secondary small">Покупатель: {{ ticket.buyerName || '—' }}</div>
                            <div class="text-body-secondary small mb-2">Телефон: {{ ticket.buyerPhone || '—' }}</div>
                            <div class="text-body-secondary small mb-2">
                              Статус: 
                              <span class="badge" :class="{
                                'bg-warning': ticket.status === 'RESERVED',
                                'bg-success': ticket.status === 'SOLD',
                                'bg-secondary': ticket.status === 'CANCELLED',
                                'bg-info': ticket.status === 'USED'
                              }">
                                {{ ticket.status === 'RESERVED' ? 'Черновик' : 
                                   ticket.status === 'SOLD' ? 'Продан' : 
                                   ticket.status === 'CANCELLED' ? 'Отменён' : 'Использован' }}
                              </span>
                            </div>
                            <div class="text-body-secondary small">Выдан: {{ formatDate(ticket.issuedAt) }}</div>
                          </div>
                        </div>
                        <div v-if="ticket.status === 'SOLD'" class="mt-3">
                          <label class="form-label small">Причина возврата</label>
                          <input 
                            v-model="refundReasons[ticket.id]" 
                            class="form-control form-control-sm mb-2" 
                            placeholder="Причина возврата (обязательно)"
                          />
                          <button 
                            class="btn btn-danger btn-sm w-100" 
                            :disabled="!refundReasons[ticket.id] || refundLoading"
                            @click="refundSingleTicket(ticket.id)"
                          >
                            <span v-if="refundLoading" class="spinner-border spinner-border-sm me-2"></span>
                            Вернуть билет
                          </button>
                        </div>
                        <div v-else class="text-body-secondary small">
                          Этот билет нельзя вернуть (статус: {{ ticket.status }})
                        </div>
                      </div>
                    </div>
                    <div v-else-if="refundSearchPerformed && !refundSearchLoading" class="text-body-secondary small text-center py-4">
                      Билеты не найдены
                    </div>
                  </div>
                </div>

                <!-- Резервы -->
                <div v-if="activeSection === 'reservations'">
                  <div class="row g-3 mb-4">
                    <div class="col-12">
                      <div class="border rounded-4 p-3">
                        <h4 class="h6 mb-3">Подтверждение резерва</h4>
                        <form @submit.prevent="confirmReservation">
                          <label class="form-label small">ID резерва</label>
                          <input v-model="reservationId" class="form-control form-control-sm mb-3" placeholder="UUID" required />
                          <button class="btn btn-success btn-sm w-100" :disabled="pendingAction">
                            <span v-if="pendingAction" class="spinner-border spinner-border-sm me-2"></span>
                            Подтвердить
                          </button>
                        </form>
            </div>
          </div>
                    <div class="col-12">
                      <div class="border rounded-4 p-3">
                        <h4 class="h6 mb-3">Отмена резерва</h4>
                        <form @submit.prevent="cancelReservation">
                          <label class="form-label small">ID резерва</label>
                          <input v-model="cancelReservationId" class="form-control form-control-sm mb-2" placeholder="UUID" required />
                          <label class="form-label small">Причина</label>
                          <input v-model="cancelReason" class="form-control form-control-sm mb-3" placeholder="Комментарий" />
                          <button class="btn btn-danger btn-sm w-100" :disabled="pendingAction">
                            Отменить
                          </button>
                        </form>
              </div>
                    </div>
                  </div>
                  <p v-if="actionMessage" class="text-success small mb-0">{{ actionMessage }}</p>
                </div>

                <!-- Конфигурация зала -->
                <div v-if="activeSection === 'seat-config'">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-body-secondary small">Настройка категорий и цен</span>
              <button class="btn btn-outline-primary btn-sm" :disabled="seatConfigLoading" @click="loadSeatConfig">
                <span v-if="seatConfigLoading" class="spinner-border spinner-border-sm me-2"></span>
                      Обновить
              </button>
            </div>
            <p v-if="seatConfigError" class="text-danger small mb-3">{{ seatConfigError }}</p>
                  
                  <div class="mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                      <h4 class="h6 mb-0">Категории мест</h4>
                      <button 
                        class="btn btn-sm btn-primary" 
                        @click="showNewCategoryForm = !showNewCategoryForm"
                        :disabled="seatConfigLoading"
                      >
                        <i class="bi bi-plus-lg me-1"></i>
                        {{ showNewCategoryForm ? 'Отмена' : 'Добавить категорию' }}
                      </button>
                    </div>
                    
                    <!-- Форма создания новой категории -->
                    <div v-if="showNewCategoryForm" class="border border-primary rounded-3 p-3 mb-3 bg-light">
                      <h5 class="h6 mb-3">Новая категория</h5>
                      <div class="mb-2">
                        <label class="form-label small">Название категории <span class="text-danger">*</span></label>
                        <input
                          type="text"
                          class="form-control form-control-sm"
                          v-model="newCategoryForm.name"
                          placeholder="Например: VIP 15000 ₽"
                        />
                      </div>
                      <div class="mb-2">
                        <label class="form-label small">Цена (₽) <span class="text-danger">*</span></label>
                        <input
                          type="number"
                          min="0"
                          class="form-control form-control-sm"
                          v-model="newCategoryForm.price"
                          placeholder="0"
                        />
                      </div>
                      <div class="mb-2">
                        <label class="form-label small">Описание</label>
                        <textarea
                          class="form-control form-control-sm"
                          rows="2"
                          v-model="newCategoryForm.description"
                          placeholder="Описание категории (необязательно)"
                        ></textarea>
                      </div>
                      <div class="mb-3">
                        <label class="form-label small">Цвет категории</label>
                        <div class="d-flex align-items-center gap-2">
                          <input
                            type="color"
                            class="form-control form-control-color form-control-sm flex-shrink-0 category-color-picker"
                            v-model="newCategoryForm.color"
                          />
                          <input
                            type="text"
                            class="form-control form-control-sm text-uppercase"
                            v-model="newCategoryForm.color"
                            placeholder="#RRGGBB"
                          />
                        </div>
                      </div>
                      <div class="d-flex gap-2">
                        <button 
                          class="btn btn-sm btn-primary" 
                          @click="createNewCategory"
                          :disabled="seatConfigLoading || !newCategoryForm.name || !newCategoryForm.price"
                        >
                          <span v-if="seatConfigLoading" class="spinner-border spinner-border-sm me-2"></span>
                          Создать категорию
                        </button>
                        <button 
                          class="btn btn-sm btn-outline-secondary" 
                          @click="cancelNewCategory"
                          :disabled="seatConfigLoading"
                        >
                          Отмена
                        </button>
                      </div>
                      <p v-if="newCategoryError" class="text-danger small mt-2 mb-0">{{ newCategoryError }}</p>
                    </div>
                    
                  <div v-if="seatCategories.length === 0 && !showNewCategoryForm" class="alert alert-info small mb-3">
                    <i class="bi bi-info-circle me-2"></i>
                    Категории мест еще не созданы. Нажмите кнопку "Добавить категорию" выше, чтобы создать первую категорию.
                  </div>
                    <div v-for="category in seatCategories" :key="category.id" class="border rounded-3 p-3 mb-3">
                    <div class="d-flex align-items-center gap-2 fw-semibold mb-2">
                      <span
                        class="category-color-dot"
                        :style="{ background: categoryForms[category.id]?.color || '#adb5bd' }"
                      ></span>
                      <span>{{ getCategoryDisplayName(category) }}{{ categoryForms[category.id]?.price ? ' ' + formatRub(parseInt(categoryForms[category.id].price) * 100) : '' }}</span>
                    </div>
                    <label class="form-label small">Цена (₽)</label>
                    <input
                      type="number"
                      min="0"
                      class="form-control form-control-sm mb-2"
                      v-model="categoryForms[category.id].price"
                    />
                    <label class="form-label small">Описание</label>
                    <textarea
                      class="form-control form-control-sm mb-2"
                      rows="2"
                      v-model="categoryForms[category.id].description"
                    ></textarea>
                    <label class="form-label small">Цвет категории</label>
                    <div class="d-flex align-items-center gap-2 mb-2">
                      <input
                        type="color"
                        class="form-control form-control-color form-control-sm flex-shrink-0 category-color-picker"
                        v-model="categoryForms[category.id].color"
                      />
                      <input
                        type="text"
                        class="form-control form-control-sm text-uppercase"
                        :value="categoryForms[category.id].color"
                        readonly
                        @focus="selectColorValue"
                      />
                    </div>
                    <button class="btn btn-sm btn-outline-primary w-100" :disabled="seatConfigLoading" @click="saveCategory(category.id)">
                      Сохранить
                    </button>
                  </div>
                </div>

                  <div>
                    <h4 class="h6 mb-3">Столы и цены</h4>
                  <div class="table-responsive seat-table-config mb-3">
                    <table class="table table-sm align-middle">
                      <thead class="text-body-secondary small">
                        <tr>
                          <th>Стол/Зона</th>
                          <th>Категория</th>
                          <th>Цена</th>
                            <th>Действие</th>
                        </tr>
                      </thead>
                      <tbody>
                        <!-- Танцпол -->
                        <tr v-if="danceFloorAssignment" class="table-info">
                          <td>
                            <div class="fw-semibold small">
                              <i class="bi bi-music-note me-1"></i>
                              Танцпол
                            </div>
                            <div class="text-body-secondary small">tableNumber: 0</div>
                          </td>
                          <td>
                            <select class="form-select form-select-sm" v-model="danceFloorCategoryId">
                              <option :value="null">Без категории</option>
                              <option v-for="category in seatCategories" :key="category.id" :value="category.id">
                                {{ getCategoryDisplayName(category) }}
                              </option>
                            </select>
                          </td>
                          <td>
                            <input
                              type="number"
                              min="0"
                              step="100"
                              class="form-control form-control-sm"
                              v-model="danceFloorPriceOverride"
                              placeholder="Цена в копейках"
                            />
                          </td>
                          <td>
                            <button
                              class="btn btn-sm btn-outline-primary"
                              :disabled="seatConfigLoading"
                              @click="applyDanceFloorSettings"
                            >
                              Применить
                            </button>
                          </td>
                        </tr>

                        <!-- Столы -->
                          <tr v-for="assignment in seatTables" :key="assignment.tableNumber">
                          <td>
                              <div class="fw-semibold small">Стол {{ assignment.tableNumber }}</div>
                            <div class="text-body-secondary small">База: {{ formatRub(assignment.basePriceCents) }}</div>
                          </td>
                          <td>
                            <select class="form-select form-select-sm" v-model="tableSelections[assignment.tableNumber]">
                              <option v-for="category in seatCategories" :key="category.id" :value="category.id">
                                {{ getCategoryDisplayName(category) }} • {{ formatRub(category.priceCents) }}
                              </option>
                            </select>
                          </td>
                          <td>
                            <input
                              type="number"
                              min="0"
                              step="100"
                              class="form-control form-control-sm"
                              v-model="tableOverrideInputs[assignment.tableNumber]"
                              placeholder="Переопределить цену"
                            />
                          </td>
                          <td>
                            <button
                              class="btn btn-sm btn-outline-primary"
                              :disabled="seatConfigLoading"
                              @click="applyTableSettings(assignment.tableNumber)"
                            >
                              Применить
                            </button>
                          </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <p class="text-body-secondary small">
                      Всего столов: {{ seatTables.length }}
                      <span v-if="danceFloorAssignment">• Танцпол: 1 зона</span>
                    </p>
                  </div>

                  <!-- Управление местами танцпола -->
                  <div v-if="danceFloorAssignment" class="mb-4">
                    <h4 class="h6 mb-3">
                      Места танцпола
                      <button
                        class="btn btn-sm btn-outline-info ms-2"
                        @click="loadDanceFloorSeats"
                        :disabled="danceFloorSeatsLoading"
                      >
                        <span v-if="danceFloorSeatsLoading" class="spinner-border spinner-border-sm me-1"></span>
                        <i class="bi bi-arrow-clockwise me-1"></i>
                        Обновить
                      </button>
                    </h4>

                    <div v-if="danceFloorSeatsError" class="alert alert-danger small mb-3">
                      {{ danceFloorSeatsError }}
                    </div>

                    <div v-if="danceFloorSeats.length > 0" class="table-responsive">
                      <table class="table table-sm">
                        <thead class="text-body-secondary small">
                          <tr>
                            <th>ID</th>
                            <th>Место</th>
                            <th>Статус</th>
                            <th>Цена</th>
                            <th>Действия</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="seat in danceFloorSeats" :key="seat.id">
                            <td class="small">{{ seat.id }}</td>
                            <td class="small">Танцпол {{ seat.chairNumber }}</td>
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
                              <input
                                type="number"
                                min="0"
                                step="100"
                                class="form-control form-control-sm"
                                v-model="danceFloorSeatPrices[seat.id]"
                                :placeholder="formatRub(seat.priceCents)"
                                style="width: 120px;"
                              />
                            </td>
                            <td>
                              <div class="btn-group btn-group-sm">
                                <button
                                  v-if="seat.status === 'AVAILABLE'"
                                  class="btn btn-outline-danger btn-sm"
                                  @click="blockDanceFloorSeat(seat.id)"
                                  title="Заблокировать место"
                                >
                                  <i class="bi bi-lock"></i>
                                </button>
                                <button
                                  v-if="seat.status === 'BLOCKED'"
                                  class="btn btn-outline-success btn-sm"
                                  @click="unblockDanceFloorSeat(seat.id)"
                                  title="Разблокировать место"
                                >
                                  <i class="bi bi-unlock"></i>
                                </button>
                                <button
                                  class="btn btn-outline-primary btn-sm"
                                  @click="updateDanceFloorSeatPrice(seat.id)"
                                  :disabled="!danceFloorSeatPrices[seat.id]"
                                  title="Обновить цену"
                                >
                                  <i class="bi bi-currency-ruble"></i>
                                </button>
                              </div>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>

                    <div v-else-if="!danceFloorSeatsLoading" class="text-center py-3 text-body-secondary small">
                      <i class="bi bi-music-note-beamed" style="font-size: 2rem;"></i>
                      <p class="mt-2 mb-0">Места танцпола не найдены</p>
                    </div>
                  </div>

                  <!-- Танцпол -->
                  <div class="mb-4">
                    <h4 class="h6 mb-3">Танцпол</h4>
                    <div class="card border-warning">
                      <div class="card-body">
                        <p class="text-body-secondary small mb-3">
                          Укажите ID кружка танцпола из SVG схемы и количество билетов для него.
                          Например, для кружка с <code>data-seat-id="7"</code> укажите ID: 7
                        </p>
                        <div class="mb-3">
                          <label class="form-label small">ID кружка танцпола (data-seat-id)</label>
                          <input
                            type="number"
                            min="1"
                            class="form-control form-control-sm"
                            v-model.number="danceFloorSeatId"
                            placeholder="7"
                          />
                        </div>
                        <div class="mb-3">
                          <label class="form-label small">Категория</label>
                          <select class="form-select form-select-sm" v-model="danceFloorCategoryId">
                            <option :value="null">Выберите категорию</option>
                            <option v-for="category in seatCategories" :key="category.id" :value="category.id">
                              {{ getCategoryDisplayName(category) }} • {{ formatRub(category.priceCents) }}
                            </option>
                          </select>
                        </div>
                        <div class="mb-3">
                          <label class="form-label small">Количество билетов</label>
                          <input
                            type="number"
                            min="1"
                            class="form-control form-control-sm"
                            v-model.number="danceFloorCapacity"
                            placeholder="250"
                          />
                          <small class="text-body-secondary">Сколько билетов будет создано для этого кружка</small>
                        </div>
                        <button
                          class="btn btn-sm btn-warning w-100"
                          :disabled="seatConfigLoading || !danceFloorSeatId || !danceFloorCategoryId || !danceFloorCapacity"
                          @click="createDanceFloorSeats"
                        >
                          <span v-if="seatConfigLoading" class="spinner-border spinner-border-sm me-2"></span>
                          Создать места для танцпола
                        </button>
                        <p v-if="danceFloorError" class="text-danger small mt-2 mb-0">{{ danceFloorError }}</p>
                        <p v-if="danceFloorSuccess" class="text-success small mt-2 mb-0">{{ danceFloorSuccess }}</p>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Пользователи -->
                <!-- Использованные билеты (только для админа) -->
                <div v-if="activeSection === 'used-tickets'">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-body-secondary small">Использованных билетов: {{ usedTickets.length }}</span>
                    <button class="btn btn-outline-primary btn-sm" :disabled="usedTicketsLoading" @click="loadUsedTickets">
                      <i class="bi bi-arrow-clockwise me-1"></i>
                      Обновить
                    </button>
                  </div>
                  
                  <div v-if="usedTicketsError" class="alert alert-danger mb-3">
                    {{ usedTicketsError }}
                  </div>
                  
                  <div v-if="usedTicketsLoading" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Загрузка...</span>
                    </div>
                  </div>
                  
                  <div v-else-if="usedTickets.length === 0" class="text-center py-4 text-body-secondary">
                    <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                    <p class="mt-3 mb-0">Нет использованных билетов</p>
                  </div>
                  
                  <div v-else class="list-group">
                    <div 
                      v-for="ticket in usedTickets" 
                      :key="ticket.id"
                      class="list-group-item"
                    >
                      <div class="d-flex justify-content-between align-items-start">
                        <div class="flex-grow-1">
                          <h6 class="mb-1">{{ ticket.buyerName }}</h6>
                          <p class="mb-1 small text-body-secondary">
                            Телефон: {{ ticket.buyerPhone }}
                          </p>
                          <p class="mb-1 small" v-if="ticket.seat">
                            Место: Стол {{ ticket.seat.tableNumber }}, место {{ ticket.seat.chairNumber }}
                          </p>
                          <p class="mb-0 small text-body-secondary" v-if="ticket.checkedInAt">
                            Использован: {{ formatDate(ticket.checkedInAt) }}
                          </p>
                        </div>
                        <button
                          class="btn btn-sm btn-warning ms-2"
                          @click="handleRevertTicket(ticket.id)"
                          :disabled="revertLoading[ticket.id]"
                        >
                          <span v-if="revertLoading[ticket.id]" class="spinner-border spinner-border-sm me-1"></span>
                          <i v-else class="bi bi-arrow-counterclockwise me-1"></i>
                          Вернуть в продажу
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="activeSection === 'promo-codes'">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-body-secondary small">Управление промокодами</span>
                    <button class="btn btn-outline-primary btn-sm" :disabled="promoCodesLoading" @click="loadPromoCodes">
                      <span v-if="promoCodesLoading" class="spinner-border spinner-border-sm me-2"></span>
                      Обновить
                    </button>
                  </div>
                  <p v-if="promoCodesError" class="text-danger small mb-3">{{ promoCodesError }}</p>
                  
                  <div class="mb-4">
                    <button class="btn btn-primary btn-sm" @click="showCreatePromoCode = true">
                      <i class="bi bi-plus-lg me-1"></i>
                      Создать промокод
                    </button>
                  </div>

                  <div v-if="promoCodes.length === 0" class="text-body-secondary small mb-3">
                    Промокоды не найдены
                  </div>
                  <div v-else class="list-group">
                    <div
                      v-for="promoCode in promoCodes"
                      :key="promoCode.id"
                      class="list-group-item border rounded-3 p-3 mb-2"
                    >
                      <div class="d-flex justify-content-between align-items-start mb-2">
                        <div class="flex-grow-1">
                          <div class="fw-semibold small mb-1">
                            <code>{{ promoCode.code }}</code>
                            <span v-if="!promoCode.active" class="badge bg-secondary ms-2">Неактивен</span>
                          </div>
                          <div class="text-body-secondary small">
                            Скидка: {{ promoCode.discountPercent }}%
                          </div>
                          <div class="text-body-secondary small">
                            Использовано: {{ promoCode.usedCount }}
                            <span v-if="promoCode.usageLimit">/ {{ promoCode.usageLimit }}</span>
                          </div>
                          <div class="text-body-secondary small">
                            Действителен: {{ formatDate(promoCode.validFrom) }}
                            <span v-if="promoCode.validTo"> - {{ formatDate(promoCode.validTo) }}</span>
                          </div>
                          <div v-if="promoCode.applicableCategoryIds && promoCode.applicableCategoryIds.length > 0" class="text-body-secondary small">
                            Категории: {{ promoCode.applicableCategoryIds.length }}
                          </div>
                          <div v-else class="text-body-secondary small">
                            Применим ко всем категориям
                          </div>
                        </div>
                        <div class="d-flex gap-2">
                          <button class="btn btn-outline-primary btn-sm" @click="editPromoCode(promoCode)">
                            <i class="bi bi-pencil"></i>
                          </button>
                          <button class="btn btn-outline-danger btn-sm" @click="deletePromoCodeHandler(promoCode.id)">
                            <i class="bi bi-trash"></i>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Модальное окно создания/редактирования -->
                  <div v-if="showCreatePromoCode || editingPromoCode" class="modal fade show d-block" style="background: rgba(0,0,0,0.5);" @click.self="closePromoCodeModal">
                    <div class="modal-dialog" @click.stop>
                      <div class="modal-content">
                        <div class="modal-header">
                          <h5 class="modal-title">{{ editingPromoCode ? 'Редактировать промокод' : 'Создать промокод' }}</h5>
                          <button type="button" class="btn-close" @click="closePromoCodeModal"></button>
                        </div>
                        <div class="modal-body">
                          <form @submit.prevent="savePromoCode">
                            <div class="mb-3">
                              <label class="form-label small">Код промокода</label>
                              <input
                                v-model="promoCodeForm.code"
                                class="form-control form-control-sm"
                                required
                                placeholder="PROMO2024"
                              />
                            </div>
                            <div class="mb-3">
                              <label class="form-label small">Процент скидки (1-100)</label>
                              <input
                                v-model.number="promoCodeForm.discountPercent"
                                type="number"
                                min="1"
                                max="100"
                                class="form-control form-control-sm"
                                required
                              />
                            </div>
                            <div class="mb-3">
                              <label class="form-label small">Применим к категориям (оставьте пустым для всех)</label>
                              <select
                                v-model="promoCodeForm.applicableCategoryIds"
                                class="form-select form-select-sm"
                                multiple
                                size="5"
                              >
                                <option v-for="category in seatCategories" :key="category.id" :value="category.id">
                                  {{ category.name }}
                                </option>
                              </select>
                              <small class="text-body-secondary">Выберите категории или оставьте пустым для всех</small>
                            </div>
                            <div class="mb-3">
                              <div class="form-check">
                                <input
                                  v-model="promoCodeForm.active"
                                  class="form-check-input"
                                  type="checkbox"
                                  id="promoCodeActive"
                                />
                                <label class="form-check-label small" for="promoCodeActive">
                                  Активен
                                </label>
                              </div>
                            </div>
                            <div class="mb-3">
                              <label class="form-label small">Действителен с</label>
                              <input
                                v-model="promoCodeForm.validFrom"
                                type="datetime-local"
                                class="form-control form-control-sm"
                              />
                            </div>
                            <div class="mb-3">
                              <label class="form-label small">Действителен до (необязательно)</label>
                              <input
                                v-model="promoCodeForm.validTo"
                                type="datetime-local"
                                class="form-control form-control-sm"
                              />
                            </div>
                            <div class="mb-3">
                              <label class="form-label small">Лимит использований (необязательно)</label>
                              <input
                                v-model.number="promoCodeForm.usageLimit"
                                type="number"
                                min="1"
                                class="form-control form-control-sm"
                              />
                            </div>
                            <div class="d-flex gap-2">
                              <button type="submit" class="btn btn-primary btn-sm" :disabled="promoCodeSaving">
                                <span v-if="promoCodeSaving" class="spinner-border spinner-border-sm me-2"></span>
                                Сохранить
                              </button>
                              <button type="button" class="btn btn-outline-secondary btn-sm" @click="closePromoCodeModal">
                                Отмена
                              </button>
                            </div>
                            <p v-if="promoCodeSaveError" class="text-danger small mt-2 mb-0">{{ promoCodeSaveError }}</p>
                          </form>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="activeSection === 'concerts'">
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="h5 mb-0">
                      <i class="bi bi-music-note-beamed me-2"></i>
                      Управление концертами
                    </h4>
                    <button class="btn btn-outline-primary btn-sm" :disabled="concertsLoading" @click="loadConcerts">
                      <span v-if="concertsLoading" class="spinner-border spinner-border-sm me-2"></span>
                      Обновить
                    </button>
                  </div>
                  <p v-if="concertsError" class="text-danger small mb-3">{{ concertsError }}</p>
                  
                  <div class="mb-4">
                    <button class="btn btn-primary" @click="showCreateConcert = true">
                      <i class="bi bi-plus-lg me-2"></i>
                      Создать новый концерт
                    </button>
                  </div>

                  <div v-if="concerts.length === 0" class="text-body-secondary small mb-3">
                    Концерты не найдены
                  </div>
                  <div v-else class="list-group">
                    <div
                      v-for="concert in concerts"
                      :key="concert.id"
                      class="list-group-item border rounded-3 p-3 mb-2"
                    >
                      <div class="d-flex justify-content-between align-items-start mb-2">
                        <div class="flex-grow-1">
                          <div class="fw-semibold small mb-1">{{ concert.title }}</div>
                          <div class="text-body-secondary small mb-1">
                            <div v-if="concert.city">Город: {{ concert.city }}</div>
                            <div v-if="concert.venue">Место: {{ concert.venue }}</div>
                            <div v-if="concert.concertDate">
                              Дата: {{ formatDate(concert.concertDate) }}
                            </div>
                            <div v-if="concert.slug">URL: /{{ concert.slug }}</div>
                          </div>
                        </div>
                        <div class="d-flex gap-2">
                          <button class="btn btn-outline-primary btn-sm" @click="editConcert(concert)">
                            <i class="bi bi-pencil"></i>
                          </button>
                          <button class="btn btn-outline-danger btn-sm" @click="deleteConcertHandler(concert.id)">
                            <i class="bi bi-trash"></i>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Модальное окно создания/редактирования -->
                  <div v-if="showCreateConcert || editingConcert" class="modal fade show d-block" style="background: rgba(0,0,0,0.5);" @click.self="closeConcertModal">
                    <div class="modal-dialog modal-lg" @click.stop>
                      <div class="modal-content">
                        <div class="modal-header">
                          <h5 class="modal-title">{{ editingConcert ? 'Редактировать концерт' : 'Создать концерт' }}</h5>
                          <button type="button" class="btn-close" @click="closeConcertModal"></button>
                        </div>
                        <div class="modal-body" style="max-height: 70vh; overflow-y: auto;">
                          <form @submit.prevent="saveConcert">
                            <div class="row g-3">
                              <div class="col-12">
                                <label class="form-label small">Название концерта *</label>
                                <input
                                  type="text"
                                  v-model="concertForm.title"
                                  @input="handleTitleChange"
                                  class="form-control form-control-sm"
                                  required
                                />
                              </div>
                              <div class="col-12">
                                <label class="form-label small">Slug (URL) *</label>
                                <input
                                  type="text"
                                  v-model="concertForm.slug"
                                  class="form-control form-control-sm"
                                  required
                                  pattern="[a-z0-9-]+"
                                  placeholder="name-concert"
                                />
                                <small class="text-body-secondary">Только латинские буквы, цифры и дефисы</small>
                              </div>
                              <div class="col-12">
                                <label class="form-label small">Описание</label>
                                <textarea
                                  v-model="concertForm.description"
                                  class="form-control form-control-sm"
                                  rows="3"
                                ></textarea>
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Дата и время концерта *</label>
                                <input
                                  type="datetime-local"
                                  v-model="concertForm.concertDate"
                                  class="form-control form-control-sm"
                                  required
                                />
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Дата и время начала события</label>
                                <input
                                  type="datetime-local"
                                  v-model="concertForm.eventStartTime"
                                  class="form-control form-control-sm"
                                />
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Дата и время начала запуска гостей</label>
                                <input
                                  type="datetime-local"
                                  v-model="concertForm.guestStartTime"
                                  class="form-control form-control-sm"
                                />
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Место проведения</label>
                                <input
                                  type="text"
                                  v-model="concertForm.venue"
                                  class="form-control form-control-sm"
                                />
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Город мероприятия</label>
                                <input
                                  type="text"
                                  v-model="concertForm.city"
                                  class="form-control form-control-sm"
                                />
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Валюта</label>
                                <select v-model="concertForm.currency" class="form-select form-select-sm">
                                  <option value="RUB">RUB</option>
                                  <option value="USD">USD</option>
                                  <option value="EUR">EUR</option>
                                </select>
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Возрастное ограничение</label>
                                <input
                                  type="text"
                                  v-model="concertForm.ageRestriction"
                                  class="form-control form-control-sm"
                                  placeholder="18+"
                                />
                              </div>
                              <div class="col-md-6">
                                <label class="form-label small">Тип мероприятия</label>
                                <input
                                  type="text"
                                  v-model="concertForm.eventType"
                                  class="form-control form-control-sm"
                                  placeholder="Концерт"
                                />
                              </div>
                              <div class="col-12">
                                <label class="form-label small">Афиша</label>
                                <div v-if="concertForm.posterUrl" class="mb-2">
                                  <img :src="concertForm.posterUrl" alt="Poster" style="max-width: 200px; max-height: 200px;" class="img-thumbnail" />
                                </div>
                                <input
                                  type="file"
                                  accept="image/*"
                                  @change="handlePosterUpload"
                                  class="form-control form-control-sm"
                                  :disabled="posterUploading"
                                />
                                <small v-if="posterUploading" class="text-body-secondary">Загрузка...</small>
                              </div>
                              <div class="col-12">
                                <label class="form-label small">Схема продаж (SVG)</label>
                                <div v-if="concertForm.salesSchemeUrl" class="mb-2">
                                  <a :href="concertForm.salesSchemeUrl" target="_blank" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-eye me-1"></i>
                                    Просмотреть схему
                                  </a>
                                </div>
                                <input
                                  type="file"
                                  accept=".svg,image/svg+xml"
                                  @change="handleSchemeUpload"
                                  class="form-control form-control-sm"
                                  :disabled="schemeUploading"
                                />
                                <small v-if="schemeUploading" class="text-body-secondary">Загрузка...</small>
                                <small class="text-body-secondary d-block">Только SVG файлы</small>
                              </div>
                            </div>
                            <div class="mt-3">
                              <button type="submit" class="btn btn-primary btn-sm" :disabled="concertSaving">
                                <span v-if="concertSaving" class="spinner-border spinner-border-sm me-2"></span>
                                Сохранить
                              </button>
                              <button type="button" class="btn btn-outline-secondary btn-sm" @click="closeConcertModal">
                                Отмена
                              </button>
                            </div>
                            <p v-if="concertSaveSuccess" class="text-success small mt-2 mb-0">
                              <i class="bi bi-check-circle me-1"></i>{{ concertSaveSuccess }}
                            </p>
                            <p v-if="concertSaveError" class="text-danger small mt-2 mb-0">
                              <i class="bi bi-exclamation-circle me-1"></i>{{ concertSaveError }}
                            </p>
                          </form>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="activeSection === 'users'">
                  <!-- Список пользователей -->
                  <div class="card border-primary mb-4">
                    <div class="card-header bg-primary-subtle d-flex justify-content-between align-items-center">
                      <h4 class="h6 mb-0">
                        <i class="bi bi-people me-2"></i>
                        Зарегистрированные пользователи
                      </h4>
                      <button class="btn btn-outline-primary btn-sm" :disabled="usersLoading" @click="loadUsers">
                        <span v-if="usersLoading" class="spinner-border spinner-border-sm me-2"></span>
                        Обновить
                      </button>
                    </div>
                    <div class="card-body">
                      <p v-if="usersError" class="text-danger small mb-3">{{ usersError }}</p>
                      <div v-if="users.length === 0 && !usersLoading" class="text-body-secondary small">
                        Нет зарегистрированных пользователей
                      </div>
                      <div v-else class="list-group list-group-flush">
                        <div
                          v-for="user in users"
                          :key="user.id"
                          class="list-group-item px-0 py-3 border-bottom"
                        >
                          <div class="d-flex justify-content-between align-items-start">
                            <div class="flex-grow-1">
                              <div class="d-flex align-items-center gap-2 mb-2">
                                <h5 class="h6 mb-0">{{ user.username }}</h5>
                                <span v-if="!user.enabled" class="badge bg-secondary">Неактивен</span>
                                <span v-else class="badge bg-success">Активен</span>
                              </div>
                              <div class="d-flex flex-wrap gap-2 mb-2">
                                <span
                                  v-for="role in user.roles"
                                  :key="role"
                                  class="badge"
                                  :class="{
                                    'bg-danger': role === 'ADMIN',
                                    'bg-primary': role === 'MANAGER',
                                    'bg-info': role === 'CHECKIN'
                                  }"
                                >
                                  {{ role }}
                                </span>
                              </div>
                              <div class="text-body-secondary small">
                                Создан: {{ formatDate(user.createdAt) }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Регистрация нового менеджера -->
                  <div class="card border-info mb-4">
                    <div class="card-header bg-info-subtle">
                      <h4 class="h6 mb-0">
                        <i class="bi bi-person-plus me-2"></i>
                        Регистрация нового менеджера
                      </h4>
                    </div>
                    <div class="card-body">
                      <div v-if="!registrationStep || registrationStep === 'request'">
                        <p class="text-body-secondary small mb-3">
                          Зарегистрируйте нового менеджера. Код подтверждения будет отправлен в Telegram бот.
                        </p>
                        <form @submit.prevent="handleRequestRegistration" class="row g-3">
                          <div class="col-12">
                            <label class="form-label small">Имя пользователя</label>
                                <input
                              type="text" 
                              v-model="registrationForm.username" 
                                  class="form-control form-control-sm"
                              required 
                              minlength="3"
                              maxlength="50"
                              placeholder="Минимум 3 символа"
                            />
                          </div>
                          <div class="col-12">
                            <label class="form-label small">Пароль</label>
                                <input
                              type="password" 
                              v-model="registrationForm.password" 
                                  class="form-control form-control-sm"
                              required 
                              minlength="8"
                              placeholder="Минимум 8 символов"
                                />
                              </div>
                          <div class="col-12">
                            <button 
                              class="btn btn-info btn-sm w-100" 
                              :disabled="registrationLoading"
                            >
                              <span v-if="registrationLoading" class="spinner-border spinner-border-sm me-2"></span>
                              Запросить код
                            </button>
                          </div>
                        </form>
                        <p v-if="registrationError" class="text-danger small mt-3 mb-0">{{ registrationError }}</p>
                        <p v-if="registrationMessage" class="text-success small mt-3 mb-0">{{ registrationMessage }}</p>
                      </div>

                      <div v-if="registrationStep === 'confirm'">
                        <p class="text-body-secondary small mb-3">
                          Проверьте Telegram бот и введите полученный код подтверждения.
                        </p>
                        <form @submit.prevent="handleConfirmRegistration" class="row g-3">
                          <div class="col-12">
                            <label class="form-label small">Имя пользователя</label>
                            <input 
                              type="text" 
                              v-model="confirmForm.username" 
                              class="form-control form-control-sm" 
                              required 
                              readonly
                            />
                          </div>
                          <div class="col-12">
                            <label class="form-label small">Код подтверждения</label>
                            <input 
                              type="text" 
                              v-model="confirmForm.verificationCode" 
                              class="form-control form-control-sm" 
                              required 
                              maxlength="6"
                              placeholder="6 цифр из Telegram"
                              pattern="[0-9]{6}"
                            />
                          </div>
                          <div class="col-12">
                              <div class="d-flex gap-2">
                                <button
                                class="btn btn-success btn-sm" 
                                :disabled="confirmLoading"
                                >
                                <span v-if="confirmLoading" class="spinner-border spinner-border-sm me-2"></span>
                                Подтвердить
                                </button>
                                <button
                                type="button"
                                class="btn btn-outline-secondary btn-sm" 
                                @click="resetRegistration"
                                >
                                Отмена
                                </button>
                              </div>
                            </div>
                        </form>
                        <p v-if="confirmError" class="text-danger small mt-3 mb-0">{{ confirmError }}</p>
                        <p v-if="confirmMessage" class="text-success small mt-3 mb-0">{{ confirmMessage }}</p>
                  </div>
                    </div>
                  </div>

                  <!-- Смена пароля -->
                  <div class="card border-warning">
                    <div class="card-header bg-warning-subtle">
                      <h4 class="h6 mb-0">
                        <i class="bi bi-shield-exclamation me-2"></i>
                        Смена пароля
                      </h4>
                    </div>
                    <div class="card-body">
                      <p class="text-body-secondary small mb-3">
                        Рекомендуется регулярно менять пароль для обеспечения безопасности.
                      </p>
                      <form @submit.prevent="handleChangePassword" class="row g-3">
                        <div class="col-12">
                          <label class="form-label small">Текущий пароль</label>
                          <input 
                            type="password" 
                            v-model="changePasswordForm.currentPassword" 
                            class="form-control form-control-sm" 
                            required 
                          />
                        </div>
                        <div class="col-12">
                          <label class="form-label small">Новый пароль</label>
                          <input 
                            type="password" 
                            v-model="changePasswordForm.newPassword" 
                            class="form-control form-control-sm" 
                            required 
                            minlength="8"
                            placeholder="Минимум 8 символов"
                          />
                        </div>
                        <div class="col-12">
                          <button
                            class="btn btn-warning btn-sm w-100" 
                            :disabled="changePasswordLoading"
                          >
                            <span v-if="changePasswordLoading" class="spinner-border spinner-border-sm me-2"></span>
                            Изменить пароль
                          </button>
                        </div>
                      </form>
                      <p v-if="changePasswordError" class="text-danger small mt-3 mb-0">{{ changePasswordError }}</p>
                      <p v-if="changePasswordSuccess" class="text-success small mt-3 mb-0">{{ changePasswordSuccess }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import { useAdminStore } from '../stores/adminStore';
import { useSeatStore } from '../stores/seatStore';
import {
  apiClient,
  fetchTickets,
  confirmTicket,
  cancelTicket,
  confirmTicketsBulk,
  cancelTicketsBulk,
  fetchSeatCategories,
  fetchSeatTableAssignments,
  createSeatCategory,
  updateSeatCategory,
  assignSeatCategory,
  overrideSeatPrice,
  changePassword,
  requestManagerRegistration,
  confirmManagerRegistration,
  fetchAdminUsers,
  searchTickets,
  refundTicket,
  fetchUsedTickets,
  revertTicketStatus,
  fetchPromoCodes,
  createPromoCode,
  updatePromoCode,
  deletePromoCode,
  fetchAllConcerts,
  fetchConcertBySlug,
  createConcert,
  updateConcert,
  deleteConcert,
  uploadPoster,
  uploadScheme,
  bulkCreateSeats,
  blockSeat,
  unblockSeat,
  fetchSeats,
  type PromoCodeDto,
  type CreatePromoCodeRequest,
  type UpdatePromoCodeRequest
} from '../services/api';
import type { Ticket, Seat, SeatCategorySummary, SeatTableAssignment, AdminUser, Concert, CreateConcertRequest, UpdateConcertRequest } from '../types';

const adminStore = useAdminStore();
const route = useRoute();

const username = ref('');
const password = ref('');
const reservationId = ref('');
const cancelReservationId = ref('');
const cancelReason = ref('customer request');
const pendingAction = ref(false);
const actionMessage = ref('');
const tickets = ref<Ticket[]>([]);
const ticketsLoading = ref(false);
const ticketError = ref('');
const selectedTicketIds = ref<Set<string>>(new Set());

// Активный раздел
const activeSection = ref<string | null>(null);

// Смена пароля
const changePasswordForm = ref({
  currentPassword: '',
  newPassword: ''
});
const changePasswordLoading = ref(false);
const changePasswordError = ref('');
const changePasswordSuccess = ref('');

// Регистрация менеджера
const registrationStep = ref<'request' | 'confirm' | null>(null);
const registrationForm = ref({
  username: '',
  password: ''
});
const confirmForm = ref({
  username: '',
  verificationCode: ''
});
const registrationLoading = ref(false);
const registrationError = ref('');
const registrationMessage = ref('');
const confirmLoading = ref(false);
const confirmError = ref('');
const confirmMessage = ref('');

type CategoryFormState = { name: string; price: string; description: string; color: string };

const seatCategories = ref<SeatCategorySummary[]>([]);
const seatTables = ref<SeatTableAssignment[]>([]);
const seatConfigLoading = ref(false);
const seatConfigError = ref('');
const categoryForms = ref<Record<number, CategoryFormState>>({});
const showNewCategoryForm = ref(false);
const newCategoryForm = ref<CategoryFormState>({
  name: '',
  price: '',
  description: '',
  color: '#d4af37'
});
const newCategoryError = ref('');
const tableSelections = ref<Record<number, number | null>>({});
const tableOverrideInputs = ref<Record<number, string>>({});
const tableSeatSelection = ref<Record<number, string>>({});
const templateSelections = ref<Record<string, number | null>>({
  vip: null,
  cat1: null,
  cat2: null
});

// Танцпол
const danceFloorSeatId = ref<number | null>(null);
const danceFloorCategoryId = ref<number | null>(null);
const danceFloorCapacity = ref<number | null>(null);
const danceFloorError = ref('');
const danceFloorSuccess = ref('');
const danceFloorAssignment = ref<SeatTableAssignment | null>(null);
const danceFloorPriceOverride = ref('');
const danceFloorSeats = ref<Seat[]>([]);
const danceFloorSeatsLoading = ref(false);
const danceFloorSeatsError = ref('');
const danceFloorSeatPrices = ref<Record<number, string>>({});

// Промокоды
const promoCodes = ref<PromoCodeDto[]>([]);
const promoCodesLoading = ref(false);
const promoCodesError = ref('');
const showCreatePromoCode = ref(false);
const editingPromoCode = ref<PromoCodeDto | null>(null);
const promoCodeForm = ref<CreatePromoCodeRequest & { id?: number }>({
  code: '',
  discountPercent: 10,
  applicableCategoryIds: null,
  active: true,
  validFrom: null,
  validTo: null,
  usageLimit: null
});
const promoCodeSaving = ref(false);
const promoCodeSaveError = ref('');

const ticketSections = [
  { key: 'RESERVED', label: 'Черновики' },
  { key: 'SOLD', label: 'Проданные' },
  { key: 'CANCELLED', label: 'Отменённые' }
] as const;

const dashboardSections = computed(() => {
  const sections: Array<{
    key: string;
    title: string;
    description: string;
    icon: string;
    iconClass: string;
    link?: string;
  }> = [
    {
      key: 'tickets',
      title: 'Билеты',
      description: 'Управление билетами',
      icon: 'bi bi-ticket-perforated',
      iconClass: 'text-primary'
    },
    {
      key: 'refunds',
      title: 'Возвраты',
      description: 'Возврат проданных билетов',
      icon: 'bi bi-arrow-counterclockwise',
      iconClass: 'text-danger'
    },
    {
      key: 'reservations',
      title: 'Резервы',
      description: 'Подтверждение и отмена',
      icon: 'bi bi-calendar-check',
      iconClass: 'text-success'
    },
    {
      key: 'seat-config',
      title: 'Конфигурация зала',
      description: 'Настройка категорий и цен',
      icon: 'bi bi-gear',
      iconClass: 'text-info'
    },
    {
      key: 'users',
      title: 'Пользователи',
      description: 'Управление менеджерами',
      icon: 'bi bi-people',
      iconClass: 'text-warning'
    },
    {
      key: 'promo-codes',
      title: 'Промокоды',
      description: 'Создание и управление промокодами',
      icon: 'bi bi-tag',
      iconClass: 'text-success'
    },
    {
      key: 'concerts-link',
      title: 'Концерты',
      description: 'Создание и управление концертами',
      icon: 'bi bi-music-note-beamed',
      iconClass: 'text-purple',
      link: '/admin/concerts'
    }
  ];
  
  // Добавляем раздел для использованных билетов только для админа
  if (isAdmin.value) {
    sections.push({
      key: 'used-tickets',
      title: 'Использованные билеты',
      description: 'Просмотр и управление использованными билетами',
      icon: 'bi bi-check-circle',
      iconClass: 'text-success'
    });
  }
  
  return sections;
});

const isAuthenticated = computed(() => !!adminStore.token);
const selectedCount = computed(() => selectedTicketIds.value.size);
const hasSelection = computed(() => selectedCount.value > 0);

// Проверка, является ли пользователь админом (может видеть список пользователей)
const isAdmin = ref(false);
const checkAdminAccess = async () => {
  if (!isAuthenticated.value) {
    isAdmin.value = false;
    return;
  }
  try {
    await fetchAdminUsers();
    isAdmin.value = true;
  } catch (error: any) {
    isAdmin.value = error.response?.status !== 403;
  }
};

const ticketsByStatus = computed(() =>
  ticketSections.reduce<Record<string, Ticket[]>>((acc, section) => {
    acc[section.key] = Array.isArray(tickets.value) ? tickets.value.filter((ticket) => ticket.status === section.key) : [];
    return acc;
  }, {})
);

const users = ref<AdminUser[]>([]);
const usersLoading = ref(false);
const usersError = ref('');

// Возвраты
const refundSearch = ref({
  phone: '',
  name: '',
  ticketId: ''
});
const refundSearchResults = ref<Ticket[]>([]);
const refundSearchLoading = ref(false);
const refundSearchError = ref('');
const refundSearchPerformed = ref(false);
const refundReasons = ref<Record<string, string>>({});
const refundLoading = ref(false);

// Использованные билеты (только для админа)
const usedTickets = ref<Ticket[]>([]);
const usedTicketsLoading = ref(false);
const usedTicketsError = ref('');
const revertLoading = ref<Record<string, boolean>>({});

const getSectionCount = (key: string) => {
  switch (key) {
    case 'tickets':
      return Array.isArray(tickets.value) ? tickets.value.length : 0;
    case 'refunds':
      return Array.isArray(tickets.value) ? tickets.value.filter(t => t.status === 'SOLD').length : 0;
    case 'reservations':
      return 0; // Можно добавить счетчик резервов
    case 'seat-config':
      return seatCategories.value.length;
    case 'users':
      return users.value.length;
    case 'concerts':
    case 'concerts-link':
      return concerts.value.length;
    default:
      return 0;
  }
};

const getSectionTitle = (key: string) => {
  const section = dashboardSections.value.find(s => s.key === key);
  return section?.title || '';
};

const getSectionDescription = (key: string) => {
  const section = dashboardSections.value.find(s => s.key === key);
  return section?.description || '';
};

const openSection = (key: string) => {
  console.log('Opening section:', key);
  activeSection.value = key;
  if (key === 'tickets') {
    refreshTickets();
  } else if (key === 'seat-config') {
    console.log('Loading seat config...');
    loadSeatConfig();
  } else if (key === 'users') {
    loadUsers();
  } else if (key === 'promo-codes') {
    loadPromoCodes();
    loadUsers();
  } else if (key === 'refunds') {
    clearRefundSearch();
  } else if (key === 'used-tickets') {
    loadUsedTickets();
  } else if (key === 'concerts') {
    loadConcerts();
  }
};

const loadUsers = async () => {
  if (!isAuthenticated.value) {
    users.value = [];
    return;
  }
  usersLoading.value = true;
  usersError.value = '';
  try {
    users.value = await fetchAdminUsers();
    isAdmin.value = true; // Если удалось загрузить, значит админ
  } catch (error: any) {
    console.error('Error loading users:', error);
    if (error.response?.status === 403) {
      usersError.value = 'Недостаточно прав для просмотра списка пользователей';
      isAdmin.value = false;
    } else if (error.response?.status === 401) {
      usersError.value = 'Требуется авторизация';
    } else if (error.response?.data?.error) {
      usersError.value = error.response.data.error;
    } else {
      usersError.value = 'Не удалось загрузить список пользователей';
    }
  } finally {
    usersLoading.value = false;
  }
};

const loadUsedTickets = async () => {
  if (!isAuthenticated.value || !isAdmin.value) {
    usedTickets.value = [];
    return;
  }
  usedTicketsLoading.value = true;
  usedTicketsError.value = '';
  try {
    usedTickets.value = await fetchUsedTickets();
  } catch (error: any) {
    console.error('Error loading used tickets:', error);
    if (error.response?.status === 403) {
      usedTicketsError.value = 'Недостаточно прав для просмотра использованных билетов';
    } else if (error.response?.status === 401) {
      usedTicketsError.value = 'Требуется авторизация';
    } else {
      usedTicketsError.value = 'Не удалось загрузить список использованных билетов';
    }
  } finally {
    usedTicketsLoading.value = false;
  }
};

const handleRevertTicket = async (ticketId: string) => {
  if (!confirm('Вы уверены, что хотите вернуть этот билет в статус "Продан"? Это действие нельзя отменить.')) {
    return;
  }
  
  revertLoading.value[ticketId] = true;
  try {
    await revertTicketStatus(ticketId);
    await loadUsedTickets(); // Обновляем список
  } catch (error: any) {
    console.error('Error reverting ticket:', error);
    alert('Не удалось изменить статус билета: ' + (error.response?.data?.error || error.message || 'Неизвестная ошибка'));
  } finally {
    revertLoading.value[ticketId] = false;
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('ru-RU', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

const searchTicketsForRefund = async () => {
  if (!refundSearch.value.phone && !refundSearch.value.name && !refundSearch.value.ticketId) {
    refundSearchError.value = 'Укажите телефон, имя или ID билета';
    return;
  }
  refundSearchLoading.value = true;
  refundSearchError.value = '';
  refundSearchPerformed.value = false;
  try {
    refundSearchResults.value = await searchTickets(
      refundSearch.value.phone || undefined,
      refundSearch.value.name || undefined,
      refundSearch.value.ticketId || undefined
    );
    refundSearchPerformed.value = true;
  } catch (error: any) {
    refundSearchError.value = 'Не удалось найти билеты';
    console.error('Error searching tickets:', error);
  } finally {
    refundSearchLoading.value = false;
  }
};

const clearRefundSearch = () => {
  refundSearch.value = { phone: '', name: '', ticketId: '' };
  refundSearchResults.value = [];
  refundSearchError.value = '';
  refundSearchPerformed.value = false;
  refundReasons.value = {};
};

const refundSingleTicket = async (ticketId: string) => {
  const reason = refundReasons.value[ticketId];
  if (!reason || !reason.trim()) {
    refundSearchError.value = 'Укажите причину возврата';
    return;
  }
  refundLoading.value = true;
  refundSearchError.value = '';
  try {
    await refundTicket(ticketId, reason);
    // Обновляем список билетов
    await searchTicketsForRefund();
    // Очищаем причину для этого билета
    delete refundReasons.value[ticketId];
    refundSearchError.value = 'Билет успешно возвращён';
    setTimeout(() => {
      refundSearchError.value = '';
    }, 3000);
  } catch (error: any) {
    refundSearchError.value = error.response?.data?.error || 'Не удалось вернуть билет';
  } finally {
    refundLoading.value = false;
  }
};

const closeSection = () => {
  activeSection.value = null;
};

const bulkSelectedIds = () => Array.from(selectedTicketIds.value);

const isTicketSelected = (ticketId: string) => selectedTicketIds.value.has(ticketId);

const toggleTicketSelection = (ticketId: string) => {
  const next = new Set(selectedTicketIds.value);
  if (next.has(ticketId)) {
    next.delete(ticketId);
  } else {
    next.add(ticketId);
  }
  selectedTicketIds.value = next;
};

const clearSelection = () => {
  selectedTicketIds.value = new Set();
};

const removeFromSelection = (ticketId: string) => {
  if (!selectedTicketIds.value.has(ticketId)) {
    return;
  }
  const next = new Set(selectedTicketIds.value);
  next.delete(ticketId);
  selectedTicketIds.value = next;
};

const selectAllReserved = () => {
  const ids = ticketsByStatus.value.RESERVED.map((ticket) => ticket.id);
  selectedTicketIds.value = new Set(ids);
};

const handleLogin = async () => {
  await adminStore.signIn(username.value, password.value);
};

const handleChangePassword = async () => {
  changePasswordLoading.value = true;
  changePasswordError.value = '';
  changePasswordSuccess.value = '';
  
  try {
    await changePassword({
      currentPassword: changePasswordForm.value.currentPassword,
      newPassword: changePasswordForm.value.newPassword
    });
    changePasswordSuccess.value = 'Пароль успешно изменен!';
    changePasswordForm.value = { currentPassword: '', newPassword: '' };
    setTimeout(() => {
      changePasswordSuccess.value = '';
    }, 5000);
  } catch (error: any) {
    if (error.response?.data?.error) {
      changePasswordError.value = error.response.data.error;
    } else {
      changePasswordError.value = 'Не удалось изменить пароль. Проверьте правильность текущего пароля.';
    }
  } finally {
    changePasswordLoading.value = false;
  }
};

const handleRequestRegistration = async () => {
  registrationLoading.value = true;
  registrationError.value = '';
  registrationMessage.value = '';
  
  try {
    await requestManagerRegistration({
      username: registrationForm.value.username,
      password: registrationForm.value.password
    });
    registrationMessage.value = 'Код подтверждения отправлен в Telegram бот. Проверьте сообщения.';
    confirmForm.value.username = registrationForm.value.username;
    registrationStep.value = 'confirm';
    setTimeout(() => {
      registrationMessage.value = '';
    }, 10000);
  } catch (error: any) {
    if (error.response?.data?.error) {
      registrationError.value = error.response.data.error;
    } else {
      registrationError.value = 'Не удалось отправить запрос на регистрацию.';
    }
  } finally {
    registrationLoading.value = false;
  }
};

const handleConfirmRegistration = async () => {
  confirmLoading.value = true;
  confirmError.value = '';
  confirmMessage.value = '';
  
  try {
    await confirmManagerRegistration({
      username: confirmForm.value.username,
      verificationCode: confirmForm.value.verificationCode
    });
    confirmMessage.value = 'Менеджер успешно зарегистрирован!';
    setTimeout(() => {
      resetRegistration();
    }, 3000);
  } catch (error: any) {
    if (error.response?.data?.error) {
      confirmError.value = error.response.data.error;
    } else {
      confirmError.value = 'Не удалось подтвердить регистрацию. Проверьте код.';
    }
  } finally {
    confirmLoading.value = false;
  }
};

const resetRegistration = () => {
  registrationStep.value = 'request';
  registrationForm.value = { username: '', password: '' };
  confirmForm.value = { username: '', verificationCode: '' };
  registrationError.value = '';
  registrationMessage.value = '';
  confirmError.value = '';
  confirmMessage.value = '';
};

const confirmReservation = async () => {
  const id = reservationId.value.trim();
  if (!id) {
    return;
  }
  pendingAction.value = true;
  actionMessage.value = '';
  try {
    await apiClient.post(`/admin/reservations/${id}/confirm`);
    actionMessage.value = 'Резерв подтверждён';
    reservationId.value = '';
  } finally {
    pendingAction.value = false;
  }
};

const cancelReservation = async () => {
  const id = cancelReservationId.value.trim();
  if (!id) {
    return;
  }
  pendingAction.value = true;
  actionMessage.value = '';
  try {
    await apiClient.post(`/admin/reservations/${id}/cancel`, {
      operator: 'admin-ui',
      reason: cancelReason.value
    });
    actionMessage.value = 'Резерв отменён';
    cancelReservationId.value = '';
  } finally {
    pendingAction.value = false;
  }
};

const refreshTickets = async () => {
  if (!isAuthenticated.value) {
    tickets.value = [];
    return;
  }
  ticketsLoading.value = true;
  ticketError.value = '';
  try {
    const ticketsData = await fetchTickets();
    // Убеждаемся, что это массив
    tickets.value = Array.isArray(ticketsData) ? ticketsData : [];
  } catch (error) {
    ticketError.value = 'Не удалось загрузить билеты';
    tickets.value = [];
  } finally {
    ticketsLoading.value = false;
  }
};

const handleTicketConfirm = async (ticketId: string) => {
  ticketsLoading.value = true;
  try {
    await confirmTicket(ticketId);
    removeFromSelection(ticketId);
    await refreshTickets();
  } catch (error) {
    ticketError.value = 'Не удалось подтвердить билет';
  } finally {
    ticketsLoading.value = false;
  }
};

const handleTicketCancel = async (ticketId: string) => {
  const reason = prompt('Причина отмены?', 'admin cancel') || 'admin cancel';
  ticketsLoading.value = true;
  try {
    await cancelTicket(ticketId, reason);
    removeFromSelection(ticketId);
    await refreshTickets();
  } catch (error) {
    ticketError.value = 'Не удалось отменить билет';
  } finally {
    ticketsLoading.value = false;
  }
};

const confirmSelectedTickets = async () => {
  if (!hasSelection.value) {
    return;
  }
  ticketsLoading.value = true;
  ticketError.value = '';
  try {
    await confirmTicketsBulk(bulkSelectedIds());
    clearSelection();
    await refreshTickets();
  } catch (error) {
    ticketError.value = 'Не удалось подтвердить выбранные билеты';
  } finally {
    ticketsLoading.value = false;
  }
};

const cancelSelectedTickets = async () => {
  if (!hasSelection.value) {
    return;
  }
  const reason = prompt('Причина отмены выбранных?', 'admin cancel') || 'admin cancel';
  ticketsLoading.value = true;
  ticketError.value = '';
  try {
    await cancelTicketsBulk(bulkSelectedIds(), reason);
    clearSelection();
    await refreshTickets();
  } catch (error) {
    ticketError.value = 'Не удалось отменить выбранные билеты';
  } finally {
    ticketsLoading.value = false;
  }
};

const formatSeat = (ticket: Ticket) =>
  ticket.seat ? `Стол ${ticket.seat.tableNumber}, место ${ticket.seat.chairNumber}` : '—';

const formatRub = (cents: number) =>
  new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(
    cents / 100
  );

const loadPromoCodes = async () => {
  promoCodesLoading.value = true;
  promoCodesError.value = '';
  try {
    promoCodes.value = await fetchPromoCodes();
  } catch (error) {
    promoCodesError.value = 'Не удалось загрузить промокоды';
  } finally {
    promoCodesLoading.value = false;
  }
};

const editPromoCode = (promoCode: PromoCodeDto) => {
  editingPromoCode.value = promoCode;
  promoCodeForm.value = {
    code: promoCode.code,
    discountPercent: promoCode.discountPercent,
    applicableCategoryIds: promoCode.applicableCategoryIds && promoCode.applicableCategoryIds.length > 0 
      ? [...promoCode.applicableCategoryIds] 
      : null,
    active: promoCode.active,
    validFrom: promoCode.validFrom ? new Date(promoCode.validFrom).toISOString().slice(0, 16) : null,
    validTo: promoCode.validTo ? new Date(promoCode.validTo).toISOString().slice(0, 16) : null,
    usageLimit: promoCode.usageLimit || null,
    id: promoCode.id
  };
  showCreatePromoCode.value = true;
};

const closePromoCodeModal = () => {
  showCreatePromoCode.value = false;
  editingPromoCode.value = null;
  promoCodeForm.value = {
    code: '',
    discountPercent: 10,
    applicableCategoryIds: null,
    active: true,
    validFrom: null,
    validTo: null,
    usageLimit: null
  };
  promoCodeSaveError.value = '';
};

const savePromoCode = async () => {
  promoCodeSaving.value = true;
  promoCodeSaveError.value = '';
  try {
    if (editingPromoCode.value) {
      const updateData: UpdatePromoCodeRequest = {
        code: promoCodeForm.value.code.toUpperCase(),
        discountPercent: promoCodeForm.value.discountPercent,
        applicableCategoryIds: promoCodeForm.value.applicableCategoryIds && promoCodeForm.value.applicableCategoryIds.length > 0
          ? promoCodeForm.value.applicableCategoryIds
          : null,
        active: promoCodeForm.value.active,
        validFrom: promoCodeForm.value.validFrom ? new Date(promoCodeForm.value.validFrom).toISOString() : null,
        validTo: promoCodeForm.value.validTo ? new Date(promoCodeForm.value.validTo).toISOString() : null,
        usageLimit: promoCodeForm.value.usageLimit || null
      };
      await updatePromoCode(editingPromoCode.value.id, updateData);
    } else {
      const createData: CreatePromoCodeRequest = {
        code: promoCodeForm.value.code.toUpperCase(),
        discountPercent: promoCodeForm.value.discountPercent,
        applicableCategoryIds: promoCodeForm.value.applicableCategoryIds && promoCodeForm.value.applicableCategoryIds.length > 0
          ? promoCodeForm.value.applicableCategoryIds
          : null,
        active: promoCodeForm.value.active,
        validFrom: promoCodeForm.value.validFrom ? new Date(promoCodeForm.value.validFrom).toISOString() : null,
        validTo: promoCodeForm.value.validTo ? new Date(promoCodeForm.value.validTo).toISOString() : null,
        usageLimit: promoCodeForm.value.usageLimit || null
      };
      await createPromoCode(createData);
    }
    
    await loadPromoCodes();
    closePromoCodeModal();
  } catch (error: any) {
    promoCodeSaveError.value = error.response?.data?.message || 'Не удалось сохранить промокод';
  } finally {
    promoCodeSaving.value = false;
  }
};

const deletePromoCodeHandler = async (id: number) => {
  if (!confirm('Вы уверены, что хотите удалить этот промокод?')) {
    return;
  }
  try {
    await deletePromoCode(id);
    await loadPromoCodes();
  } catch (error) {
    promoCodesError.value = 'Не удалось удалить промокод';
  }
};

// Концерты
const concerts = ref<Concert[]>([]);
const concertsLoading = ref(false);
const concertsError = ref('');
const showCreateConcert = ref(false);
const editingConcert = ref<Concert | null>(null);
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
const concertSaving = ref(false);
const concertSaveError = ref('');
const concertSaveSuccess = ref('');
const posterUploading = ref(false);
const schemeUploading = ref(false);

const loadConcerts = async () => {
  concertsLoading.value = true;
  concertsError.value = '';
  try {
    concerts.value = await fetchAllConcerts();
  } catch (error: any) {
    console.error('Error loading concerts:', error);
    concertsError.value = error.response?.data?.error || error.message || 'Не удалось загрузить концерты';
  } finally {
    concertsLoading.value = false;
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
  showCreateConcert.value = true;
};

const closeConcertModal = () => {
  showCreateConcert.value = false;
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
  concertSaveError.value = '';
  concertSaveSuccess.value = '';
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
  try {
    const url = await uploadPoster(file);
    concertForm.value.posterUrl = url;
  } catch (error: any) {
    concertSaveError.value = 'Не удалось загрузить афишу: ' + (error.response?.data?.error || error.message);
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
    concertSaveError.value = 'Только SVG файлы разрешены для схемы';
    return;
  }
  
  schemeUploading.value = true;
  try {
    const url = await uploadScheme(file);
    concertForm.value.salesSchemeUrl = url;
  } catch (error: any) {
    concertSaveError.value = 'Не удалось загрузить схему: ' + (error.response?.data?.error || error.message);
  } finally {
    schemeUploading.value = false;
    input.value = '';
  }
};

const saveConcert = async () => {
  if (!concertForm.value.title || !concertForm.value.slug) {
    concertSaveError.value = 'Название и slug обязательны';
    return;
  }
  
  concertSaving.value = true;
  concertSaveError.value = '';
  concertSaveSuccess.value = '';
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
      concertSaveSuccess.value = 'Концерт успешно обновлен';
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
      concertSaveSuccess.value = 'Концерт успешно создан';
    }
    
    // Обновляем список концертов
    await loadConcerts();
    
    // Закрываем модальное окно через небольшую задержку, чтобы показать сообщение об успехе
    setTimeout(() => {
      closeConcertModal();
    }, 1000);
  } catch (error: any) {
    console.error('Error saving concert:', error);
    const errorMessage = error.response?.data?.error || error.response?.data?.message || error.message || 'Не удалось сохранить концерт';
    concertSaveError.value = errorMessage;
    // Не закрываем модальное окно при ошибке, чтобы пользователь мог исправить данные
  } finally {
    concertSaving.value = false;
  }
};

const deleteConcertHandler = async (id: number) => {
  if (!confirm('Вы уверены, что хотите удалить этот концерт? Это действие нельзя отменить.')) {
    return;
  }
  try {
    await deleteConcert(id);
    await loadConcerts();
  } catch (error: any) {
    console.error('Error deleting concert:', error);
    concertsError.value = error.response?.data?.error || error.message || 'Не удалось удалить концерт';
  }
};

const getCategoryDisplayName = (category: SeatCategorySummary) => {
  // Убираем цену из названия категории (удаляем все цифры, пробелы и символ ₽)
  return category.name.replace(/\s*\d+[\s₽]*/g, '').trim() || category.name;
};

const displayPriceInput = (cents?: number | null) => (cents != null ? Math.round(cents / 100).toString() : '');

const parsePriceInput = (value?: string) => {
  if (!value || !value.toString().trim()) {
    return null;
  }
  const numeric = Number(value);
  if (Number.isNaN(numeric)) {
    return null;
  }
  return Math.round(numeric * 100);
};

const CATEGORY_COLOR_FALLBACKS = ['#d4af37', '#1f9d6c', '#6f42c1', '#ff8c42', '#4361ee'];

const normalizeHexColor = (value?: string | null) => {
  if (!value) return null;
  const trimmed = value.trim();
  if (!trimmed) return null;
  const prefixed = trimmed.startsWith('#') ? trimmed : `#${trimmed}`;
  return /^#[0-9A-Fa-f]{6}$/.test(prefixed) ? prefixed.toUpperCase() : null;
};

const ensureColorValue = (value: string | null | undefined, fallback: string) =>
  normalizeHexColor(value) ?? fallback;

const selectColorValue = (event: Event) => {
  const target = event.target as HTMLInputElement | null;
  target?.select();
};

const loadSeatConfig = async () => {
  if (!isAuthenticated.value) {
    seatCategories.value = [];
    seatTables.value = [];
    categoryForms.value = {};
    tableSelections.value = {};
    tableOverrideInputs.value = {};
    tableSeatSelection.value = {};
    return;
  }
  seatConfigLoading.value = true;
  seatConfigError.value = '';
  try {
    const [categories, tables] = await Promise.all([fetchSeatCategories(), fetchSeatTableAssignments()]);
    // Нормализуем данные - убеждаемся, что это массивы
    const categoriesArray = Array.isArray(categories) ? categories : [];
    const tablesArray = Array.isArray(tables) ? tables : [];
    
    seatCategories.value = categoriesArray;
    seatTables.value = tablesArray;
    const categoryState: Record<number, CategoryFormState> = {};
    categoriesArray.forEach((category, index) => {
      categoryState[category.id] = {
        name: category.name,
        price: displayPriceInput(category.priceCents),
        description: category.description || '',
        color: ensureColorValue(
          category.colorHex,
          CATEGORY_COLOR_FALLBACKS[index % CATEGORY_COLOR_FALLBACKS.length]
        )
      };
    });
    categoryForms.value = categoryState;
    const selectionState: Record<number, number | null> = {};
    const overrideState: Record<number, string> = {};
    const seatState: Record<number, string> = {};
    tablesArray.forEach((table) => {
      selectionState[table.tableNumber] = table.seatCategoryId;
      overrideState[table.tableNumber] = displayPriceInput(table.overridePriceCents ?? null);
      seatState[table.tableNumber] = '';
    });
    tableSelections.value = selectionState;
    tableOverrideInputs.value = overrideState;
    tableSeatSelection.value = seatState;
  } catch (error: any) {
    console.error('Error loading seat config:', error);
    const errorMessage = error.response?.data?.error || error.response?.data?.message || error.message || 'Не удалось загрузить конфигурацию зала';
    seatConfigError.value = errorMessage;
    if (error.response?.status === 401 || error.response?.status === 403) {
      seatConfigError.value = 'Доступ запрещен. Убедитесь, что вы авторизованы и имеете права доступа.';
    }
  } finally {
    seatConfigLoading.value = false;
  }
};

const saveCategory = async (categoryId: number) => {
  const form = categoryForms.value[categoryId];
  if (!form) return;
  const priceCents = parsePriceInput(form.price);
  if (priceCents === null || priceCents < 0) {
    seatConfigError.value = 'Укажите корректную цену (в рублях)';
    return;
  }
  const colorHex = normalizeHexColor(form.color);
  if (!colorHex) {
    seatConfigError.value = 'Укажите цвет в формате #RRGGBB';
    return;
  }
  seatConfigError.value = '';
  try {
    await updateSeatCategory(categoryId, {
      name: form.name.trim() || 'Категория',
      priceCents,
      description: form.description,
      colorHex
    });
    await loadSeatConfig();
  } catch (error) {
    seatConfigError.value = 'Не удалось обновить категорию';
  }
};

const createNewCategory = async () => {
  if (!newCategoryForm.value.name.trim()) {
    newCategoryError.value = 'Укажите название категории';
    return;
  }
  const priceCents = parsePriceInput(newCategoryForm.value.price);
  if (priceCents === null || priceCents < 0) {
    newCategoryError.value = 'Укажите корректную цену (в рублях)';
    return;
  }
  const colorHex = normalizeHexColor(newCategoryForm.value.color);
  if (!colorHex) {
    newCategoryError.value = 'Укажите цвет в формате #RRGGBB';
    return;
  }
  newCategoryError.value = '';
  seatConfigLoading.value = true;
  try {
    await createSeatCategory({
      name: newCategoryForm.value.name.trim(),
      priceCents,
      description: newCategoryForm.value.description || null,
      colorHex
    });
    await loadSeatConfig();
    cancelNewCategory();
  } catch (error: any) {
    newCategoryError.value = error.response?.data?.message || 'Не удалось создать категорию';
  } finally {
    seatConfigLoading.value = false;
  }
};

const cancelNewCategory = () => {
  showNewCategoryForm.value = false;
  newCategoryForm.value = {
    name: '',
    price: '',
    description: '',
    color: '#d4af37'
  };
  newCategoryError.value = '';
};

const assignTables = async (tableNumbers: number[], categoryId: number | null) => {
  if (!categoryId || tableNumbers.length === 0) {
    seatConfigError.value = 'Выберите категорию';
    return;
  }
  seatConfigError.value = '';
  try {
    await assignSeatCategory({
      seatCategoryId: categoryId,
      tableNumbers
    });
    await loadSeatConfig();
  } catch (error) {
    seatConfigError.value = 'Не удалось применить категорию к столам';
  }
};

const applyTableSettings = async (tableNumber: number) => {
  const categoryId = tableSelections.value[tableNumber] ?? null;
  const priceOverride = tableOverrideInputs.value[tableNumber] ?
    parsePriceInput(tableOverrideInputs.value[tableNumber]) : null;

  try {
    // Применяем категорию
    if (categoryId !== null) {
      await assignTables([tableNumber], categoryId);
    }

    // Применяем переопределение цены, если указано
    if (priceOverride !== null) {
      await overrideSeatPrice({
        tableNumber,
        priceCents: priceOverride
      });
    }

    await loadSeatConfig(); // Перезагружаем данные
  } catch (error) {
    seatConfigError.value = 'Не удалось применить настройки стола';
  }
};

const applyDanceFloorSettings = async () => {
  try {
    const categoryId = danceFloorCategoryId.value;
    const priceOverride = danceFloorPriceOverride.value ?
      parsePriceInput(danceFloorPriceOverride.value) : null;

    // Применяем категорию для танцпола
    if (categoryId !== null) {
      await assignTables([0], categoryId);
    }

    // Применяем переопределение цены, если указано
    if (priceOverride !== null) {
      await overrideSeatPrice({
        tableNumber: 0,
        priceCents: priceOverride
      });
    }

    await loadSeatConfig(); // Перезагружаем данные
  } catch (error) {
    seatConfigError.value = 'Не удалось применить настройки танцпола';
  }
};

const loadDanceFloorSeats = async () => {
  if (!danceFloorAssignment.value) return;

  danceFloorSeatsLoading.value = true;
  danceFloorSeatsError.value = '';

  try {
    // Получаем первый концерт
    const concerts = await fetchAllConcerts();
    if (concerts.length === 0) {
      danceFloorSeatsError.value = 'Нет доступных концертов';
      return;
    }

    const concertId = concerts[0].id;
    const allSeats = await fetchSeats(concertId);

    // Фильтруем только места танцпола (tableNumber = 0)
    danceFloorSeats.value = allSeats.filter(seat => seat.tableNumber === 0);

    // Инициализируем цены для редактирования
    danceFloorSeatPrices.value = {};
    danceFloorSeats.value.forEach(seat => {
      danceFloorSeatPrices.value[seat.id] = seat.priceOverrideCents ?
        seat.priceOverrideCents.toString() : seat.priceCents.toString();
    });
  } catch (error: any) {
    console.error('Error loading dance floor seats:', error);
    danceFloorSeatsError.value = 'Не удалось загрузить места танцпола';
  } finally {
    danceFloorSeatsLoading.value = false;
  }
};

const blockDanceFloorSeat = async (seatId: number) => {
  try {
    await blockSeat(seatId);
    await loadDanceFloorSeats(); // Перезагружаем список
  } catch (error: any) {
    console.error('Error blocking dance floor seat:', error);
    danceFloorSeatsError.value = 'Не удалось заблокировать место';
  }
};

const unblockDanceFloorSeat = async (seatId: number) => {
  try {
    await unblockSeat(seatId);
    await loadDanceFloorSeats(); // Перезагружаем список
  } catch (error: any) {
    console.error('Error unblocking dance floor seat:', error);
    danceFloorSeatsError.value = 'Не удалось разблокировать место';
  }
};

const updateDanceFloorSeatPrice = async (seatId: number) => {
  const priceString = danceFloorSeatPrices.value[seatId];
  if (!priceString) return;

  const priceCents = parsePriceInput(priceString);
  if (priceCents === null) return;

  try {
    await overrideSeatPrice({
      seatId,
      priceCents
    });
    await loadDanceFloorSeats(); // Перезагружаем список
  } catch (error: any) {
    console.error('Error updating dance floor seat price:', error);
    danceFloorSeatsError.value = 'Не удалось обновить цену места';
  }
};

const applyTableCategory = async (tableNumber: number) => {
  await assignTables([tableNumber], tableSelections.value[tableNumber] ?? null);
};

const createDanceFloorSeats = async () => {
  if (!danceFloorSeatId.value || !danceFloorCategoryId.value || !danceFloorCapacity.value) {
    danceFloorError.value = 'Заполните все поля';
    return;
  }

  if (danceFloorCapacity.value < 1) {
    danceFloorError.value = 'Количество билетов должно быть больше 0';
    return;
  }

  danceFloorError.value = '';
  danceFloorSuccess.value = '';
  seatConfigLoading.value = true;

  try {
    // Получаем текущий концерт по slug из URL
    const currentPath = window.location.pathname;
    const slug = currentPath.startsWith('/') ? currentPath.substring(1) : currentPath;

    if (!slug) {
      danceFloorError.value = 'Не удалось определить текущий концерт. Убедитесь, что вы находитесь на странице концерта.';
      return;
    }

    const currentConcert = await fetchConcertBySlug(slug);
    if (!currentConcert) {
      danceFloorError.value = 'Концерт не найден.';
      return;
    }

    const concertId = currentConcert.id;

    // Создаем места для танцпола
    // tableNumber = 0 для танцпола, chairNumber от 1 до capacity
    await bulkCreateSeats({
      concertId,
      categoryId: danceFloorCategoryId.value,
      tables: [{
        tableNumber: 0,
        chairsCount: danceFloorCapacity.value
      }]
    });

    danceFloorSuccess.value = `Успешно создано ${danceFloorCapacity.value} мест для танцпола (ID кружка: ${danceFloorSeatId.value})`;

    // Очищаем форму
    danceFloorSeatId.value = null;
    danceFloorCategoryId.value = null;
    danceFloorCapacity.value = null;

    // Обновляем конфигурацию
    await loadSeatConfig();

    // Если мы на странице концерта, перезагружаем места
    if (window.location.pathname.startsWith('/')) {
      // Обновляем store с местами для текущего концерта
      const seatStore = useSeatStore();
      await seatStore.loadSeats();
    }

    // Очищаем сообщение об успехе через 5 секунд
    setTimeout(() => {
      danceFloorSuccess.value = '';
    }, 5000);
  } catch (error: any) {
    console.error('Error creating dance floor seats:', error);
    danceFloorError.value = error.response?.data?.error || error.response?.data?.message || 'Не удалось создать места для танцпола';
  } finally {
    seatConfigLoading.value = false;
  }
};

onMounted(() => {
  if (isAuthenticated.value) {
    refreshTickets();
    loadSeatConfig();
    checkAdminAccess();
    
    // Проверяем query параметр для автоматического открытия раздела
    const section = route.query.section as string;
    if (section) {
      openSection(section);
    }
  }
});

watch(isAuthenticated, (newVal) => {
  if (newVal) {
    checkAdminAccess();
    // Проверяем query параметр для автоматического открытия раздела после авторизации
    const section = route.query.section as string;
    if (section) {
      // Небольшая задержка, чтобы данные успели загрузиться
      setTimeout(() => {
        openSection(section);
      }, 100);
    }
  } else {
    isAdmin.value = false;
  }
});

watch(tickets, (list) => {
  if (!Array.isArray(list)) {
    return;
  }
  const reservedIds = new Set(list.filter((ticket) => ticket.status === 'RESERVED').map((ticket) => ticket.id));
  const filtered = new Set<string>();
  selectedTicketIds.value.forEach((id) => {
    if (reservedIds.has(id)) {
      filtered.add(id);
    }
  });
  if (filtered.size !== selectedTicketIds.value.size) {
    selectedTicketIds.value = filtered;
  }
});

watch(isAuthenticated, (val) => {
  if (val) {
    refreshTickets();
    loadSeatConfig();
  } else {
    tickets.value = [];
    seatCategories.value = [];
    seatTables.value = [];
    users.value = [];
    clearSelection();
    activeSection.value = null;
  }
});
</script>

<style scoped>
.admin-dashboard {
  min-height: calc(100vh - 200px);
}

.dashboard-card {
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent !important;
}

.dashboard-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

.dashboard-card.active {
  border-color: #18723F !important;
  background: #f8f9fa;
}

.section-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.05);
  font-size: 24px;
}

.sidebar-panel {
  position: sticky;
  top: 20px;
  height: fit-content;
  max-height: calc(100vh - 40px);
}

.qr-preview img {
  max-height: 180px;
  object-fit: contain;
}

.seat-table-config {
  max-height: 300px;
  overflow: auto;
}

.category-color-dot {
  width: 14px;
  height: 14px;
  border-radius: 999px;
  border: 1px solid rgba(0, 0, 0, 0.2);
  display: inline-block;
}

.category-color-picker {
  width: 48px;
  min-width: 48px;
  padding: 0;
  height: 32px;
}

.text-purple {
  color: #6f42c1;
}
</style>
