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
                <div 
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
                    <h4 class="h6 mb-3">Категории</h4>
                  <div v-if="seatCategories.length === 0" class="text-body-secondary small">Категории недоступны.</div>
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
                          <th>Стол</th>
                          <th>Категория</th>
                            <th>Действие</th>
                        </tr>
                      </thead>
                      <tbody>
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
                            <button
                              class="btn btn-sm btn-outline-primary"
                              :disabled="seatConfigLoading"
                              @click="applyTableCategory(assignment.tableNumber)"
                            >
                              Применить
                            </button>
                          </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <p class="text-body-secondary small">Всего столов: {{ seatTables.length }}</p>
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
import { useAdminStore } from '../stores/adminStore';
import {
  apiClient,
  fetchTickets,
  confirmTicket,
  cancelTicket,
  confirmTicketsBulk,
  cancelTicketsBulk,
  fetchSeatCategories,
  fetchSeatTableAssignments,
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
  revertTicketStatus
} from '../services/api';
import type { Ticket, SeatCategorySummary, SeatTableAssignment, AdminUser } from '../types';

const adminStore = useAdminStore();

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
const tableSelections = ref<Record<number, number | null>>({});
const tableOverrideInputs = ref<Record<number, string>>({});
const tableSeatSelection = ref<Record<number, string>>({});
const templateSelections = ref<Record<string, number | null>>({
  vip: null,
  cat1: null,
  cat2: null
});

const ticketSections = [
  { key: 'RESERVED', label: 'Черновики' },
  { key: 'SOLD', label: 'Проданные' },
  { key: 'CANCELLED', label: 'Отменённые' }
] as const;

const dashboardSections = computed(() => {
  const sections = [
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
    acc[section.key] = tickets.value.filter((ticket) => ticket.status === section.key);
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
      return tickets.value.length;
    case 'refunds':
      return tickets.value.filter(t => t.status === 'SOLD').length;
    case 'reservations':
      return 0; // Можно добавить счетчик резервов
    case 'seat-config':
      return seatCategories.value.length;
    case 'users':
      return users.value.length;
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
  activeSection.value = key;
  if (key === 'tickets') {
    refreshTickets();
  } else if (key === 'seat-config') {
    loadSeatConfig();
  } else if (key === 'users') {
    loadUsers();
  } else if (key === 'refunds') {
    clearRefundSearch();
  } else if (key === 'used-tickets') {
    loadUsedTickets();
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
    tickets.value = await fetchTickets();
  } catch (error) {
    ticketError.value = 'Не удалось загрузить билеты';
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
  `Стол ${ticket.seat.tableNumber}, место ${ticket.seat.chairNumber}`;

const formatRub = (cents: number) =>
  new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(
    cents / 100
  );

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
    seatCategories.value = categories;
    seatTables.value = tables;
    const categoryState: Record<number, CategoryFormState> = {};
    categories.forEach((category, index) => {
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
    tables.forEach((table) => {
      selectionState[table.tableNumber] = table.seatCategoryId;
      overrideState[table.tableNumber] = displayPriceInput(table.overridePriceCents ?? null);
      seatState[table.tableNumber] = '';
    });
    tableSelections.value = selectionState;
    tableOverrideInputs.value = overrideState;
    tableSeatSelection.value = seatState;
  } catch (error) {
    seatConfigError.value = 'Не удалось загрузить конфигурацию зала';
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

const applyTableCategory = async (tableNumber: number) => {
  await assignTables([tableNumber], tableSelections.value[tableNumber] ?? null);
};

onMounted(() => {
  if (isAuthenticated.value) {
    refreshTickets();
    loadSeatConfig();
    checkAdminAccess();
  }
});

watch(isAuthenticated, (newVal) => {
  if (newVal) {
    checkAdminAccess();
  } else {
    isAdmin.value = false;
  }
});

watch(tickets, (list) => {
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
</style>
