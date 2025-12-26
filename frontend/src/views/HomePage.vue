<template>
  <div class="landing-page">
    <ConcertHero
      v-if="concert"
      :title="concert.title"
      :description="concert.description"
      :date="formattedDate"
      :venue="concert.venue"
      @cta="openModal"
    />

    <!-- Почему не дорого -->
    <section class="why-not-expensive-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="why-card">
          <div class="why-header">
            <div class="why-icon-wrapper">
              <i class="bi bi-question-circle-fill"></i>
            </div>
            <h2 class="why-title">Почему не дорого?</h2>
          </div>
          <div class="why-content">
            <div class="why-question-block">
              <p class="why-question">Частый вопрос: «Почему так дорого?»</p>
              <p class="why-answer">Ответ: Это не билет на концерт. Это полноценный вечер:</p>
            </div>
            
            <div class="why-features-grid">
              <div class="why-feature-item" v-for="(feature, index) in whyFeatures" :key="index" :style="{ animationDelay: (index * 0.1) + 's' }">
                <div class="why-feature-icon">
                  <i :class="feature.icon"></i>
                </div>
                <div class="why-feature-content">
                  <span class="why-feature-text">{{ feature.text }}</span>
                </div>
              </div>
            </div>
            
            <div class="why-highlight-block">
              <div class="why-highlight-icon">
                <i class="bi bi-star-fill"></i>
              </div>
              <p class="why-highlight-text">По факту вы платите за отдых, а не за вход</p>
            </div>
            
            <div class="why-cta-wrapper">
              <button class="btn why-cta-button" @click="openModal">
                <span class="why-cta-icon">👉</span>
                <span class="why-cta-text">Купить билет сейчас</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Кому стоит идти -->
    <section class="who-should-go-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="who-card">
          <div class="who-header">
            <div class="who-icon-wrapper">
              <i class="bi bi-people-fill"></i>
            </div>
            <h2 class="who-title">Кому стоит идти</h2>
          </div>
          <div class="who-content">
            <div class="who-intro-block">
              <p class="who-intro">Этот вечер для вас, если вы хотите:</p>
            </div>
            
            <div class="who-features-grid">
              <div class="who-feature-item" v-for="(feature, index) in whoFeatures" :key="index" :style="{ animationDelay: (index * 0.1) + 's' }">
                <div class="who-feature-icon">
                  <i :class="feature.icon"></i>
                </div>
                <div class="who-feature-content">
                  <span class="who-feature-text">{{ feature.text }}</span>
                </div>
              </div>
            </div>
            
            <div class="who-note-block">
              <div class="who-note-icon">
                <i class="bi bi-info-circle-fill"></i>
              </div>
              <p class="who-note-text"><strong>Важно:</strong> Мероприятие 16+</p>
            </div>
            
            <div class="who-cta-wrapper">
              <button class="btn who-cta-button" @click="openModal">
                <span class="who-cta-icon">👉</span>
                <span class="who-cta-text">Купить билет сейчас</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Не будет скучно -->
    <section class="not-boring-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="not-boring-card">
          <div class="not-boring-header">
            <div class="not-boring-icon-wrapper">
              <i class="bi bi-music-note-beamed"></i>
            </div>
            <h2 class="not-boring-title">Не будет скучно</h2>
          </div>
          <div class="not-boring-content">
            <div class="not-boring-intro-block">
              <p class="not-boring-intro">Что будет происходить весь вечер:</p>
            </div>
            
            <div class="not-boring-events-grid">
              <div class="not-boring-event-item" v-for="(event, index) in notBoringEvents" :key="index" :style="{ animationDelay: (index * 0.1) + 's' }">
                <div class="not-boring-event-icon">
                  <i :class="event.icon"></i>
                </div>
                <div class="not-boring-event-content">
                  <span class="not-boring-event-text">{{ event.text }}</span>
                </div>
              </div>
            </div>
            
            <div class="not-boring-highlight-block">
              <div class="not-boring-highlight-icon">
                <i class="bi bi-clock-fill"></i>
              </div>
              <p class="not-boring-highlight-text">3 часа без пауз и пустых моментов</p>
            </div>
            
            <div class="not-boring-cta-wrapper">
              <button class="btn not-boring-cta-button" @click="openModal">
                <span class="not-boring-cta-icon">👉</span>
                <span class="not-boring-cta-text">Купить билет сейчас</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Артист -->
    <section class="artist-section py-4 py-md-3 py-sm-2 bg-gradient position-relative overflow-hidden text-white">
      <div class="container">
        <div class="artist-header text-center mb-4">
          <div class="artist-section-icon-wrapper">
            <i class="bi bi-music-player-fill"></i>
          </div>
          <h2 class="artist-section-title">Артист</h2>
        </div>
        
        <div class="artists-grid">
          <div 
            class="artist-card" 
            v-for="(artist, index) in artists" 
            :key="index"
            :style="{ animationDelay: (index * 0.15) + 's' }"
          >
            <div class="artist-card-image-wrapper">
              <img 
                :src="artist.image" 
                :alt="artist.name"
                class="artist-card-image"
              />
            </div>
            <div class="artist-card-content">
              <h3 class="artist-card-name">{{ artist.name }}</h3>
              <p class="artist-card-role">{{ artist.role }}</p>
              <ul class="artist-card-features">
                <li v-for="(feature, fIndex) in artist.features" :key="fIndex">
                  <i class="bi bi-check-circle-fill"></i>
                  <span>{{ feature }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <div class="artist-cta-wrapper text-center mt-4">
          <button class="btn artist-cta-button" @click="openModal">
            <span class="artist-cta-icon">👉</span>
            <span class="artist-cta-text">Купить билет сейчас</span>
          </button>
        </div>
      </div>
    </section>

    <!-- Еда - без экономии -->
    <section class="food-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="food-card">
          <div class="food-header">
            <div class="food-icon-wrapper">
              <i class="bi bi-egg-fried"></i>
            </div>
            <h2 class="food-title">Еда — без экономии</h2>
          </div>
          <div class="food-content">
            <div class="food-intro-block">
              <p class="food-intro">Меню включено в билет</p>
            </div>
            
            <div class="food-categories-grid">
              <div 
                class="food-category-card" 
                v-for="(category, index) in foodCategories" 
                :key="index"
                :style="{ animationDelay: (index * 0.1) + 's' }"
              >
                <div class="food-category-icon">
                  <i :class="category.icon"></i>
                </div>
                <h4 class="food-category-title">{{ category.title }}</h4>
              <ul class="food-category-list">
                <li v-for="(item, itemIndex) in category.items" :key="itemIndex">
                  <i class="bi bi-star-fill"></i>
                  <span>{{ item }}</span>
                </li>
              </ul>
              </div>
            </div>
            
            <div class="food-notes-block">
              <div class="food-note-item">
                <i class="bi bi-pin-fill"></i>
                <span>Рассадка за столами</span>
              </div>
              <div class="food-note-item">
                <i class="bi bi-pin-fill"></i>
                <span>Доплат на месте нет</span>
              </div>
            </div>
            
            <div class="food-cta-wrapper">
              <button class="btn food-cta-button" @click="openModal">
                <span class="food-cta-icon">👉</span>
                <span class="food-cta-text">Купить билет</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Билеты -->
    <section class="tickets-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="tickets-card">
          <div class="tickets-header">
            <div class="tickets-icon-wrapper">
              <i class="bi bi-ticket-perforated-fill"></i>
            </div>
            <h2 class="tickets-title">Билеты</h2>
          </div>
          <div class="tickets-content">
            <div class="tickets-intro-block">
              <p class="tickets-intro">Категории билетов:</p>
            </div>
            
            <div class="tickets-grid">
              <div 
                class="ticket-card" 
                v-for="(ticket, index) in tickets" 
                :key="index"
                :class="{ 'ticket-vip': ticket.vip }"
                :style="{ animationDelay: (index * 0.1) + 's' }"
              >
                <div class="ticket-card-glow"></div>
                <div class="ticket-card-content">
                  <div class="ticket-price-wrapper">
                    <span class="ticket-price">{{ ticket.price }}</span>
                    <span class="ticket-currency">₽</span>
                  </div>
                  <div v-if="ticket.vip" class="ticket-vip-badge">
                    <i class="bi bi-star-fill"></i>
                    <span>VIP</span>
                  </div>
                </div>
                <div class="ticket-card-shine"></div>
              </div>
            </div>
            
            <div class="tickets-note-block">
              <i class="bi bi-info-circle-fill"></i>
              <p class="tickets-note-text">Чем ближе к сцене — тем дороже место</p>
            </div>
            
            <div class="tickets-cta-wrapper">
              <button class="btn tickets-cta-button" @click="openModal">
                <span class="tickets-cta-icon">👉</span>
                <span class="tickets-cta-text">Купить билет</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Давление на решение -->
    <section class="urgency-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="urgency-card">
          <div class="urgency-header">
            <div class="urgency-icon-wrapper">
              <i class="bi bi-clock-fill"></i>
            </div>
            <h2 class="urgency-title">Почему не стоит откладывать</h2>
          </div>
          <div class="urgency-content">
            <div class="urgency-intro-block">
              <p class="urgency-intro">Время ограничено!</p>
            </div>
            
            <div class="urgency-features-grid">
              <div 
                class="urgency-feature-item" 
                v-for="(feature, index) in urgencyFeatures" 
                :key="index"
                :style="{ animationDelay: (index * 0.1) + 's' }"
              >
                <div class="urgency-feature-icon">
                  <i :class="feature.icon"></i>
                </div>
                <div class="urgency-feature-content">
                  <span class="urgency-feature-text">{{ feature.text }}</span>
                </div>
              </div>
            </div>
            
            <div class="urgency-warning-block">
              <div class="urgency-warning-icon">
                <i class="bi bi-arrow-up-circle-fill"></i>
              </div>
              <p class="urgency-warning-text">Цена будет расти</p>
            </div>
            
            <div class="urgency-cta-wrapper">
              <button class="btn urgency-cta-button" @click="openModal">
                <span class="urgency-cta-icon">👉</span>
                <span class="urgency-cta-text">Купить билет</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Организатор -->
    <section class="organizer-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="organizer-card">
          <div class="organizer-header">
            <div class="organizer-icon-wrapper">
              <i class="bi bi-handshake-fill"></i>
            </div>
            <h2 class="organizer-title">Организатор</h2>
          </div>
          <div class="organizer-content">
            <div class="organizer-logo-wrapper">
              <img :src="organizerLogo" alt="Surnek Events" class="organizer-logo" />
            </div>
            
            <div class="organizer-description-block">
              <p class="organizer-description">
                Мы — молодая и амбициозная команда. Это наш первый проект, и мы делаем его как начало новой серии качественных ивент в Москве.
              </p>
            </div>
            
            <div class="organizer-highlight-block">
              <div class="organizer-highlight-icon">
                <i class="bi bi-heart-fill"></i>
              </div>
              <p class="organizer-highlight-text">Нам важно, чтобы вы захотели вернуться снова</p>
            </div>
            
            <div class="organizer-cta-wrapper">
              <button class="btn organizer-cta-button" @click="openModal">
                <span class="organizer-cta-icon">👉</span>
                <span class="organizer-cta-text">Купить билет</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Видео секция -->
    <section class="videos-section py-4 py-md-3 py-sm-2">
      <div class="container">
        <div class="videos-card">
          <div class="videos-header">
            <div class="videos-icon-wrapper">
              <i class="bi bi-play-circle-fill"></i>
            </div>
            <h2 class="videos-title">Видео</h2>
          </div>
          <div class="videos-content">
            <div class="videos-grid">
              <div 
                class="video-item" 
                v-for="(video, index) in videos" 
                :key="`video-${index}-${video.src}`"
                :style="{ animationDelay: (index * 0.1) + 's' }"
              >
                <CustomVideoPlayer
                  :video-src="video.src"
                  :width="video.width"
                  :height="video.height"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Top.Mail.Ru counter noscript -->
    <noscript>
      <div>
        <img src="https://top-fwz1.mail.ru/counter?id=3728352;js=na" style="position:absolute;left:-9999px;" alt="Top.Mail.Ru" />
      </div>
    </noscript>

    <!-- Финальный блок -->
    <section class="final-section py-5 py-md-4 py-sm-3">
      <div class="final-background-decoration"></div>
      <div class="container">
        <div class="final-content">
          <!-- Заголовок с акцентом на три слова -->
          <div class="final-title-wrapper">
            <div class="final-main-title">
              <div 
                class="final-word-item" 
                v-for="(item, index) in finalHighlightWords" 
                :key="index"
                :style="{ animationDelay: (index * 0.2) + 's' }"
                :class="{ 'final-word-music': item.text === 'Родная музыка' }"
                @mouseenter="item.text === 'Родная музыка' ? playMusicPreview() : null"
                @mouseleave="item.text === 'Родная музыка' ? stopMusicPreview() : null"
              >
                <span class="final-word-icon">
                  <i :class="item.icon"></i>
                </span>
                <h1 class="final-word-text">{{ item.text }}</h1>
                <span v-if="index < finalHighlightWords.length - 1" class="final-word-divider">•</span>
              </div>
            </div>
            <!-- Скрытый аудио элемент для музыки (опционально, если будет добавлен файл) -->
            <audio ref="musicAudio" preload="none" style="display: none;"></audio>
            <p class="final-subtitle">Незабываемый вечер ждёт вас</p>
          </div>

          <!-- Детали события в карточках -->
          <div class="final-details">
            <div 
              class="final-detail-card" 
              v-for="(detail, index) in finalDetails" 
              :key="index"
              :style="{ animationDelay: (index * 0.15) + 's' }"
            >
              <div class="final-detail-icon-wrapper">
                <i :class="detail.icon"></i>
              </div>
              <div class="final-detail-content">
                <span class="final-detail-label">{{ detail.label }}</span>
                <span class="final-detail-value">{{ detail.value }}</span>
              </div>
            </div>
          </div>

          <!-- Призыв к действию -->
          <div class="final-cta-wrapper">
            <button class="final-cta-button" @click="openModal">
              <span class="final-cta-icon">🎫</span>
              <span class="final-cta-text">Купить билет сейчас</span>
              <span class="final-cta-arrow">→</span>
            </button>
            <p class="final-cta-hint">Ограниченное количество мест</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Program Section -->
    <section class="py-5 py-md-4 py-sm-3 bg-body d-none">
      <div class="container">
        <div class="text-center mb-5 mb-md-4 mb-sm-3">
          <h2 class="display-5 fw-bold mb-3 mb-md-2 mb-sm-2 program-title">Программа концерта</h2>
          <p class="lead text-body-secondary program-subtitle">Расписание мероприятия для участников</p>
        </div>

        <div class="program-timeline">
          <!-- 18:00 - Фуршет -->
          <div class="timeline-item">
            <div class="timeline-marker">
              <div class="timeline-time">18:00</div>
              <div class="timeline-dot"></div>
            </div>
            <div class="timeline-content">
              <div class="program-card">
                <div class="program-icon">
                  <i class="bi bi-cup-hot fs-2"></i>
                </div>
                <h3 class="h4 fw-bold mb-2">Фуршет</h3>
                <p class="text-body-secondary mb-0">Встреча гостей, легкие закуски и напитки</p>
              </div>
            </div>
          </div>

          <!-- 19:00-22:00 - Концертная программа -->
          <div class="timeline-item">
            <div class="timeline-marker">
              <div class="timeline-time">19:00 - 22:00</div>
              <div class="timeline-dot timeline-dot-main"></div>
            </div>
            <div class="timeline-content">
              <div class="program-card program-card-main">
                <div class="program-icon program-icon-main">
                  <i class="bi bi-music-note-beamed fs-2"></i>
                </div>
                <h3 class="h4 fw-bold mb-3">Концертная программа</h3>
                <div class="row g-3">
                  <div class="col-md-6" v-for="item in concertProgram" :key="item.title">
                    <div class="program-item-flip">
                      <div class="program-item-flip-inner">
                        <!-- Front side -->
                        <div class="program-item-flip-front">
                          <div class="program-item-icon">
                            <i :class="['bi', item.icon]"></i>
                          </div>
                          <div class="program-item-content">
                            <h4 class="h6 fw-semibold mb-1">{{ item.title }}</h4>
                            <p class="small text-body-secondary mb-0">{{ item.description }}</p>
                          </div>
                        </div>
                        <!-- Back side -->
                        <div class="program-item-flip-back" v-if="item.details">
                          <div class="program-item-back-content">
                            <h4 class="h6 fw-semibold mb-2">{{ item.title }}</h4>
                            <ul class="program-details-list">
                              <li v-for="detail in item.details" :key="detail">{{ detail }}</li>
                            </ul>
                            <div v-if="item.groups" class="program-groups">
                              <p class="small fw-semibold mb-1 text-primary">Группы танцоров:</p>
                              <p class="small mb-0">{{ item.groups }}</p>
                            </div>
                          </div>
                        </div>
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

    <!-- Partners Section -->
    <section class="partners-section py-5 py-md-4 py-sm-2 bg-light">
      <div class="container">
        <div class="text-center mb-5 mb-md-4 mb-sm-3">
          <h2 class="display-5 fw-bold mb-3 mb-md-2 mb-sm-2">Наши партнёры</h2>
          <p class="lead text-body-secondary">Благодарим наших спонсоров за поддержку мероприятия</p>
        </div>

        <!-- General Sponsors -->
        <div v-if="generalSponsors.length > 0" class="mb-5 mb-md-4 mb-sm-3">
          <div class="text-center mb-4 mb-md-3 mb-sm-2">
            <h3 class="h4 fw-bold text-primary mb-2">
              <i class="bi bi-star-fill me-2"></i>
              Генеральный спонсор
            </h3>
            <p class="text-body-secondary small mb-0">Главные партнёры нашего концерта</p>
          </div>
          <!-- Desktop: Grid layout -->
          <div class="row g-4 g-md-3 g-sm-2 justify-content-center d-none d-md-flex">
            <div 
              v-for="(sponsor, index) in generalSponsors" 
              :key="`sponsor-${sponsor.name}-${index}`"
              class="col-lg-6 col-md-6 partner-sponsor-wrapper partner-card-animated"
            >
              <div class="partner-card partner-card-general" @click="openSponsorModal(sponsor)">
                <div class="partner-logo-wrapper">
                  <div class="partner-logo partner-logo-general">
                    <img 
                      v-if="sponsor.logo" 
                      :src="sponsor.logo" 
                      :alt="sponsor.name"
                      class="partner-logo-img"
                    />
                    <span v-else class="partner-logo-text">{{ sponsor.name }}</span>
                  </div>
                </div>
                <div class="partner-badge-general">
                  <i class="bi bi-award-fill"></i>
                  Генеральный спонсор
                </div>
              </div>
            </div>
          </div>
          
          <!-- Mobile: Horizontal scroll -->
          <div class="partners-scroll-container d-md-none">
            <div class="partners-scroll-wrapper partners-sponsors-scroll">
              <div 
                v-for="(sponsor, index) in generalSponsors" 
                :key="`sponsor-mobile-${sponsor.name}-${index}`"
                class="partner-sponsor-wrapper partner-sponsor-mobile partner-card-animated"
              >
                <div class="partner-card partner-card-general" @click="openSponsorModal(sponsor)">
                  <div class="partner-logo-wrapper">
                    <div class="partner-logo partner-logo-general">
                      <img 
                        v-if="sponsor.logo" 
                        :src="sponsor.logo" 
                        :alt="sponsor.name"
                        class="partner-logo-img"
                      />
                      <span v-else class="partner-logo-text">{{ sponsor.name }}</span>
                    </div>
                  </div>
                  <div class="partner-badge-general">
                    <i class="bi bi-award-fill"></i>
                    Генеральный спонсор
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Event Partners -->
        <div v-if="eventPartners.length > 0">
          <div class="text-center mb-4 mb-md-3 mb-sm-2">
            <h3 class="h5 fw-semibold text-body mb-2">
              <i class="bi bi-handshake me-2"></i>
              Партнёры события
            </h3>
            <p class="text-body-secondary small mb-0">Наши официальные партнёры</p>
          </div>
          <!-- Grid layout for all screens -->
          <div class="row g-3 g-md-4 justify-content-center">
            <div 
              v-for="(partner, index) in eventPartners" 
              :key="`partner-${partner.name}-${index}`"
              class="col-lg-3 col-md-4 col-6 col-sm-6 partner-card-animated"
            >
              <div 
                v-if="partner.isPlaceholder"
                class="partner-card partner-card-event partner-card-placeholder"
                @click="handlePlaceholderClick"
                style="cursor: pointer;"
              >
                <div class="partner-logo-wrapper">
                  <div class="partner-logo partner-logo-event">
                    <div class="partner-placeholder-content">
                      <i class="bi bi-plus-circle-fill partner-placeholder-icon"></i>
                      <span class="partner-logo-text partner-placeholder-text">{{ partner.placeholderText || partner.name }}</span>
                    </div>
                  </div>
                </div>
                <div class="partner-badge-event partner-badge-placeholder">
                  <i class="bi bi-info-circle-fill"></i>
                  {{ partner.description || 'Место свободно' }}
                </div>
              </div>
              <div 
                v-else
                class="partner-card partner-card-event" 
                @click="handleEventPartnerClick(partner)"
                style="cursor: pointer;"
              >
                <div class="partner-logo-wrapper">
                  <div class="partner-logo partner-logo-event">
                    <img 
                      v-if="partner.logo" 
                      :src="partner.logo" 
                      :alt="partner.name"
                      class="partner-logo-img"
                    />
                    <span v-else class="partner-logo-text">{{ partner.name }}</span>
                  </div>
                </div>
                <div class="partner-badge-event">
                  <i class="bi bi-check-circle-fill"></i>
                  Партнёр
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Contacts Section -->
    <section class="contacts-section py-5 py-md-4 py-sm-2 bg-body">
      <div class="container">
        <div class="text-center mb-4 mb-md-3 mb-sm-2">
          <h2 class="display-6 fw-bold mb-2 mb-md-2 mb-sm-1">Свяжитесь с нами</h2>
          <p class="text-body-secondary mb-0">Мы всегда готовы ответить на ваши вопросы</p>
        </div>

        <div class="row g-3 g-md-3 g-sm-2 justify-content-center">
          <!-- Booking Contacts -->
          <div class="col-lg-4 col-md-6">
            <div class="contact-card">
              <div class="contact-card-icon contact-icon-booking">
                <i class="bi bi-calendar-check"></i>
              </div>
              <h3 class="contact-card-title">Бронирование</h3>
              <p class="contact-card-description">Забронируйте место на концерт</p>
              
              <div class="contact-phone-wrapper">
                <div class="contact-icons">
                  <a 
                    href="https://t.me/surnek_events" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    class="contact-icon-link"
                    title="Telegram"
                  >
                    <i class="bi bi-telegram"></i>
                  </a>
                  <a 
                    href="https://wa.me/79362660668" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    class="contact-icon-link"
                    title="WhatsApp"
                  >
                    <i class="bi bi-whatsapp"></i>
                  </a>
                  <a 
                    href="tel:+79362660668" 
                    class="contact-icon-link"
                    title="Телефон"
                  >
                    <i class="bi bi-telephone"></i>
                  </a>
                </div>
                <a href="tel:+79362660668" class="contact-phone-number">+7 (936) 266-06-68</a>
              </div>
            </div>
          </div>

          <!-- Partners Contact -->
          <div class="col-lg-4 col-md-6">
            <div class="contact-card">
              <div class="contact-card-icon contact-icon-partners">
                <i class="bi bi-briefcase"></i>
              </div>
              <h3 class="contact-card-title">Для партнёров</h3>
              <p class="contact-card-description">Обратная связь для партнёрства</p>
              
              <div class="contact-phone-wrapper">
                <div class="contact-icons">
                  <a 
                    href="https://t.me/+79154680181" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    class="contact-icon-link"
                    title="Telegram"
                  >
                    <i class="bi bi-telegram"></i>
                  </a>
                  <a 
                    href="https://wa.me/79154680181" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    class="contact-icon-link"
                    title="WhatsApp"
                  >
                    <i class="bi bi-whatsapp"></i>
                  </a>
                  <a 
                    href="tel:+79154680181" 
                    class="contact-icon-link"
                    title="Телефон"
                  >
                    <i class="bi bi-telephone"></i>
                  </a>
                </div>
                <a href="tel:+79154680181" class="contact-phone-number">+7 (915) 468-01-81</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-5 py-md-4 py-sm-2 bg-primary text-white">
      <div class="container text-center">
        <h2 class="display-6 fw-bold mb-3">Готовы выбрать место?</h2>
        <p class="lead mb-4">Выберите лучший стол на интерактивной карте зала</p>
        <button class="btn btn-light btn-lg px-5" @click="openModal">
          <i class="bi bi-ticket-perforated me-2"></i>
          Выбрать место
        </button>
      </div>
    </section>

    <!-- Partners Carousel Section -->
    <section class="partners-carousel-section">
      <div class="partners-carousel-container">
        <div class="partners-carousel-track">
          <div class="partners-carousel-item">
            <img :src="chidSvgLogo" alt="CHID" class="partners-carousel-logo" />
          </div>
          <div class="partners-carousel-item">
            <img :src="asakiLogo" alt="Асаки" class="partners-carousel-logo partners-carousel-logo-asaki" />
          </div>
          <div class="partners-carousel-item">
            <img :src="drnematboevLogo" alt="Dr. Nematboev" class="partners-carousel-logo" />
          </div>
          <div class="partners-carousel-item">
            <img :src="smartxLogo" alt="SmartX" class="partners-carousel-logo" />
          </div>
          <div class="partners-carousel-item">
            <img :src="jxnLogo" alt="JXN" class="partners-carousel-logo" />
          </div>
          <!-- Duplicate for seamless loop -->
          <div class="partners-carousel-item">
            <img :src="chidSvgLogo" alt="CHID" class="partners-carousel-logo" />
          </div>
          <div class="partners-carousel-item">
            <img :src="asakiLogo" alt="Асаки" class="partners-carousel-logo partners-carousel-logo-asaki" />
          </div>
          <div class="partners-carousel-item">
            <img :src="drnematboevLogo" alt="Dr. Nematboev" class="partners-carousel-logo" />
          </div>
          <div class="partners-carousel-item">
            <img :src="smartxLogo" alt="SmartX" class="partners-carousel-logo" />
          </div>
          <div class="partners-carousel-item">
            <img :src="jxnLogo" alt="JXN" class="partners-carousel-logo" />
          </div>
        </div>
      </div>
    </section>

    <SeatMapModal :open="modalOpen" :concert-id="concertId" @close="closeModal" />

    <!-- Partner Request Modal -->
    <div v-if="showPartnerRequestModal" class="sponsor-modal-overlay" @click="closePartnerRequestModal">
      <div class="sponsor-modal" @click.stop>
        <button class="sponsor-modal-close" @click="closePartnerRequestModal">
          <i class="bi bi-x-lg"></i>
        </button>
        <div class="sponsor-modal-content">
          <div class="sponsor-modal-header">
            <h3 class="sponsor-modal-title">Стать партнёром</h3>
            <p class="text-body-secondary mb-0">Заполните форму, и мы свяжемся с вами</p>
          </div>
          
          <form @submit.prevent="submitPartnerRequest" class="mt-4">
            <div class="mb-3">
              <label class="form-label">ФИО *</label>
              <input 
                v-model="partnerRequestForm.fullName" 
                type="text" 
                class="form-control" 
                placeholder="Иванов Иван Иванович"
                required
              />
            </div>
            
            <div class="mb-3">
              <label class="form-label">Компания *</label>
              <input 
                v-model="partnerRequestForm.company" 
                type="text" 
                class="form-control" 
                placeholder="Название компании"
                required
              />
            </div>
            
            <div class="mb-3">
              <label class="form-label">Номер телефона *</label>
              <input 
                v-model="partnerRequestForm.phone" 
                type="tel" 
                class="form-control" 
                placeholder="+7 (999) 123-45-67"
                required
              />
            </div>
            
            <div class="mb-3">
              <label class="form-label">Email *</label>
              <input 
                v-model="partnerRequestForm.email" 
                type="email" 
                class="form-control" 
                placeholder="example@mail.com"
                required
              />
            </div>
            
            <div v-if="partnerRequestError" class="alert alert-danger mb-3">
              {{ partnerRequestError }}
            </div>
            
            <div v-if="partnerRequestSuccess" class="alert alert-success mb-3">
              Заявка успешно отправлена! Мы свяжемся с вами в ближайшее время.
            </div>
            
            <div class="d-flex gap-2">
              <button 
                type="submit" 
                class="btn btn-primary flex-grow-1"
                :disabled="partnerRequestLoading"
              >
                <span v-if="partnerRequestLoading" class="spinner-border spinner-border-sm me-2"></span>
                Отправить заявку
              </button>
              <button 
                type="button" 
                class="btn btn-outline-secondary"
                @click="closePartnerRequestModal"
                :disabled="partnerRequestLoading"
              >
                Отмена
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Sponsor Info Modal -->
    <div v-if="selectedSponsor" class="sponsor-modal-overlay" @click="closeSponsorModal">
      <div class="sponsor-modal" @click.stop>
        <button class="sponsor-modal-close" @click="closeSponsorModal">
          <i class="bi bi-x-lg"></i>
        </button>
        <div class="sponsor-modal-content">
          <div class="sponsor-modal-header">
            <div v-if="selectedSponsor.logo" class="sponsor-modal-logo">
              <img :src="selectedSponsor.logo" :alt="selectedSponsor.name" />
            </div>
            <h3 class="sponsor-modal-title">{{ selectedSponsor.name }}</h3>
            <div class="sponsor-modal-badge">
              <i class="bi bi-award-fill"></i>
              Генеральный спонсор
            </div>
          </div>
          
          <div v-if="selectedSponsor.description" class="sponsor-modal-description">
            <p style="white-space: pre-line;">{{ selectedSponsor.description }}</p>
          </div>
          
          <div v-if="selectedSponsor.services && selectedSponsor.services.length > 0" class="sponsor-modal-services">
            <h4 class="sponsor-modal-services-title">Наши услуги:</h4>
            <ul class="sponsor-modal-services-list">
              <li v-for="(service, i) in selectedSponsor.services" :key="i">
                <i class="bi bi-check-circle"></i>
                {{ service }}
              </li>
            </ul>
          </div>
          
          <div v-if="selectedSponsor.telegram" class="sponsor-modal-telegram">
            <a 
              :href="selectedSponsor.telegram" 
              target="_blank" 
              rel="noopener noreferrer"
              class="sponsor-telegram-link"
            >
              <i class="bi bi-telegram"></i>
              {{ selectedSponsor.telegramChannel || 'Наш Telegram канал' }}
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Нижнее меню навигации - вне landing-page для правильного позиционирования -->
  <nav v-if="showNavigationMenu && navigationMenu && navigationMenu.length > 0" class="bottom-nav" ref="bottomNavRef" aria-hidden="false">
    <a
      v-for="(item, index) in navigationMenu"
      :key="`nav-${index}-${item.id}`"
      href="#"
      class="nav-item"
      :class="{ 'active': activeSection === item.id }"
      :data-target="`#${item.id}`"
      @click.prevent="scrollToSection(item.id)"
    >
      <i :class="item.icon"></i>
      <span>{{ item.label }}</span>
    </a>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import ConcertHero from '../components/ConcertHero.vue';
import SeatMapModal from '../components/SeatMapModal.vue';
import CustomVideoPlayer from '@/components/CustomVideoPlayer.vue';
import { useConcertStore } from '../stores/concertStore';
import { storeToRefs } from 'pinia';
import { submitPartnerRequest as apiSubmitPartnerRequest } from '../services/api';
import chidLogo from '../assets/chid.svg';
import asakiLogo from '../assets/asaki.svg';
import drnematboevLogo from '../assets/drnematboev.svg';
import smartxLogo from '../assets/smartx.svg';
import jxnLogo from '../assets/jxnlogo.svg';
import chidSvgLogo from '../assets/chid.svg';
import safarImage from '@/assets/safarr.png';
import rizoImage from '../assets/rizo.png';
import furqatImage from '../assets/furqat.png';
import organizerLogo from '../assets/log.svg';

const concertId = 1;
const modalOpen = ref(false);
const selectedSponsor = ref<any>(null);
const showPartnerRequestModal = ref(false);
const showNavigationMenu = ref(true);
const partnerRequestForm = ref({
  fullName: '',
  company: '',
  phone: '',
  email: ''
});
const partnerRequestLoading = ref(false);
const partnerRequestError = ref('');
const partnerRequestSuccess = ref(false);
const bottomNavRef = ref<HTMLElement | null>(null);
const activeSection = ref<string>('');

// Меню навигации для секций
const navigationMenu = ref([
  { id: 'hero', label: 'Главное', icon: 'bi bi-house-fill' },
  { id: 'artist', label: 'Атисты', icon: 'bi bi-music-player-fill' },
  { id: 'tickets', label: 'Билеты', icon: 'bi bi-ticket-perforated-fill' },
  { id: 'partners', label: 'Партнеры', icon: 'bi bi-people-fill' },
  { id: 'contacts', label: 'Контакты', icon: 'bi bi-telephone-fill' }
]);

// Функция прокрутки к секции
const scrollToSection = (sectionId: string) => {
  let element: Element | null = null;
  
  // Для hero секции используем специальный селектор
  if (sectionId === 'hero') {
    element = document.querySelector('.hero-section');
  } else {
    element = document.querySelector(`.${sectionId}-section`);
  }
  
  if (element) {
    // Устанавливаем активную секцию сразу при клике
    activeSection.value = sectionId;
    
    const offset = 80; // Отступ от верха
    const elementPosition = element.getBoundingClientRect().top;
    const offsetPosition = elementPosition + window.pageYOffset - offset;

    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    });
    
    // Обновляем активную секцию после завершения прокрутки
    setTimeout(() => {
      handleScroll();
    }, 500);
  } else {
    console.warn(`Секция ${sectionId} не найдена`);
  }
};

// Отслеживание активной секции при прокрутке
const handleScroll = () => {
  const sections = navigationMenu.value.map(item => {
    let element: Element | null = null;
    // Для hero секции используем специальный селектор
    if (item.id === 'hero') {
      element = document.querySelector('.hero-section');
    } else {
      element = document.querySelector(`.${item.id}-section`);
    }
    return {
      id: item.id,
      element: element
    };
  }).filter(section => section.element !== null); // Фильтруем только существующие секции

  if (sections.length === 0) return;

  const viewportHeight = window.innerHeight;
  const scrollPosition = window.scrollY;
  const threshold = 150; // Порог от верха экрана для определения активной секции

  let activeId = sections[0].id; // По умолчанию первая секция
  let maxVisibleArea = 0;

  // Находим секцию, которая видна больше всего на экране
  for (const section of sections) {
    if (section.element) {
      const rect = section.element.getBoundingClientRect();
      const elementTop = scrollPosition + rect.top;
      const elementBottom = scrollPosition + rect.bottom;
      
      // Проверяем, видна ли секция на экране
      const visibleTop = Math.max(0, rect.top);
      const visibleBottom = Math.min(viewportHeight, rect.bottom);
      const visibleHeight = Math.max(0, visibleBottom - visibleTop);
      
      // Если секция пересекает порог (верх экрана + threshold) или находится выше него
      if (elementTop <= scrollPosition + threshold && visibleHeight > 0) {
        // Выбираем секцию с наибольшей видимой областью
        if (visibleHeight > maxVisibleArea) {
          maxVisibleArea = visibleHeight;
          activeId = section.id;
        }
      }
    }
  }

  // Если ни одна секция не пересекает порог, выбираем ту, которая ближе всего к верху
  if (maxVisibleArea === 0) {
    let minDistance = Infinity;
    for (const section of sections) {
      if (section.element) {
        const rect = section.element.getBoundingClientRect();
        const elementTop = scrollPosition + rect.top;
        const distance = Math.abs(elementTop - scrollPosition - threshold);
        
        if (distance < minDistance) {
          minDistance = distance;
          activeId = section.id;
        }
      }
    }
  }

  activeSection.value = activeId;
};

const concertStore = useConcertStore();
const { concert } = storeToRefs(concertStore);

const musicAudio = ref<HTMLAudioElement | null>(null);
const musicTimeout = ref<NodeJS.Timeout | null>(null);
const audioContext = ref<AudioContext | null>(null);
const isPlayingMusic = ref(false);
const currentAudioSource = ref<AudioBufferSourceNode | null>(null);

onMounted(() => {
  concertStore.load(concertId);
  // Инициализируем AudioContext для генерации звука
  try {
    audioContext.value = new (window.AudioContext || (window as any).webkitAudioContext)();
  } catch (e) {
    console.log('Web Audio API not supported');
  }
  
  // Добавляем обработчик прокрутки для отслеживания активной секции
  window.addEventListener('scroll', handleScroll);
  handleScroll(); // Вызываем сразу для установки начальной активной секции
  
  // Top.Mail.Ru counter
  const _tmr = (window as any)._tmr || ((window as any)._tmr = []);
  _tmr.push({id: "3728352", type: "pageView", start: (new Date()).getTime(), pid: "USER_ID"});
  (function (d: Document, w: Window, id: string) {
    if (d.getElementById(id)) return;
    const ts = d.createElement("script");
    ts.type = "text/javascript";
    ts.async = true;
    ts.id = id;
    ts.src = "https://top-fwz1.mail.ru/js/code.js";
    const f = function () {
      const s = d.getElementsByTagName("script")[0];
      if (s && s.parentNode) {
        s.parentNode.insertBefore(ts, s);
      }
    };
    if ((w as any).opera == "[object Opera]") {
      d.addEventListener("DOMContentLoaded", f, false);
    } else {
      f();
    }
  })(document, window, "tmr-code");
});

onUnmounted(() => {
  if (musicTimeout.value) {
    clearTimeout(musicTimeout.value);
  }
  if (audioContext.value) {
    audioContext.value.close();
  }
  // Удаляем обработчик прокрутки
  window.removeEventListener('scroll', handleScroll);
  // Удаляем обработчик события открытия модального окна
  const ticketModalHandler = (window as any).__handleOpenTicketModal;
  if (ticketModalHandler) {
    window.removeEventListener('openTicketModal', ticketModalHandler);
    delete (window as any).__handleOpenTicketModal;
  }
  // Удаляем обработчики событий для меню
  const hideMenuHandler = (window as any).__handleHideNavigationMenu;
  if (hideMenuHandler) {
    window.removeEventListener('hideNavigationMenu', hideMenuHandler);
    delete (window as any).__handleHideNavigationMenu;
  }
  const showMenuHandler = (window as any).__handleShowNavigationMenu;
  if (showMenuHandler) {
    window.removeEventListener('showNavigationMenu', showMenuHandler);
    delete (window as any).__handleShowNavigationMenu;
  }
});

const playMusicPreview = () => {
  if (isPlayingMusic.value) return;
  
  isPlayingMusic.value = true;
  
  // Используем Web Audio API для генерации музыки
  // Если в будущем нужно будет использовать аудио файл, можно добавить его в /public/music-preview.mp3
  // и раскомментировать код ниже
  /*
  if (musicAudio.value && musicAudio.value.src) {
    musicAudio.value.currentTime = 0;
    musicAudio.value.volume = 0.6;
    musicAudio.value.play().catch(() => {
      playGeneratedMusic();
    });
    
    musicTimeout.value = setTimeout(() => {
      stopMusicPreview();
    }, 5000);
  } else {
    playGeneratedMusic();
  }
  */
  playGeneratedMusic();
};

const stopMusicPreview = () => {
  if (musicTimeout.value) {
    clearTimeout(musicTimeout.value);
    musicTimeout.value = null;
  }
  
  if (musicAudio.value) {
    musicAudio.value.pause();
    musicAudio.value.currentTime = 0;
  }
  
  if (currentAudioSource.value) {
    try {
      currentAudioSource.value.stop();
    } catch (e) {
      // Источник уже остановлен
    }
    currentAudioSource.value = null;
  }
  
  if (audioContext.value && audioContext.value.state !== 'closed') {
    // Останавливаем все источники звука
    audioContext.value.suspend();
  }
  
  isPlayingMusic.value = false;
};

const playGeneratedMusic = () => {
  if (!audioContext.value || audioContext.value.state === 'closed') {
    try {
      audioContext.value = new (window.AudioContext || (window as any).webkitAudioContext)();
    } catch (e) {
      return;
    }
  }
  
  if (audioContext.value.state === 'suspended') {
    audioContext.value.resume();
  }
  
  // Создаем приятный музыкальный аккорд (мажорное трезвучие)
  const frequencies = [261.63, 329.63, 392.00]; // C, E, G (до, ми, соль)
  const duration = 5; // 5 секунд
  const sampleRate = audioContext.value.sampleRate;
  const buffer = audioContext.value.createBuffer(2, sampleRate * duration, sampleRate);
  
  frequencies.forEach((freq, index) => {
    for (let channel = 0; channel < buffer.numberOfChannels; channel++) {
      const channelData = buffer.getChannelData(channel);
      const volume = 0.12 / frequencies.length; // Уменьшаем громкость
      const delay = index * 0.05; // Небольшая задержка для каждого тона
      
      for (let i = 0; i < channelData.length; i++) {
        const time = (i + delay * sampleRate) / sampleRate;
        if (time < duration) {
          // Добавляем envelope для плавного затухания
          const envelope = Math.exp(-time * 0.3) * (1 - time / duration * 0.5);
          // Добавляем гармоники для более богатого звука
          const fundamental = Math.sin(2 * Math.PI * freq * time);
          const harmonic = Math.sin(2 * Math.PI * freq * 2 * time) * 0.3;
          channelData[i] += (fundamental + harmonic) * volume * envelope;
        }
      }
    }
  });
  
  const source = audioContext.value.createBufferSource();
  const gainNode = audioContext.value.createGain();
  
  source.buffer = buffer;
  gainNode.gain.setValueAtTime(0.5, audioContext.value.currentTime);
  
  source.connect(gainNode);
  gainNode.connect(audioContext.value.destination);
  
  source.start(0);
  
  // Сохраняем источник для возможности остановки
  currentAudioSource.value = source;
  
  // Останавливаем через 5 секунд
  musicTimeout.value = setTimeout(() => {
    stopMusicPreview();
  }, duration * 1000);
};

const openModal = () => {
  modalOpen.value = true;
  showNavigationMenu.value = false;
};

const closeModal = () => {
  modalOpen.value = false;
  showNavigationMenu.value = true;
};

// Обработчики событий для управления меню из SplashScreen
// Добавляем их сразу после определения функций, чтобы они были готовы до onMounted
const handleOpenTicketModal = () => {
  openModal();
};

const handleHideNavigationMenu = () => {
  showNavigationMenu.value = false;
};

const handleShowNavigationMenu = () => {
  showNavigationMenu.value = true;
};

// Регистрируем обработчики событий
window.addEventListener('openTicketModal', handleOpenTicketModal);
window.addEventListener('hideNavigationMenu', handleHideNavigationMenu);
window.addEventListener('showNavigationMenu', handleShowNavigationMenu);

// Сохраняем обработчики для удаления в onUnmounted
(window as any).__handleOpenTicketModal = handleOpenTicketModal;
(window as any).__handleHideNavigationMenu = handleHideNavigationMenu;
(window as any).__handleShowNavigationMenu = handleShowNavigationMenu;

const formattedDate = computed(() => {
  if (!concert.value) return '';

  const date = new Date(concert.value.concertDate as unknown as string);

  if (Number.isNaN(date.getTime())) {
    // Если по какой-то причине дата пришла в неожиданном формате,
    // не падаем, а просто скрываем форматированную дату
    return '';
  }

  return new Intl.DateTimeFormat('ru-RU', {
    dateStyle: 'full',
    timeStyle: 'short'
  }).format(date);
});

const whyFeatures = [
  {
    icon: 'bi bi-music-note-beamed',
    text: 'живой концерт Сафармухаммада'
  },
  {
    icon: 'bi bi-people-fill',
    text: 'живая группа'
  },
  {
    icon: 'bi bi-cup-hot',
    text: 'банкет с мясом и закусками'
  },
  {
    icon: 'bi bi-cup-straw',
    text: 'напитки без доплат'
  },
  {
    icon: 'bi bi-mic-fill',
    text: 'ведущий, DJ, шоу'
  },
  {
    icon: 'bi bi-gift-fill',
    text: 'розыгрыши и конкурсы'
  }
];

const whoFeatures = [
  {
    icon: 'bi bi-emoji-smile',
    text: 'отдохнуть без суеты'
  },
  {
    icon: 'bi bi-music-note-list',
    text: 'услышать родную музыку вживую'
  },
  {
    icon: 'bi bi-heart-fill',
    text: 'провести вечер парой или компанией'
  },
  {
    icon: 'bi bi-people',
    text: 'почувствовать атмосферу «своих»'
  },
  {
    icon: 'bi bi-star-fill',
    text: 'начать год красиво'
  }
];

const notBoringEvents = [
  {
    icon: 'bi bi-music-note-beamed',
    text: 'живая музыка с 19:00'
  },
  {
    icon: 'bi bi-collection-play',
    text: 'более 20 песен'
  },
  {
    icon: 'bi bi-star-fill',
    text: 'хиты и новые треки'
  },
  {
    icon: 'bi bi-person-badge',
    text: 'ведущий и DJ'
  },
  {
    icon: 'bi bi-heart',
    text: 'танцы'
  },
  {
    icon: 'bi bi-trophy',
    text: 'конкурсы'
  },
  {
    icon: 'bi bi-gift',
    text: 'розыгрыши'
  },
  {
    icon: 'bi bi-camera',
    text: 'фотозона'
  }
];

const artists = [
  {
    name: 'Сафармухаммад',
    role: 'Главный артист',
    image: safarImage,
    features: [
      '100% живой звук',
      'полный состав группы',
      'эксклюзивные номера',
      'контакт с залом'
    ]
  },
  {
    name: 'Ризо',
    role: 'Гость вечера',
    image: rizoImage,
    features: [
      'Специальный гость',
      'Эксклюзивное выступление'
    ]
  },
  {
    name: 'Фуркат',
    role: 'Гость вечера',
    image: furqatImage,
    features: [
      'Специальный гость',
      'Эксклюзивное выступление'
    ]
  }
];

// Компонент поддерживает как прямые ссылки на видеофайлы, так и VK iframe ссылки
// Для прямых ссылок: используйте .mp4, .webm и т.д.
// Для VK видео: используйте ссылку на video_ext.php (компонент автоматически определит тип)
const videos = [
  {
    src: 'https://vk.com/video_ext.php?oid=137564879&id=456239896',
    width: '1080',
    height: '1920'
  },
  {
    src: 'https://vk.com/video_ext.php?oid=137564879&id=456239898',
    width: '1080',
    height: '1920'
  },
  {
    src: 'https://vk.com/video_ext.php?oid=137564879&id=456239899&autoplay=1',
    width: '325',
    height: '646'
  }
];

const foodCategories = [
  {
    icon: 'bi bi-fire',
    title: 'Мясные блюда',
    items: ['Мясное ассорти', 'Шашлык']
  },
  {
    icon: 'bi bi-flower1',
    title: 'Салаты',
    items: ['Цезарь', 'Салат из баклажанов', 'Овощная нарезка']
  },
  {
    icon: 'bi bi-egg-fried',
    title: 'Закуски',
    items: ['Европейские сыры']
  },
  {
    icon: 'bi bi-cup-straw',
    title: 'Напитки',
    items: ['Кола, минералка, тархун, дюшес, соки']
  }
];

const tickets = [
  { price: '5 000', vip: false },
  { price: '6 000', vip: false },
  { price: '7 000', vip: false },
  { price: '10 000', vip: true }
];

const urgencyFeatures = [
  {
    icon: 'bi bi-people-fill',
    text: 'всего 500 мест'
  },
  {
    icon: 'bi bi-check-circle-fill',
    text: 'часть уже продана'
  },
  {
    icon: 'bi bi-calendar-check-fill',
    text: 'новогодняя акция до 29 декабря'
  },
  {
    icon: 'bi bi-hourglass-split',
    text: 'билеты по 5 000 ₽ быстро заканчиваются'
  }
];

const finalHighlightWords = [
  {
    icon: 'bi bi-heart-fill',
    text: 'Комфорт'
  },
  {
    icon: 'bi bi-stars',
    text: 'Атмосфера'
  },
  {
    icon: 'bi bi-music-note-beamed',
    text: 'Родная музыка'
  }
];

const finalDetails = [
  {
    icon: 'bi bi-calendar3-fill',
    label: 'Дата',
    value: '3 января 2026'
  },
  {
    icon: 'bi bi-clock-fill',
    label: 'Время',
    value: '19:00'
  },
  {
    icon: 'bi bi-geo-alt-fill',
    label: 'Место',
    value: 'Банкетный зал Асаки'
  },
  {
    icon: 'bi bi-pin-map-fill',
    label: 'Адрес',
    value: 'ул. Юных Ленинцев, 12'
  }
];

const concertProgram = [
  {
    title: 'Танцы',
    description: 'Зажигательные танцевальные номера',
    icon: 'bi-music-player',
    details: ['Памирский танцы', 'Сагдинский', 'Хатлонский'],
    groups: 'Выступления профессиональных танцевальных коллективов'
  },
  {
    title: 'Сытный банкетный стол',
    description: 'Богатое угощение для всех гостей',
    icon: 'bi-egg-fried',
    details: ['Традиционные блюда', 'Национальная кухня', 'Десерты и напитки']
  },
  {
    title: 'ШОУ-ПРОГРАММА',
    description: 'Яркие выступления и развлечения',
    icon: 'bi-stars',
    details: ['Музыкальные номера', 'Вокальные выступления', 'Интерактивные шоу']
  },
  {
    title: 'Розыгрыши призов и конкурсы',
    description: 'Интерактивные игры с ценными подарками',
    icon: 'bi-gift',
    details: ['Розыгрыш призов', 'Интерактивные конкурсы', 'Сюрпризы для гостей']
  }
];

// Partners data - original arrays
const originalGeneralSponsors = [
  { 
    name: 'CHID Агентство недвижимости',
    logo: chidLogo,
    description: 'Надёжные сделки и лучшие предложения — с CHID.\n\nБолее 200 семей уже приобрели свои квартиры с нами.',
    telegram: 'https://t.me/chidestate',
    telegramChannel: '@chidestate',
    services: [
      'Продажа и покупка недвижимости',
      'Аренда жилой и коммерческой недвижимости',
      'Юридическое сопровождение сделок',
      'Оценка недвижимости',
      'Консультации по инвестициям'
    ]
  },
  { 
    name: 'Ресторан Асаки',
    logo: asakiLogo,
    description: 'Ресторан Асаки расположен в Москве по адресу: ул. Юных Ленинцев, 12.\n\nМы проводим банкеты, корпоративные мероприятия, свадьбы и другие торжественные события. У нас уютная атмосфера, изысканная кухня и профессиональное обслуживание.',
    telegram: '',
    telegramChannel: '',
    services: [
      'Банкеты и торжественные мероприятия',
      'Корпоративные события',
      'Свадебные церемонии',
      'Проведение праздников',
      'Кейтеринг и выездное обслуживание'
    ]
  }
];

const originalEventPartners = [
  { 
    name: 'Партнёр 1',
    logo: drnematboevLogo,
    socialMediaUrl: 'https://www.instagram.com/dr.nematboev?igsh=MWJ5MWs4MjQ1eWJrNg=='
  },
  { 
    name: 'Партнёр 2',
    logo: smartxLogo,
    telegramUrl: 'https://t.me/SmartXB71'
  },
  { 
    name: 'JXN',
    logo: jxnLogo
  },
  { 
    name: 'Ваше место здесь',
    isPlaceholder: true,
    placeholderText: 'Стать партнёром',
    description: 'Место для размещения рекламы свободно'
  }
];

// Reactive arrays for display
const generalSponsors = ref([...originalGeneralSponsors]);
const eventPartners = ref([...originalEventPartners]);

// Function to shuffle array
const shuffleArray = <T,>(array: T[]): T[] => {
  const shuffled = [...array];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  return shuffled;
};

// Function to rotate partners
const rotatePartners = () => {
  // Rotate general sponsors (swap positions)
  if (generalSponsors.value.length >= 2) {
    generalSponsors.value = [generalSponsors.value[1], generalSponsors.value[0]];
  }
  
  // Shuffle event partners
  if (eventPartners.value.length > 1) {
    eventPartners.value = shuffleArray(eventPartners.value);
  }

};

// Start rotation interval
let rotationInterval: ReturnType<typeof setInterval> | null = null;

const startRotation = () => {
  if (rotationInterval) {
    clearInterval(rotationInterval);
  }
  rotationInterval = setInterval(rotatePartners, 15000);
};

const stopRotation = () => {
  if (rotationInterval) {
    clearInterval(rotationInterval);
    rotationInterval = null;
  }
};

const openSponsorModal = (sponsor: any) => {
  selectedSponsor.value = sponsor;
  stopRotation();
};

const closeSponsorModal = () => {
  selectedSponsor.value = null;
  startRotation();
};

const handleEventPartnerClick = (partner: any) => {
  // Для партнеров события - переходим на их сайт или соцсеть
  const url = partner.websiteUrl || partner.socialMediaUrl || partner.telegramUrl;
  if (url) {
    window.open(url, '_blank', 'noopener,noreferrer');
  }
};

const handlePlaceholderClick = () => {
  showPartnerRequestModal.value = true;
  stopRotation();
};

const closePartnerRequestModal = () => {
  showPartnerRequestModal.value = false;
  partnerRequestForm.value = {
    fullName: '',
    company: '',
    phone: '',
    email: ''
  };
  partnerRequestError.value = '';
  partnerRequestSuccess.value = false;
  startRotation();
};

const submitPartnerRequest = async () => {
  partnerRequestLoading.value = true;
  partnerRequestError.value = '';
  partnerRequestSuccess.value = false;
  
  try {
    await apiSubmitPartnerRequest(partnerRequestForm.value);
    partnerRequestSuccess.value = true;
    // Закрываем модальное окно через 2 секунды после успешной отправки
    setTimeout(() => {
      closePartnerRequestModal();
    }, 2000);
  } catch (error: any) {
    console.error('Error submitting partner request:', error);
    partnerRequestError.value = error.response?.data?.error || 'Не удалось отправить заявку. Попробуйте позже.';
  } finally {
    partnerRequestLoading.value = false;
  }
};

onMounted(() => {
  concertStore.load(concertId);
  
  // Start rotating partners every 15 seconds
  startRotation();
});

// Cleanup interval on unmount
onUnmounted(() => {
  stopRotation();
});
</script>

<style scoped>
.landing-page {
  padding-bottom: 120px; /* Отступ для нижнего меню и футера */
  min-height: 100vh;
}

/* Program Timeline Styles */
.program-timeline {
  position: relative;
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 0;
}

.program-timeline::before {
  content: '';
  position: absolute;
  left: 120px;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, #18723F 0%, #6f42c1 100%);
  border-radius: 2px;
}

.timeline-item {
  position: relative;
  display: flex;
  margin-bottom: 3rem;
  align-items: flex-start;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-marker {
  position: relative;
  width: 120px;
  flex-shrink: 0;
  padding-right: 2rem;
  text-align: right;
}

.timeline-time {
  font-size: 1.1rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 0.5rem;
  white-space: nowrap;
}

.timeline-dot {
  position: absolute;
  right: -1.5rem;
  top: -1.75rem;
  width: 20px;
  height: 20px;
  background: #18723F;
  border: 4px solid #fff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(24, 114, 63, 0.3);
  z-index: 2;
}

.timeline-dot-main {
  width: 28px;
  height: 28px;
  right: -1.7rem;
  top: -1.85rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-width: 5px;
  box-shadow: 0 4px 12px rgba(111, 66, 193, 0.4);
}

.timeline-content {
  flex: 1;
  padding-left: 1rem;
}

.program-card {
  background: #fff;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(24, 114, 63, 0.1);
}

.program-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.program-card-main {
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 2px solid rgba(24, 114, 63, 0.2);
}

.program-icon {
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1));
  border-radius: 16px;
  color: #18723F;
  margin-bottom: 1rem;
}

.program-icon-main {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #18723F, #6f42c1);
  color: #fff;
  margin-bottom: 1.5rem;
}

/* Flip Card Styles */
.program-item-flip {
  perspective: 1000px;
  height: 100%;
  min-height: 120px;
}

.program-item-flip-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.program-item-flip:hover .program-item-flip-inner {
  transform: rotateY(180deg);
}

.program-item-flip-front,
.program-item-flip-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem;
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.program-item-flip-back {
  transform: rotateY(180deg);
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 2px solid rgba(24, 114, 63, 0.2);
  align-items: flex-start;
  justify-content: flex-start;
  padding: 0.75rem;
  overflow-y: auto;
  overflow-x: hidden;
  max-height: 100%;
  scrollbar-width: thin;
  scrollbar-color: rgba(24, 114, 63, 0.3) transparent;
}

.program-item-flip-back::-webkit-scrollbar {
  width: 6px;
}

.program-item-flip-back::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
}

.program-item-flip-back::-webkit-scrollbar-thumb {
  background: rgba(24, 114, 63, 0.3);
  border-radius: 10px;
}

.program-item-flip-back::-webkit-scrollbar-thumb:hover {
  background: rgba(24, 114, 63, 0.5);
}

.program-item-back-content {
  width: 100%;
  overflow: visible;
}

.program-item-back-content h4 {
  font-size: 0.95rem;
  margin-bottom: 0.5rem !important;
}

.program-details-list {
  list-style: none;
  padding: 0;
  margin: 0;
  margin-bottom: 0.5rem;
}

.program-details-list li {
  padding: 0.25rem 0;
  padding-left: 1rem;
  position: relative;
  color: #495057;
  font-size: 0.8rem;
  line-height: 1.3;
}

.program-details-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #18723F;
  font-weight: bold;
  font-size: 1rem;
  line-height: 1.2;
}

.program-groups {
  padding-top: 0.5rem;
  margin-top: 0.5rem;
  border-top: 1px solid rgba(24, 114, 63, 0.1);
}

.program-groups p {
  font-size: 0.75rem;
  margin-bottom: 0.25rem !important;
}

.program-groups .small {
  font-size: 0.7rem !important;
}

.program-item-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1));
  border-radius: 10px;
  color: #18723F;
  flex-shrink: 0;
  font-size: 1.2rem;
}

.program-item-content {
  flex: 1;
}

/* Responsive Design */
@media (max-width: 768px) {
  .program-timeline {
    padding: 1rem 0;
  }

  .program-timeline::before {
    left: 60px;
    width: 2px;
  }

  .timeline-item {
    margin-bottom: 2rem;
  }

  .timeline-marker {
    width: 60px;
    padding-right: 0.75rem;
  }

  .timeline-time {
    font-size: 0.85rem;
    margin-bottom: 0.25rem;
  }

  .timeline-dot {
    right: -0.75rem;
    width: 14px;
    height: 14px;
    border-width: 3px;
    top: -1.15rem;
  }

  .timeline-dot-main {
    right: -0.8rem;
    width: 18px;
    height: 18px;
    border-width: 3px;
    top: -1.2rem;
  }

  .timeline-content {
    padding-left: 0.25rem;
  }

  .program-card {
    padding: 1rem;
    border-radius: 12px;
  }

  .program-icon {
    width: 50px;
    height: 50px;
    margin-bottom: 0.75rem;
    border-radius: 12px;
  }

  .program-icon i {
    font-size: 1.25rem !important;
  }

  .program-icon-main {
    width: 60px;
    height: 60px;
    margin-bottom: 1rem;
  }

  .program-card h3 {
    font-size: 1.1rem;
    margin-bottom: 0.75rem !important;
  }

  .program-card p {
    font-size: 0.875rem;
  }

  .program-item-flip {
    min-height: 100px;
  }

  .program-item-flip-front,
  .program-item-flip-back {
    flex-direction: row;
    text-align: left;
    padding: 0.75rem;
    gap: 0.75rem;
    border-radius: 10px;
  }

  .program-item-flip-back {
    padding: 0.6rem;
    overflow-y: auto;
    overflow-x: hidden;
  }

  .program-item-back-content h4 {
    font-size: 0.85rem !important;
    margin-bottom: 0.4rem !important;
  }

  .program-item-icon {
    width: 32px;
    height: 32px;
    font-size: 1rem;
    border-radius: 8px;
    margin: 0;
  }

  .program-details-list {
    margin-bottom: 0.4rem;
  }

  .program-details-list li {
    font-size: 0.7rem;
    padding: 0.2rem 0;
    padding-left: 0.9rem;
    line-height: 1.2;
  }

  .program-details-list li::before {
    font-size: 0.9rem;
  }

  .program-groups {
    padding-top: 0.4rem;
    margin-top: 0.4rem;
  }

  .program-groups p {
    font-size: 0.7rem !important;
    margin-bottom: 0.2rem !important;
  }

  .program-groups .small {
    font-size: 0.65rem !important;
  }

  .program-item-content h4 {
    font-size: 0.9rem;
    margin-bottom: 0.25rem !important;
  }

  .program-item-content p {
    font-size: 0.8rem;
  }

  .program-title {
    font-size: 1.75rem !important;
  }

  .program-subtitle {
    font-size: 0.95rem !important;
  }

  .row.g-3 {
    --bs-gutter-y: 0.75rem;
    --bs-gutter-x: 0.75rem;
  }
}

/* Partners Section Styles */
.partner-card {
  position: relative;
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border: 2px solid transparent;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  overflow: hidden;
}

.partner-card-general {
  overflow: visible;
}

.partner-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* General Sponsor Card */
.partner-card-general {
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 3px solid rgba(24, 114, 63, 0.3);
  padding: 3rem 2.5rem;
  min-height: 280px;
  position: relative;
  cursor: pointer;
}

.partner-card-general::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 100%);
}

.partner-card-general:hover {
  border-color: rgba(24, 114, 63, 0.5);
  box-shadow: 0 16px 50px rgba(24, 114, 63, 0.2);
}

/* Event Partner Card */
.partner-card-event {
  border: 2px solid rgba(24, 114, 63, 0.15);
  min-height: 180px;
  padding: 2rem 1.5rem;
}

.partner-card-event:hover {
  border-color: rgba(24, 114, 63, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
}

.partner-logo-wrapper {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
  position: relative;
}

.partner-logo {
  width: 100%;
  max-width: 100%;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.partner-logo::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.partner-card:hover .partner-logo::before {
  left: 100%;
}

.partner-logo-general {
  min-height: 180px;
  padding: 2.5rem;
  background: linear-gradient(135deg, #fff 0%, rgba(24, 114, 63, 0.02) 100%);
  border: 2px solid rgba(24, 114, 63, 0.1);
}

.partner-logo-event {
  min-height: 120px;
  padding: 1.5rem;
}

.partner-logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: #18723F;
  text-align: center;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  position: relative;
  z-index: 1;
}

.partner-logo-general .partner-logo-text {
  font-size: 2rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.partner-logo-img {
  max-width: 100%;
  max-height: 140px;
  width: auto;
  height: auto;
  object-fit: contain;
  display: block;
  transition: transform 0.3s ease;
}

.partner-logo-event .partner-logo-img {
  filter: brightness(0);
}

.partner-logo-wrapper:hover .partner-logo-img {
  transform: scale(1.05);
}

/* Partner Sponsor Wrapper */
.partner-sponsor-wrapper {
  position: relative;
}

/* Partner Card Animation */
.partner-card-animated {
  animation: partnerCardFadeIn 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes partnerCardFadeIn {
  from {
    opacity: 0.7;
    transform: scale(0.98) translateY(3px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}


.partner-badge-general,
.partner-badge-event {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  white-space: nowrap;
  z-index: 10;
  pointer-events: none;
}

.partner-badge-general {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.3);
  padding: 0.6rem 1.2rem;
  font-size: 0.8rem;
}

.partner-badge-event {
  background: rgba(24, 114, 63, 0.1);
  color: #18723F;
  border: 1px solid rgba(24, 114, 63, 0.2);
}

.partner-badge-general i,
.partner-badge-event i {
  font-size: 0.9rem;
}

/* Placeholder Card Styles */
.partner-card-placeholder {
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border: 2px dashed rgba(24, 114, 63, 0.3);
  position: relative;
  overflow: hidden;
}

.partner-card-placeholder::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(24, 114, 63, 0.1), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%) translateY(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) translateY(100%) rotate(45deg);
  }
}

.partner-card-placeholder:hover {
  border-color: rgba(24, 114, 63, 0.5);
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.08) 0%, rgba(111, 66, 193, 0.08) 100%);
  transform: translateY(-2px);
}

.partner-placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  width: 100%;
  height: 100%;
}

.partner-placeholder-icon {
  font-size: 3rem;
  color: rgba(24, 114, 63, 0.4);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.05);
  }
}

.partner-placeholder-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18723F;
  text-align: center;
}

.partner-badge-placeholder {
  background: rgba(24, 114, 63, 0.15);
  color: #18723F;
  border: 1px solid rgba(24, 114, 63, 0.3);
  font-size: 0.7rem;
  padding: 0.4rem 0.8rem;
}

/* Horizontal Scroll Container for Mobile */
.partners-scroll-container {
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: rgba(24, 114, 63, 0.3) transparent;
  padding-bottom: 1rem;
  margin: 0 -1rem;
  padding-left: 1rem;
  padding-right: 1rem;
}

.partners-scroll-container::-webkit-scrollbar {
  height: 6px;
}

.partners-scroll-container::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
}

.partners-scroll-container::-webkit-scrollbar-thumb {
  background: rgba(24, 114, 63, 0.3);
  border-radius: 10px;
}

.partners-scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(24, 114, 63, 0.5);
}

.partners-scroll-wrapper {
  display: flex;
  gap: 1rem;
  width: max-content;
}

@media (max-width: 576px) {
  .partners-scroll-wrapper {
    gap: 0.5rem;
  }
}

/* General Sponsors Scroll - one full + half visible */
.partners-sponsors-scroll {
  padding-right: calc(50vw - 1rem);
}

.partner-sponsor-mobile {
  flex: 0 0 auto;
  width: calc(85vw - 2rem);
  max-width: 320px;
  position: relative;
  margin-right: 1rem;
}

@media (max-width: 576px) {
  .partner-sponsor-mobile {
    margin-right: 0.5rem;
  }
}


/* Responsive Design for Partners */
@media (max-width: 768px) {
  .partner-card-general {
    padding: 2rem 1.5rem;
    min-height: 240px;
  }

  .partner-card-event {
    padding: 1.5rem 1rem;
    min-height: 160px;
  }

  .partner-logo-general {
    min-height: 150px;
    padding: 2rem 1.5rem;
  }

  .partner-logo-event {
    min-height: 100px;
    padding: 1.25rem;
  }

  .partner-logo-general .partner-logo-text {
    font-size: 1.5rem;
  }

  .partner-logo-text {
    font-size: 1.2rem;
  }

  .partner-badge-general {
    padding: 0.5rem 1rem;
    font-size: 0.75rem;
  }

  .partner-badge-event {
    padding: 0.4rem 0.8rem;
    font-size: 0.7rem;
  }
}

@media (max-width: 576px) {
  .partner-card {
    min-height: 160px;
  }

  .partner-card-general {
    padding: 1.25rem 0.6rem;
    min-height: 160px;
    padding-bottom: 3rem;
  }

  .partner-card-event {
    padding: 1.25rem 0.75rem;
    min-height: 140px;
  }

  .partner-logo-general {
    min-height: 90px;
    padding: 1.25rem 0.6rem;
  }

  .partner-logo-event {
    min-height: 80px;
    padding: 1rem;
  }

  .partner-logo-general .partner-logo-text {
    font-size: 1rem;
  }

  .partner-logo-text {
    font-size: 1rem;
  }

  .partner-badge-general,
  .partner-badge-event {
    bottom: 0.5rem;
    font-size: 0.65rem;
    padding: 0.4rem 0.8rem;
    white-space: nowrap;
    max-width: calc(100% - 1rem);
    overflow: visible;
  }
  
  .partner-badge-general {
    font-size: 0.7rem;
    padding: 0.45rem 0.9rem;
  }

  .partner-logo-img {
    max-height: 70px;
  }

  /* Mobile scroll adjustments */
  .partner-sponsor-mobile {
    width: calc(80vw - 1.5rem);
    max-width: 280px;
  }


  .partners-sponsors-scroll {
    padding-right: calc(50vw - 1rem);
  }
}

/* Sponsor Modal Styles */
.sponsor-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.sponsor-modal {
  background: #fff;
  border-radius: 20px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.sponsor-modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s ease;
  color: #333;
  font-size: 1.2rem;
}

.sponsor-modal-close:hover {
  background: rgba(0, 0, 0, 0.2);
  transform: rotate(90deg);
}

.sponsor-modal-content {
  padding: 2rem;
}

.sponsor-modal-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-top: 1rem;
}

.sponsor-modal-logo {
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: center;
}

.sponsor-modal-logo img {
  max-width: 200px;
  max-height: 120px;
  object-fit: contain;
}

.sponsor-modal-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 1rem;
}

.sponsor-modal-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  color: #fff;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.sponsor-modal-description {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: rgba(24, 114, 63, 0.05);
  border-radius: 12px;
  border-left: 4px solid #18723F;
}

.sponsor-modal-description p {
  margin: 0;
  line-height: 1.7;
  color: #495057;
  font-size: 1rem;
}

.sponsor-modal-services {
  margin-bottom: 2rem;
}

.sponsor-modal-services-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #18723F;
  margin-bottom: 1rem;
}

.sponsor-modal-services-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sponsor-modal-services-list li {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem;
  margin-bottom: 0.5rem;
  background: #fff;
  border-radius: 8px;
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.sponsor-modal-services-list li:hover {
  background: rgba(24, 114, 63, 0.05);
  border-color: rgba(24, 114, 63, 0.2);
  transform: translateX(5px);
}

.sponsor-modal-services-list li i {
  color: #18723F;
  font-size: 1.1rem;
  margin-top: 0.2rem;
  flex-shrink: 0;
}

.sponsor-modal-telegram {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 2px solid rgba(24, 114, 63, 0.1);
}

.sponsor-telegram-link {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  color: #0088cc;
  text-decoration: none;
  font-size: 1rem;
  font-weight: 600;
  padding: 0.75rem 1.5rem;
  background: rgba(0, 136, 204, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.sponsor-telegram-link:hover {
  background: rgba(0, 136, 204, 0.2);
  color: #006ba3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 136, 204, 0.2);
}

.sponsor-telegram-link i {
  font-size: 1.3rem;
}

/* Mobile styles for modal */
@media (max-width: 576px) {
  .sponsor-modal-overlay {
    padding: 0.5rem;
  }

  .sponsor-modal {
    max-height: 95vh;
    border-radius: 16px;
  }

  .sponsor-modal-close {
    top: 0.75rem;
    right: 0.75rem;
    width: 36px;
    height: 36px;
    font-size: 1rem;
  }

  .sponsor-modal-content {
    padding: 1.5rem 1rem;
  }

  .sponsor-modal-header {
    margin-bottom: 1.5rem;
  }

  .sponsor-modal-logo img {
    max-width: 150px;
    max-height: 100px;
  }

  .sponsor-modal-title {
    font-size: 1.4rem;
    margin-bottom: 0.75rem;
  }

  .sponsor-modal-badge {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }

  .sponsor-modal-description {
    padding: 1rem;
    margin-bottom: 1.5rem;
  }

  .sponsor-modal-description p {
    font-size: 0.9rem;
  }

  .sponsor-modal-services-title {
    font-size: 1rem;
    margin-bottom: 0.75rem;
  }

  .sponsor-modal-services-list li {
    padding: 0.6rem;
    font-size: 0.9rem;
  }

  .sponsor-telegram-link {
    font-size: 0.9rem;
    padding: 0.6rem 1.2rem;
  }
}

/* Contacts Section Styles */
.contact-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.75rem 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(24, 114, 63, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.contact-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.contact-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: rgba(24, 114, 63, 0.25);
}

.contact-card:hover::before {
  transform: scaleX(1);
}

.contact-card-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
  font-size: 1.6rem;
  color: #fff;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.contact-icon-booking {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.25);
}

.contact-icon-partners {
  background: linear-gradient(135deg, #6f42c1 0%, #18723F 100%);
  box-shadow: 0 4px 12px rgba(111, 66, 193, 0.25);
}

.contact-card:hover .contact-card-icon {
  transform: scale(1.08) rotate(3deg);
}

.contact-card-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 0.5rem;
}

.contact-card-description {
  color: #6c757d;
  margin-bottom: 1rem;
  font-size: 0.875rem;
  line-height: 1.5;
}

.contact-phone-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  background: rgba(24, 114, 63, 0.04);
  border-radius: 12px;
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.contact-phone-wrapper:hover {
  background: rgba(24, 114, 63, 0.08);
  border-color: rgba(24, 114, 63, 0.2);
}

.contact-icons {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.contact-icon-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 1.1rem;
}

.contact-icon-link {
  color: #18723F;
  background: rgba(24, 114, 63, 0.1);
}

.contact-icon-link:hover {
  background: #18723F;
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(24, 114, 63, 0.3);
}

.contact-phone-number {
  color: #18723F;
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: color 0.3s ease;
  flex: 1;
  text-align: center;
}

.contact-phone-number:hover {
  color: #6f42c1;
}

/* Responsive Design for Contacts */
@media (max-width: 768px) {
  .contact-card {
    padding: 1.25rem 1rem;
  }

  .contact-card-icon {
    width: 50px;
    height: 50px;
    font-size: 1.25rem;
    margin-bottom: 0.75rem;
  }

  .contact-card-title {
    font-size: 1rem;
    margin-bottom: 0.35rem;
  }

  .contact-card-description {
    font-size: 0.8rem;
    margin-bottom: 0.75rem;
  }

  .contact-phone-wrapper {
    padding: 0.55rem 0.75rem;
    gap: 0.5rem;
  }

  .contact-icon-link {
    width: 28px;
    height: 28px;
    font-size: 0.9rem;
  }

  .contact-phone-number {
    font-size: 0.85rem;
  }
}

@media (max-width: 576px) {
  .contact-card {
    padding: 1rem 0.75rem;
  }

  .contact-card-icon {
    width: 44px;
    height: 44px;
    font-size: 1.1rem;
    margin-bottom: 0.6rem;
  }

  .contact-card-title {
    font-size: 0.95rem;
    margin-bottom: 0.3rem;
  }

  .contact-card-description {
    font-size: 0.75rem;
    margin-bottom: 0.6rem;
  }

  .contact-phone-wrapper {
    padding: 0.5rem 0.6rem;
    gap: 0.4rem;
    flex-wrap: wrap;
  }

  .contact-icons {
    gap: 0.3rem;
  }

  .contact-icon-link {
    width: 26px;
    height: 26px;
    font-size: 0.85rem;
  }

  .contact-phone-number {
    font-size: 0.8rem;
    width: 100%;
    text-align: center;
  }
}

/* Partners Carousel Section */
.partners-carousel-section {
  background-color: #18723F;
  padding: 0 0 3rem 0;
  overflow: hidden;
  position: relative;
}

.partners-carousel-container {
  width: 100%;
  overflow: hidden;
  position: relative;
}

.partners-carousel-track {
  display: flex;
  align-items: center;
  gap: 4rem;
  animation: scroll 30s linear infinite;
  will-change: transform;
}

.partners-carousel-item {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  padding: 0 1.5rem;
  margin: 0;
}

.partners-carousel-logo {
  max-height: 100%;
  max-width: 120px;
  width: auto;
  height: auto;
  object-fit: contain;
  opacity: 0.9;
  transition: opacity 0.3s ease;
  /* Изменяем цвет через CSS filter для карусели */
  filter: brightness(0) saturate(100%) invert(90%) sepia(0%) saturate(0%) hue-rotate(0deg);
}

.partners-carousel-logo:hover {
  opacity: 1;
}

.partners-carousel-logo-asaki {
  transform: translateY(10px);
}

@keyframes scroll {
  0% {
    transform: translateX(-50%);
  }
  100% {
    transform: translateX(0);
  }
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .partners-carousel-section {
    padding: 0 0 2rem 0;
  }

  .partners-carousel-item {
    height: 50px;
    padding: 0 1rem;
  }

  .partners-carousel-logo {
    max-width: 100px;
  }

  .partners-carousel-track {
    gap: 2rem;
  }
}

@media (max-width: 576px) {
  .partners-carousel-section {
    padding: 0 0 1.5rem 0;
  }

  .partners-carousel-item {
    height: 40px;
    padding: 0 0.75rem;
  }

  .partners-carousel-logo {
    max-width: 80px;
  }

  .partners-carousel-track {
    gap: 1.5rem;
  }
}

/* Why Not Expensive Section - Professional Design */
.why-not-expensive-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.why-not-expensive-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 50%, #18723F 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

@keyframes gradientShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.why-card {
  background: #fff;
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(24, 114, 63, 0.1);
  border: 2px solid transparent;
  background-image: linear-gradient(#fff, #fff), 
                    linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1));
  background-origin: border-box;
  background-clip: padding-box, border-box;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardFadeIn 0.6s ease-out;
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.why-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(24, 114, 63, 0.05) 0%, transparent 70%);
  animation: pulseGlow 4s ease-in-out infinite;
  pointer-events: none;
}

@keyframes pulseGlow {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

.why-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(24, 114, 63, 0.2);
}

.why-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.why-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  position: relative;
}

.why-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  opacity: 0.3;
  z-index: -1;
  animation: iconPulse 2s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.2);
    opacity: 0;
  }
}

.why-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.why-title {
  font-size: 2.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.why-content {
  position: relative;
  z-index: 1;
}

.why-question-block {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border-radius: 16px;
  border-left: 4px solid #18723F;
}

.why-question {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 0.75rem;
}

.why-answer {
  font-size: 1.05rem;
  color: #555;
  margin: 0;
  line-height: 1.5;
}

.why-features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.why-feature-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: #fff;
  border-radius: 14px;
  border: 2px solid rgba(24, 114, 63, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  opacity: 0;
  animation: featureSlideIn 0.6s ease-out forwards;
}

@keyframes featureSlideIn {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.why-feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(24, 114, 63, 0.1), transparent);
  transition: left 0.5s ease;
}

.why-feature-item:hover::before {
  left: 100%;
}

.why-feature-item:hover,
.why-feature-item:active {
  transform: translateX(8px) translateY(-4px);
  border-color: #18723F;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.15);
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.02) 0%, rgba(111, 66, 193, 0.02) 100%);
}

.why-feature-item:active {
  transform: translateX(4px) translateY(-2px);
}

.why-feature-icon {
  width: 50px;
  height: 50px;
  min-width: 50px;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1) 0%, rgba(111, 66, 193, 0.1) 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  animation: iconBounce 3s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.why-feature-item:hover .why-feature-icon,
.why-feature-item:active .why-feature-icon {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  transform: scale(1.1) rotate(5deg) translateY(-5px);
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.3);
  animation: iconBounceHover 0.6s ease-in-out;
}

@keyframes iconBounceHover {
  0%, 100% {
    transform: scale(1.1) rotate(5deg) translateY(-5px);
  }
  50% {
    transform: scale(1.15) rotate(8deg) translateY(-8px);
  }
}

.why-feature-icon::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.2), rgba(111, 66, 193, 0.2));
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
  animation: iconGlow 2s ease-in-out infinite;
}

@keyframes iconGlow {
  0%, 100% {
    opacity: 0;
    transform: scale(1);
  }
  50% {
    opacity: 0.3;
    transform: scale(1.1);
  }
}

.why-feature-item:hover .why-feature-icon::before,
.why-feature-item:active .why-feature-icon::before {
  opacity: 0.5;
  animation: iconGlowActive 1s ease-in-out infinite;
}

@keyframes iconGlowActive {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1.1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.2);
  }
}

.why-feature-icon i {
  font-size: 1.5rem;
  color: #18723F;
  transition: all 0.3s ease;
  animation: iconPulse 2s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.why-feature-item:hover .why-feature-icon i,
.why-feature-item:active .why-feature-icon i {
  color: #fff;
  transform: scale(1.1);
  animation: iconPulseHover 0.5s ease-in-out;
}

@keyframes iconPulseHover {
  0%, 100% {
    transform: scale(1.1);
  }
  50% {
    transform: scale(1.2);
  }
}

.why-feature-content {
  flex: 1;
}

.why-feature-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.why-highlight-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.25rem 1.5rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  animation: highlightPulse 2s ease-in-out infinite;
  position: relative;
  overflow: hidden;
}

.why-highlight-block::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: highlightShine 3s ease-in-out infinite;
}

@keyframes highlightPulse {
  0%, 100% {
    box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  }
  50% {
    box-shadow: 0 12px 32px rgba(24, 114, 63, 0.4);
  }
}

@keyframes highlightShine {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.why-highlight-icon {
  font-size: 1.75rem;
  color: #ffc107;
  animation: starRotate 3s ease-in-out infinite;
  filter: drop-shadow(0 0 8px rgba(255, 193, 7, 0.5));
  flex-shrink: 0;
}

@keyframes starRotate {
  0%, 100% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.1);
  }
}

.why-highlight-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.why-cta-wrapper {
  text-align: center;
}

.why-cta-button {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.why-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.why-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.why-cta-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(24, 114, 63, 0.4);
}

.why-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.why-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

@keyframes pointRight {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(5px);
  }
}

.why-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Why Section */
@media (max-width: 991px) {
  .why-card {
    padding: 2rem 2rem;
  }

  .why-title {
    font-size: 1.875rem;
  }

  .why-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.875rem;
  }
}

@media (max-width: 768px) {
  .why-not-expensive-section {
    padding: 2rem 0 !important;
  }

  .why-card {
    padding: 2rem 1.5rem;
    border-radius: 20px;
    margin: 0 0.5rem;
  }

  .why-header {
    margin-bottom: 1.5rem;
  }

  .why-title {
    font-size: 1.625rem;
    line-height: 1.2;
  }

  .why-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 1rem;
  }

  .why-icon-wrapper i {
    font-size: 1.75rem;
  }

  .why-question-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
  }

  .why-question {
    font-size: 1.1rem;
    margin-bottom: 0.625rem;
  }

  .why-answer {
    font-size: 0.95rem;
    line-height: 1.4;
  }

  .why-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .why-feature-item {
    padding: 1rem 0.75rem;
    gap: 0.625rem;
    border-radius: 12px;
    flex-direction: column;
    text-align: center;
    min-height: 100px;
    justify-content: center;
  }

  .why-feature-icon {
    width: 48px;
    height: 48px;
    min-width: 48px;
    border-radius: 12px;
    margin: 0 auto;
  }

  .why-feature-icon i {
    font-size: 1.4rem;
  }

  .why-feature-content {
    width: 100%;
  }

  .why-feature-text {
    font-size: 0.95rem;
    line-height: 1.3;
    display: block;
  }

  .why-highlight-block {
    flex-direction: row;
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
    gap: 0.75rem;
  }

  .why-highlight-icon {
    font-size: 1.5rem;
    flex-shrink: 0;
  }

  .why-highlight-text {
    font-size: 1.1rem;
    text-align: left;
    line-height: 1.3;
  }

  .why-cta-button {
    font-size: 1.05rem;
    padding: 1rem 2.5rem;
    width: 100%;
    max-width: 100%;
    border-radius: 40px;
  }

  .why-cta-icon {
    font-size: 1.35rem;
  }
}

@media (max-width: 576px) {
  .why-not-expensive-section {
    padding: 1.5rem 0 !important;
  }

  .why-card {
    padding: 1.5rem 1rem;
    border-radius: 18px;
    margin: 0 0.75rem;
  }

  .why-header {
    margin-bottom: 1.25rem;
  }

  .why-title {
    font-size: 1.5rem;
    line-height: 1.25;
  }

  .why-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.75rem;
  }

  .why-icon-wrapper i {
    font-size: 1.5rem;
  }

  .why-question-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    border-left-width: 3px;
  }

  .why-question {
    font-size: 1rem;
    margin-bottom: 0.5rem;
  }

  .why-answer {
    font-size: 0.9rem;
    line-height: 1.4;
  }

  .why-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.625rem;
    margin-bottom: 1.25rem;
  }

  .why-feature-item {
    padding: 0.875rem 0.5rem;
    gap: 0.5rem;
    border-radius: 12px;
    flex-direction: column;
    text-align: center;
    min-height: 95px;
    justify-content: center;
  }

  .why-feature-item:hover,
  .why-feature-item:active {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(24, 114, 63, 0.15);
  }

  .why-feature-item:active {
    transform: translateY(-2px);
  }

  .why-feature-icon {
    width: 48px;
    height: 48px;
    min-width: 48px;
    border-radius: 12px;
    margin: 0 auto;
  }

  .why-feature-icon i {
    font-size: 1.4rem;
  }

  .why-feature-content {
    width: 100%;
  }

  .why-feature-text {
    font-size: 0.9rem;
    line-height: 1.3;
    display: block;
  }

  .why-highlight-block {
    flex-direction: column;
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    gap: 0.625rem;
    text-align: center;
  }

  .why-highlight-icon {
    font-size: 1.35rem;
  }

  .why-highlight-text {
    font-size: 1rem;
    text-align: center;
    line-height: 1.3;
  }

  .why-cta-button {
    font-size: 0.95rem;
    padding: 0.875rem 2rem;
    border-radius: 35px;
    gap: 0.625rem;
  }

  .why-cta-icon {
    font-size: 1.25rem;
  }

  .why-cta-text {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .why-not-expensive-section {
    padding: 1.25rem 0 !important;
  }

  .why-card {
    padding: 1.25rem 0.875rem;
    margin: 0 0.5rem;
  }

  .why-title {
    font-size: 1.375rem;
  }

  .why-icon-wrapper {
    width: 50px;
    height: 50px;
  }

  .why-icon-wrapper i {
    font-size: 1.5rem;
  }

  .why-question-block {
    padding: 1rem;
  }

  .why-question {
    font-size: 1rem;
  }

  .why-answer {
    font-size: 0.9rem;
  }

  .why-feature-item {
    padding: 0.875rem 0.625rem;
    gap: 0.625rem;
    min-height: 110px;
  }

  .why-feature-icon {
    width: 44px;
    height: 44px;
    min-width: 44px;
  }

  .why-feature-icon i {
    font-size: 1.3rem;
  }

  .why-feature-text {
    font-size: 0.85rem;
    line-height: 1.25;
  }

  .why-highlight-block {
    padding: 1rem;
  }

  .why-highlight-text {
    font-size: 1rem;
  }

  .why-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.75rem;
  }
}

/* Who Should Go Section - Professional Design */
.who-should-go-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  position: relative;
  overflow: hidden;
}

.who-should-go-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #6f42c1 0%, #18723F 50%, #6f42c1 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.who-card {
  background: #fff;
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(111, 66, 193, 0.1);
  border: 2px solid transparent;
  background-image: linear-gradient(#fff, #fff), 
                    linear-gradient(135deg, rgba(111, 66, 193, 0.1), rgba(24, 114, 63, 0.1));
  background-origin: border-box;
  background-clip: padding-box, border-box;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardFadeIn 0.6s ease-out;
}

.who-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(111, 66, 193, 0.05) 0%, transparent 70%);
  animation: pulseGlow 4s ease-in-out infinite;
  pointer-events: none;
}

.who-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(111, 66, 193, 0.2);
}

.who-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.who-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #6f42c1 0%, #18723F 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(111, 66, 193, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  position: relative;
}

.who-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6f42c1 0%, #18723F 100%);
  opacity: 0.3;
  z-index: -1;
  animation: iconPulse 2s ease-in-out infinite;
}

.who-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.who-title {
  font-size: 2.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, #6f42c1 0%, #18723F 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.who-content {
  position: relative;
  z-index: 1;
}

.who-intro-block {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, rgba(111, 66, 193, 0.05) 0%, rgba(24, 114, 63, 0.05) 100%);
  border-radius: 16px;
  border-left: 4px solid #6f42c1;
}

.who-intro {
  font-size: 1.25rem;
  font-weight: 700;
  color: #6f42c1;
  margin: 0;
  line-height: 1.5;
}

.who-features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.who-feature-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: #fff;
  border-radius: 14px;
  border: 2px solid rgba(111, 66, 193, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  opacity: 0;
  animation: featureSlideIn 0.6s ease-out forwards;
}

.who-feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(111, 66, 193, 0.1), transparent);
  transition: left 0.5s ease;
}

.who-feature-item:hover::before,
.who-feature-item:active::before {
  left: 100%;
}

.who-feature-item:hover,
.who-feature-item:active {
  transform: translateX(8px) translateY(-4px);
  border-color: #6f42c1;
  box-shadow: 0 8px 24px rgba(111, 66, 193, 0.15);
  background: linear-gradient(135deg, rgba(111, 66, 193, 0.02) 0%, rgba(24, 114, 63, 0.02) 100%);
}

.who-feature-item:active {
  transform: translateX(4px) translateY(-2px);
}

.who-feature-icon {
  width: 50px;
  height: 50px;
  min-width: 50px;
  background: linear-gradient(135deg, rgba(111, 66, 193, 0.1) 0%, rgba(24, 114, 63, 0.1) 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  animation: iconBounce 3s ease-in-out infinite;
}

.who-feature-item:hover .who-feature-icon,
.who-feature-item:active .who-feature-icon {
  background: linear-gradient(135deg, #6f42c1 0%, #18723F 100%);
  transform: scale(1.1) rotate(5deg) translateY(-5px);
  box-shadow: 0 4px 12px rgba(111, 66, 193, 0.3);
  animation: iconBounceHover 0.6s ease-in-out;
}

.who-feature-icon::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(111, 66, 193, 0.2), rgba(24, 114, 63, 0.2));
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
  animation: iconGlow 2s ease-in-out infinite;
}

.who-feature-item:hover .who-feature-icon::before,
.who-feature-item:active .who-feature-icon::before {
  opacity: 0.5;
  animation: iconGlowActive 1s ease-in-out infinite;
}

.who-feature-icon i {
  font-size: 1.5rem;
  color: #6f42c1;
  transition: all 0.3s ease;
  animation: iconPulse 2s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

.who-feature-item:hover .who-feature-icon i,
.who-feature-item:active .who-feature-icon i {
  color: #fff;
  transform: scale(1.1);
  animation: iconPulseHover 0.5s ease-in-out;
}

.who-feature-content {
  flex: 1;
}

.who-feature-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.who-note-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, rgba(111, 66, 193, 0.1) 0%, rgba(24, 114, 63, 0.1) 100%);
  border-radius: 12px;
  border: 2px solid rgba(111, 66, 193, 0.2);
}

.who-note-icon {
  font-size: 1.5rem;
  color: #6f42c1;
  animation: notePulse 2s ease-in-out infinite;
  flex-shrink: 0;
}

@keyframes notePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.who-note-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.who-note-text strong {
  color: #6f42c1;
}

.who-cta-wrapper {
  text-align: center;
}

.who-cta-button {
  background: linear-gradient(135deg, #6f42c1 0%, #18723F 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(111, 66, 193, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.who-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.who-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.who-cta-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(111, 66, 193, 0.4);
}

.who-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.who-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

.who-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Who Section */
@media (max-width: 991px) {
  .who-card {
    padding: 2rem 2rem;
  }

  .who-title {
    font-size: 1.875rem;
  }

  .who-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.875rem;
  }
}

@media (max-width: 768px) {
  .who-should-go-section {
    padding: 2rem 0 !important;
  }

  .who-card {
    padding: 2rem 1.5rem;
    border-radius: 20px;
    margin: 0 0.5rem;
  }

  .who-header {
    margin-bottom: 1.5rem;
  }

  .who-title {
    font-size: 1.625rem;
    line-height: 1.2;
  }

  .who-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 1rem;
  }

  .who-icon-wrapper i {
    font-size: 2rem;
  }

  .who-intro-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
  }

  .who-intro {
    font-size: 1.1rem;
    line-height: 1.4;
  }

  .who-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .who-feature-item {
    padding: 1rem 0.75rem;
    gap: 0.625rem;
    border-radius: 12px;
    flex-direction: column;
    text-align: center;
    min-height: 100px;
    justify-content: center;
  }

  .who-feature-item:hover,
  .who-feature-item:active {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(111, 66, 193, 0.15);
  }

  .who-feature-item:active {
    transform: translateY(-2px);
  }

  .who-feature-icon {
    width: 50px;
    height: 50px;
    min-width: 50px;
    border-radius: 12px;
    margin: 0 auto;
  }

  .who-feature-icon i {
    font-size: 1.5rem;
  }

  .who-feature-content {
    width: 100%;
  }

  .who-feature-text {
    font-size: 1.05rem;
    line-height: 1.3;
    display: block;
  }

  .who-note-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.5rem;
    gap: 0.625rem;
  }

  .who-note-icon {
    font-size: 1.35rem;
  }

  .who-note-text {
    font-size: 0.95rem;
  }

  .who-cta-button {
    font-size: 1.05rem;
    padding: 1rem 2.5rem;
    width: 100%;
    max-width: 100%;
    border-radius: 40px;
  }

  .who-cta-icon {
    font-size: 1.35rem;
  }
}

@media (max-width: 576px) {
  .who-should-go-section {
    padding: 1.5rem 0 !important;
  }

  .who-card {
    padding: 1.5rem 1rem;
    border-radius: 18px;
    margin: 0 0.75rem;
  }

  .who-header {
    margin-bottom: 1.25rem;
  }

  .who-title {
    font-size: 1.5rem;
    line-height: 1.25;
  }

  .who-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.75rem;
  }

  .who-icon-wrapper i {
    font-size: 1.75rem;
  }

  .who-intro-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    border-left-width: 3px;
  }

  .who-intro {
    font-size: 1rem;
    line-height: 1.4;
  }

  .who-features-grid {
    gap: 0.625rem;
    margin-bottom: 1.25rem;
  }

  .who-feature-item {
    padding: 0.875rem 0.5rem;
    gap: 0.5rem;
    min-height: 95px;
  }

  .who-feature-icon {
    width: 48px;
    height: 48px;
    min-width: 48px;
    border-radius: 12px;
  }

  .who-feature-icon i {
    font-size: 1.4rem;
  }

  .who-feature-text {
    font-size: 0.9rem;
    line-height: 1.3;
  }

  .who-note-block {
    padding: 0.75rem 0.875rem;
    margin-bottom: 1.25rem;
    flex-direction: column;
    text-align: center;
  }

  .who-note-icon {
    font-size: 1.25rem;
  }

  .who-note-text {
    font-size: 0.9rem;
  }

  .who-cta-button {
    font-size: 0.95rem;
    padding: 0.875rem 2rem;
    border-radius: 35px;
    gap: 0.625rem;
  }

  .who-cta-icon {
    font-size: 1.25rem;
  }

  .who-cta-text {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .who-should-go-section {
    padding: 1.25rem 0 !important;
  }

  .who-card {
    padding: 1.25rem 0.875rem;
    margin: 0 0.5rem;
  }

  .who-title {
    font-size: 1.375rem;
  }

  .who-icon-wrapper {
    width: 48px;
    height: 48px;
  }

  .who-icon-wrapper i {
    font-size: 1.5rem;
  }

  .who-intro-block {
    padding: 1rem;
  }

  .who-intro {
    font-size: 1rem;
  }

  .who-feature-item {
    padding: 0.875rem 0.625rem;
    gap: 0.625rem;
    min-height: 100px;
  }

  .who-feature-icon {
    width: 44px;
    height: 44px;
    min-width: 44px;
  }

  .who-feature-icon i {
    font-size: 1.3rem;
  }

  .who-feature-text {
    font-size: 0.85rem;
    line-height: 1.25;
  }

  .who-note-block {
    padding: 0.875rem;
  }

  .who-note-text {
    font-size: 0.95rem;
  }

  .who-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.75rem;
  }
}

/* Not Boring Section - Professional Design */
.not-boring-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.not-boring-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ffc107 0%, #18723F 50%, #ffc107 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.not-boring-card {
  background: #fff;
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(255, 193, 7, 0.1);
  border: 2px solid transparent;
  background-image: linear-gradient(#fff, #fff), 
                    linear-gradient(135deg, rgba(255, 193, 7, 0.1), rgba(24, 114, 63, 0.1));
  background-origin: border-box;
  background-clip: padding-box, border-box;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardFadeIn 0.6s ease-out;
}

.not-boring-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 193, 7, 0.05) 0%, transparent 70%);
  animation: pulseGlow 4s ease-in-out infinite;
  pointer-events: none;
}

.not-boring-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(255, 193, 7, 0.2);
}

.not-boring-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.not-boring-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #ffc107 0%, #18723F 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(255, 193, 7, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  position: relative;
}

.not-boring-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffc107 0%, #18723F 100%);
  opacity: 0.3;
  z-index: -1;
  animation: iconPulse 2s ease-in-out infinite;
}

.not-boring-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.not-boring-title {
  font-size: 2.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, #ffc107 0%, #18723F 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.not-boring-content {
  position: relative;
  z-index: 1;
}

.not-boring-intro-block {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.05) 0%, rgba(24, 114, 63, 0.05) 100%);
  border-radius: 16px;
  border-left: 4px solid #ffc107;
}

.not-boring-intro {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin: 0;
  line-height: 1.5;
}

.not-boring-events-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.not-boring-event-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: #fff;
  border-radius: 14px;
  border: 2px solid rgba(255, 193, 7, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  opacity: 0;
  animation: featureSlideIn 0.6s ease-out forwards;
}

.not-boring-event-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 193, 7, 0.1), transparent);
  transition: left 0.5s ease;
}

.not-boring-event-item:hover::before,
.not-boring-event-item:active::before {
  left: 100%;
}

.not-boring-event-item:hover,
.not-boring-event-item:active {
  transform: translateX(8px) translateY(-4px);
  border-color: #ffc107;
  box-shadow: 0 8px 24px rgba(255, 193, 7, 0.15);
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.02) 0%, rgba(24, 114, 63, 0.02) 100%);
}

.not-boring-event-item:active {
  transform: translateX(4px) translateY(-2px);
}

.not-boring-event-icon {
  width: 50px;
  height: 50px;
  min-width: 50px;
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.1) 0%, rgba(24, 114, 63, 0.1) 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  animation: iconBounce 3s ease-in-out infinite;
}

.not-boring-event-item:hover .not-boring-event-icon,
.not-boring-event-item:active .not-boring-event-icon {
  background: linear-gradient(135deg, #ffc107 0%, #18723F 100%);
  transform: scale(1.1) rotate(5deg) translateY(-5px);
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
  animation: iconBounceHover 0.6s ease-in-out;
}

.not-boring-event-icon::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.2), rgba(24, 114, 63, 0.2));
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
  animation: iconGlow 2s ease-in-out infinite;
}

.not-boring-event-item:hover .not-boring-event-icon::before,
.not-boring-event-item:active .not-boring-event-icon::before {
  opacity: 0.5;
  animation: iconGlowActive 1s ease-in-out infinite;
}

.not-boring-event-icon i {
  font-size: 1.5rem;
  color: #ffc107;
  transition: all 0.3s ease;
  animation: iconPulse 2s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

.not-boring-event-item:hover .not-boring-event-icon i,
.not-boring-event-item:active .not-boring-event-icon i {
  color: #fff;
  transform: scale(1.1);
  animation: iconPulseHover 0.5s ease-in-out;
}

.not-boring-event-content {
  flex: 1;
}

.not-boring-event-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.not-boring-highlight-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.25rem 1.5rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, #ffc107 0%, #18723F 100%);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(255, 193, 7, 0.3);
  animation: highlightPulse 2s ease-in-out infinite;
  position: relative;
  overflow: hidden;
}

.not-boring-highlight-block::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: highlightShine 3s ease-in-out infinite;
}

.not-boring-highlight-icon {
  font-size: 1.75rem;
  color: #fff;
  animation: clockRotate 3s ease-in-out infinite;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
  flex-shrink: 0;
}

@keyframes clockRotate {
  0%, 100% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.1);
  }
}

.not-boring-highlight-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.not-boring-cta-wrapper {
  text-align: center;
}

.not-boring-cta-button {
  background: linear-gradient(135deg, #ffc107 0%, #18723F 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(255, 193, 7, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.not-boring-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.not-boring-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.not-boring-cta-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(255, 193, 7, 0.4);
}

.not-boring-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.not-boring-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

.not-boring-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Not Boring Section */
@media (max-width: 991px) {
  .not-boring-card {
    padding: 2rem 2rem;
  }

  .not-boring-title {
    font-size: 1.875rem;
  }

  .not-boring-events-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.875rem;
  }
}

@media (max-width: 768px) {
  .not-boring-section {
    padding: 2rem 0 !important;
  }

  .not-boring-card {
    padding: 2rem 1.5rem;
    border-radius: 20px;
    margin: 0 0.5rem;
  }

  .not-boring-header {
    margin-bottom: 1.5rem;
  }

  .not-boring-title {
    font-size: 1.625rem;
    line-height: 1.2;
  }

  .not-boring-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 1rem;
  }

  .not-boring-icon-wrapper i {
    font-size: 1.75rem;
  }

  .not-boring-intro-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
  }

  .not-boring-intro {
    font-size: 1.1rem;
    line-height: 1.4;
  }

  .not-boring-events-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .not-boring-event-item {
    padding: 1rem 0.75rem;
    gap: 0.625rem;
    border-radius: 12px;
    flex-direction: column;
    text-align: center;
    min-height: 100px;
    justify-content: center;
  }

  .not-boring-event-item:hover,
  .not-boring-event-item:active {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(255, 193, 7, 0.15);
  }

  .not-boring-event-item:active {
    transform: translateY(-2px);
  }

  .not-boring-event-icon {
    width: 48px;
    height: 48px;
    min-width: 48px;
    border-radius: 12px;
    margin: 0 auto;
  }

  .not-boring-event-icon i {
    font-size: 1.4rem;
  }

  .not-boring-event-content {
    width: 100%;
  }

  .not-boring-event-text {
    font-size: 0.95rem;
    line-height: 1.3;
    display: block;
  }

  .not-boring-highlight-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
    gap: 0.75rem;
  }

  .not-boring-highlight-icon {
    font-size: 1.5rem;
  }

  .not-boring-highlight-text {
    font-size: 1.1rem;
    line-height: 1.3;
  }

  .not-boring-cta-button {
    font-size: 1.05rem;
    padding: 1rem 2.5rem;
    width: 100%;
    max-width: 100%;
    border-radius: 40px;
  }

  .not-boring-cta-icon {
    font-size: 1.35rem;
  }
}

@media (max-width: 576px) {
  .not-boring-section {
    padding: 1.5rem 0 !important;
  }

  .not-boring-card {
    padding: 1.5rem 1rem;
    border-radius: 18px;
    margin: 0 0.75rem;
  }

  .not-boring-header {
    margin-bottom: 1.25rem;
  }

  .not-boring-title {
    font-size: 1.5rem;
    line-height: 1.25;
  }

  .not-boring-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.75rem;
  }

  .not-boring-icon-wrapper i {
    font-size: 1.5rem;
  }

  .not-boring-intro-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    border-left-width: 3px;
  }

  .not-boring-intro {
    font-size: 1rem;
    line-height: 1.4;
  }

  .not-boring-events-grid {
    gap: 0.625rem;
    margin-bottom: 1.25rem;
  }

  .not-boring-event-item {
    padding: 0.875rem 0.5rem;
    gap: 0.5rem;
    min-height: 95px;
  }

  .not-boring-event-icon {
    width: 44px;
    height: 44px;
    min-width: 44px;
    border-radius: 12px;
  }

  .not-boring-event-icon i {
    font-size: 1.3rem;
  }

  .not-boring-event-text {
    font-size: 0.85rem;
    line-height: 1.25;
  }

  .not-boring-highlight-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    gap: 0.625rem;
    flex-direction: column;
    text-align: center;
  }

  .not-boring-highlight-icon {
    font-size: 1.35rem;
  }

  .not-boring-highlight-text {
    font-size: 1rem;
    line-height: 1.3;
  }

  .not-boring-cta-button {
    font-size: 0.95rem;
    padding: 0.875rem 2rem;
    border-radius: 35px;
    gap: 0.625rem;
  }

  .not-boring-cta-icon {
    font-size: 1.25rem;
  }

  .not-boring-cta-text {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .not-boring-section {
    padding: 1.25rem 0 !important;
  }

  .not-boring-card {
    padding: 1.25rem 0.875rem;
    margin: 0 0.5rem;
  }

  .not-boring-title {
    font-size: 1.375rem;
  }

  .not-boring-icon-wrapper {
    width: 48px;
    height: 48px;
  }

  .not-boring-icon-wrapper i {
    font-size: 1.35rem;
  }

  .not-boring-intro-block {
    padding: 0.75rem 0.875rem;
  }

  .not-boring-intro {
    font-size: 0.95rem;
  }

  .not-boring-event-item {
    padding: 0.75rem 0.5rem;
    min-height: 90px;
  }

  .not-boring-event-icon {
    width: 40px;
    height: 40px;
    min-width: 40px;
  }

  .not-boring-event-icon i {
    font-size: 1.2rem;
  }

  .not-boring-event-text {
    font-size: 0.8rem;
  }

  .not-boring-highlight-block {
    padding: 0.75rem 0.875rem;
  }

  .not-boring-highlight-text {
    font-size: 0.95rem;
  }

  .not-boring-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.75rem;
  }
}

/* Artist Section - Professional Design with Dark Background */
.artist-section {
  background: #18723F;
  position: relative;
  overflow: hidden;
}

.artist-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 70% 50%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.artist-header {
  position: relative;
  z-index: 1;
  margin-bottom: 2.5rem;
}

.artist-section-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.artist-section-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.artist-section-title {
  font-size: 2.25rem;
  font-weight: 800;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  letter-spacing: -0.5px;
}

.artists-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 100px;
  margin-bottom: 2.5rem;
  position: relative;
  z-index: 1;
}

.artist-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  overflow: visible;
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  opacity: 0;
  animation: cardFadeIn 0.6s ease-out forwards;
  position: relative;
  padding-top: 120px;
}

.artist-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.artist-card:hover::before {
  opacity: 1;
}

.artist-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  border-color: rgba(255, 255, 255, 0.4);
}

.artist-card-image-wrapper {
  position: absolute;
  top: -80px;
  left: 50%;
  transform: translateX(-50%);
  width: 200px;
  height: 200px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
  z-index: 10;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.1);
}

.artist-card:hover .artist-card-image-wrapper {
  transform: translateX(-50%) translateY(-10px) scale(1.05);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.5);
  border-color: rgba(255, 255, 255, 0.5);
}

.artist-card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center top;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.artist-card:hover .artist-card-image {
  transform: scale(1.1);
}

.artist-card-content {
  padding: 1.5rem;
  padding-top: 2rem;
  position: relative;
  z-index: 1;
  text-align: center;
}

.artist-card-name {
  font-size: 1.75rem;
  font-weight: 800;
  color: #fff;
  margin-bottom: 0.5rem;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.artist-card-role {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 1.25rem;
  font-weight: 500;
}

.artist-card-features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.artist-card-features li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0;
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.95);
}

.artist-card-features li i {
  color: #ffc107;
  font-size: 1.1rem;
  flex-shrink: 0;
  animation: checkPulse 2s ease-in-out infinite;
}

@keyframes checkPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.artist-cta-wrapper {
  position: relative;
  z-index: 1;
}

.artist-cta-button {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.artist-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.artist-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.artist-cta-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
}

.artist-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.artist-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

.artist-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Artist Section */
@media (max-width: 991px) {
  .artists-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
    margin-top: 90px;
  }

  .artist-card {
    padding-top: 100px;
  }

  .artist-card-image-wrapper {
    width: 180px;
    height: 180px;
    top: -70px;
  }
}

@media (max-width: 768px) {
  .artist-section {
    padding: 2rem 0 !important;
  }

  .artist-header {
    margin-bottom: 2rem;
  }

  .artist-section-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 0.875rem;
  }

  .artist-section-icon-wrapper i {
    font-size: 1.75rem;
  }

  .artist-section-title {
    font-size: 1.875rem;
  }

  .artists-grid {
    grid-template-columns: 1fr;
    gap: 2rem;
    margin-top: 80px;
    margin-bottom: 2rem;
  }

  .artist-card {
    padding-top: 90px;
  }

  .artist-card-image-wrapper {
    width: 160px;
    height: 160px;
    top: -60px;
  }

  .artist-card-content {
    padding: 1.25rem;
  }

  .artist-card-name {
    font-size: 1.5rem;
  }

  .artist-card-role {
    font-size: 0.95rem;
    margin-bottom: 1rem;
  }

  .artist-card-features li {
    font-size: 0.9rem;
    padding: 0.4rem 0;
  }

  .artist-cta-button {
    font-size: 1.05rem;
    padding: 0.875rem 2rem;
    width: 100%;
    max-width: 100%;
  }
}

@media (max-width: 576px) {
  .artist-section {
    padding: 1.5rem 0 !important;
  }

  .artist-header {
    margin-bottom: 1.5rem;
  }

  .artist-section-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.75rem;
  }

  .artist-section-icon-wrapper i {
    font-size: 1.5rem;
  }

  .artist-section-title {
    font-size: 1.625rem;
  }

  .artists-grid {
    gap: 1.75rem;
    margin-top: 70px;
    margin-bottom: 1.5rem;
  }

  .artist-card {
    border-radius: 16px;
    padding-top: 80px;
  }

  .artist-card-image-wrapper {
    width: 140px;
    height: 140px;
    top: -50px;
    border-width: 3px;
  }

  .artist-card-content {
    padding: 1rem;
  }

  .artist-card-name {
    font-size: 1.35rem;
  }

  .artist-card-role {
    font-size: 0.9rem;
    margin-bottom: 0.875rem;
  }

  .artist-card-features li {
    font-size: 0.85rem;
    padding: 0.35rem 0;
  }

  .artist-cta-button {
    font-size: 0.95rem;
    padding: 0.875rem 1.75rem;
  }
}

@media (max-width: 480px) {
  .artist-section {
    padding: 1.25rem 0 !important;
  }

  .artist-header {
    margin-bottom: 1.25rem;
  }

  .artist-section-icon-wrapper {
    width: 48px;
    height: 48px;
  }

  .artist-section-icon-wrapper i {
    font-size: 1.35rem;
  }

  .artist-section-title {
    font-size: 1.5rem;
  }

  .artists-grid {
    gap: 1.5rem;
    margin-top: 60px;
  }

  .artist-card {
    padding-top: 70px;
  }

  .artist-card-image-wrapper {
    width: 120px;
    height: 120px;
    top: -40px;
    border-width: 3px;
  }

  .artist-card-content {
    padding: 0.875rem;
  }

  .artist-card-name {
    font-size: 1.25rem;
  }

  .artist-card-features li {
    font-size: 0.8rem;
  }

  .artist-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.5rem;
  }
}

/* Food Section - Professional Design */
.food-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  position: relative;
  overflow: hidden;
}

.food-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ff9800 0%, #18723F 50%, #ff9800 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.food-card {
  background: #fff;
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(255, 152, 0, 0.1);
  border: 2px solid transparent;
  background-image: linear-gradient(#fff, #fff), 
                    linear-gradient(135deg, rgba(255, 152, 0, 0.1), rgba(24, 114, 63, 0.1));
  background-origin: border-box;
  background-clip: padding-box, border-box;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardFadeIn 0.6s ease-out;
}

.food-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 152, 0, 0.05) 0%, transparent 70%);
  animation: pulseGlow 4s ease-in-out infinite;
  pointer-events: none;
}

.food-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(255, 152, 0, 0.2);
}

.food-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.food-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #ff9800 0%, #18723F 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(255, 152, 0, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  position: relative;
}

.food-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9800 0%, #18723F 100%);
  opacity: 0.3;
  z-index: -1;
  animation: iconPulse 2s ease-in-out infinite;
}

.food-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.food-title {
  font-size: 2.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, #ff9800 0%, #18723F 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.food-content {
  position: relative;
  z-index: 1;
}

.food-intro-block {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.05) 0%, rgba(24, 114, 63, 0.05) 100%);
  border-radius: 16px;
  border-left: 4px solid #ff9800;
}

.food-intro {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin: 0;
  line-height: 1.5;
}

.food-categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.food-category-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  border: 2px solid rgba(255, 152, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  opacity: 0;
  animation: featureSlideIn 0.6s ease-out forwards;
}

.food-category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 152, 0, 0.1), transparent);
  transition: left 0.5s ease;
}

.food-category-card:hover::before,
.food-category-card:active::before {
  left: 100%;
}

.food-category-card:hover,
.food-category-card:active {
  transform: translateY(-6px);
  border-color: #ff9800;
  box-shadow: 0 8px 24px rgba(255, 152, 0, 0.15);
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.02) 0%, rgba(24, 114, 63, 0.02) 100%);
}

.food-category-card:active {
  transform: translateY(-3px);
}

.food-category-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.1) 0%, rgba(24, 114, 63, 0.1) 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  transition: all 0.3s ease;
  position: relative;
  animation: iconBounce 3s ease-in-out infinite;
}

.food-category-card:hover .food-category-icon,
.food-category-card:active .food-category-icon {
  background: linear-gradient(135deg, #ff9800 0%, #18723F 100%);
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
  animation: iconBounceHover 0.6s ease-in-out;
}

.food-category-icon::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.2), rgba(24, 114, 63, 0.2));
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
  animation: iconGlow 2s ease-in-out infinite;
}

.food-category-card:hover .food-category-icon::before,
.food-category-card:active .food-category-icon::before {
  opacity: 0.5;
  animation: iconGlowActive 1s ease-in-out infinite;
}

.food-category-icon i {
  font-size: 1.75rem;
  color: #ff9800;
  transition: all 0.3s ease;
  animation: iconPulse 2s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

.food-category-card:hover .food-category-icon i,
.food-category-card:active .food-category-icon i {
  color: #fff;
  transform: scale(1.1);
  animation: iconPulseHover 0.5s ease-in-out;
}

.food-category-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 1rem;
  text-align: center;
}

.food-category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.food-category-list li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0;
  font-size: 0.95rem;
  color: #444;
}

.food-category-list li i {
  color: #18723F;
  font-size: 1rem;
  flex-shrink: 0;
  animation: checkPulse 2s ease-in-out infinite;
}

.food-notes-block {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
  margin-bottom: 2rem;
  padding: 1.25rem;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.05) 0%, rgba(24, 114, 63, 0.05) 100%);
  border-radius: 16px;
  border: 2px solid rgba(255, 152, 0, 0.2);
}

.food-note-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
}

.food-note-item i {
  color: #ff9800;
  font-size: 1.2rem;
  animation: pinPulse 2s ease-in-out infinite;
}

@keyframes pinPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.food-cta-wrapper {
  text-align: center;
}

.food-cta-button {
  background: linear-gradient(135deg, #ff9800 0%, #18723F 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(255, 152, 0, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.food-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.food-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.food-cta-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(255, 152, 0, 0.4);
}

.food-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.food-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

.food-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Food Section */
@media (max-width: 991px) {
  .food-card {
    padding: 2rem 2rem;
  }

  .food-title {
    font-size: 1.875rem;
  }

  .food-categories-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.875rem;
  }
}

@media (max-width: 768px) {
  .food-section {
    padding: 1.5rem 0 !important;
  }

  .food-card {
    padding: 1.5rem 1.25rem;
    border-radius: 18px;
    margin: 0 0.5rem;
  }

  .food-header {
    margin-bottom: 1.25rem;
  }

  .food-title {
    font-size: 1.5rem;
    line-height: 1.2;
  }

  .food-icon-wrapper {
    width: 56px;
    height: 56px;
    margin-bottom: 0.75rem;
  }

  .food-icon-wrapper i {
    font-size: 1.6rem;
  }

  .food-intro-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
  }

  .food-intro {
    font-size: 1rem;
    line-height: 1.4;
  }

  .food-categories-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.625rem;
    margin-bottom: 1.25rem;
  }

  .food-category-card {
    padding: 1rem 0.75rem;
    border-radius: 12px;
  }

  .food-category-icon {
    width: 44px;
    height: 44px;
    margin-bottom: 0.75rem;
  }

  .food-category-icon i {
    font-size: 1.4rem;
  }

  .food-category-title {
    font-size: 0.95rem;
    margin-bottom: 0.75rem;
  }

  .food-category-list li {
    font-size: 0.85rem;
    padding: 0.35rem 0;
  }

  .food-notes-block {
    padding: 0.875rem;
    margin-bottom: 1.25rem;
    gap: 1rem;
  }

  .food-note-item {
    font-size: 0.9rem;
  }

  .food-cta-button {
    font-size: 1rem;
    padding: 0.875rem 2rem;
    width: 100%;
    max-width: 100%;
    border-radius: 35px;
  }

  .food-cta-icon {
    font-size: 1.25rem;
  }
}

@media (max-width: 576px) {
  .food-section {
    padding: 1.25rem 0 !important;
  }

  .food-card {
    padding: 1.25rem 0.875rem;
    border-radius: 16px;
    margin: 0 0.75rem;
  }

  .food-header {
    margin-bottom: 1rem;
  }

  .food-title {
    font-size: 1.375rem;
    line-height: 1.25;
  }

  .food-icon-wrapper {
    width: 48px;
    height: 48px;
    margin-bottom: 0.625rem;
  }

  .food-icon-wrapper i {
    font-size: 1.4rem;
  }

  .food-intro-block {
    padding: 0.75rem 0.875rem;
    margin-bottom: 1rem;
    border-radius: 12px;
    border-left-width: 3px;
  }

  .food-intro {
    font-size: 0.95rem;
    line-height: 1.3;
  }

  .food-categories-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.5rem;
    margin-bottom: 1rem;
  }

  .food-category-card {
    padding: 0.875rem 0.75rem;
    border-radius: 12px;
  }

  .food-category-icon {
    width: 40px;
    height: 40px;
    margin-bottom: 0.625rem;
  }

  .food-category-icon i {
    font-size: 1.2rem;
  }

  .food-category-title {
    font-size: 0.95rem;
    margin-bottom: 0.625rem;
  }

  .food-category-list li {
    font-size: 0.8rem;
    padding: 0.3rem 0;
  }

  .food-notes-block {
    padding: 0.75rem;
    margin-bottom: 1rem;
    flex-direction: column;
    gap: 0.625rem;
    text-align: center;
  }

  .food-note-item {
    font-size: 0.85rem;
    justify-content: center;
  }

  .food-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.75rem;
    border-radius: 30px;
    gap: 0.5rem;
  }

  .food-cta-icon {
    font-size: 1.15rem;
  }

  .food-cta-text {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .food-section {
    padding: 1rem 0 !important;
  }

  .food-card {
    padding: 1rem 0.75rem;
    margin: 0 0.5rem;
  }

  .food-header {
    margin-bottom: 0.875rem;
  }

  .food-title {
    font-size: 1.25rem;
  }

  .food-icon-wrapper {
    width: 44px;
    height: 44px;
    margin-bottom: 0.5rem;
  }

  .food-icon-wrapper i {
    font-size: 1.3rem;
  }

  .food-intro-block {
    padding: 0.625rem 0.75rem;
    margin-bottom: 0.875rem;
  }

  .food-intro {
    font-size: 0.9rem;
  }

  .food-categories-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.5rem;
    margin-bottom: 0.875rem;
  }

  .food-category-card {
    padding: 0.75rem 0.625rem;
  }

  .food-category-icon {
    width: 36px;
    height: 36px;
    margin-bottom: 0.5rem;
  }

  .food-category-icon i {
    font-size: 1.1rem;
  }

  .food-category-title {
    font-size: 0.9rem;
    margin-bottom: 0.5rem;
  }

  .food-category-list li {
    font-size: 0.75rem;
    padding: 0.25rem 0;
  }

  .food-notes-block {
    padding: 0.625rem 0.75rem;
    margin-bottom: 0.875rem;
    gap: 0.5rem;
  }

  .food-note-item {
    font-size: 0.8rem;
  }

  .food-note-item i {
    font-size: 1rem;
  }

  .food-cta-button {
    font-size: 0.85rem;
    padding: 0.7rem 1.5rem;
  }

  .food-cta-icon {
    font-size: 1.1rem;
  }

  .food-cta-text {
    font-size: 0.85rem;
  }
}

/* Tickets Section - Extraordinary Design */
.tickets-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.tickets-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 50%, #ffc107 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.tickets-card {
  background: #fff;
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(24, 114, 63, 0.1);
  border: 2px solid transparent;
  background-image: linear-gradient(#fff, #fff), 
                    linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1), rgba(255, 193, 7, 0.1));
  background-origin: border-box;
  background-clip: padding-box, border-box;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardFadeIn 0.6s ease-out;
}

.tickets-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(24, 114, 63, 0.05) 0%, transparent 70%);
  animation: pulseGlow 4s ease-in-out infinite;
  pointer-events: none;
}

.tickets-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(24, 114, 63, 0.2);
}

.tickets-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.tickets-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 50%, #ffc107 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  position: relative;
}

.tickets-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 50%, #ffc107 100%);
  opacity: 0.3;
  z-index: -1;
  animation: iconPulse 2s ease-in-out infinite;
}

.tickets-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.tickets-title {
  font-size: 2.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 50%, #ffc107 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.tickets-content {
  position: relative;
  z-index: 1;
}

.tickets-intro-block {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border-radius: 16px;
  border-left: 4px solid #18723F;
}

.tickets-intro {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin: 0;
  line-height: 1.5;
}

.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.ticket-card {
  position: relative;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  border-radius: 20px;
  padding: 2rem 1.5rem;
  border: 3px solid rgba(24, 114, 63, 0.2);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;
  opacity: 0;
  animation: ticketSlideIn 0.6s ease-out forwards;
  box-shadow: 0 4px 20px rgba(24, 114, 63, 0.1);
}

@keyframes ticketSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.ticket-card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(24, 114, 63, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.5s ease;
  pointer-events: none;
}

.ticket-card:hover .ticket-card-glow {
  opacity: 1;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.6;
  }
}

.ticket-card-shine {
  position: absolute;
  top: -100%;
  left: -100%;
  width: 300%;
  height: 300%;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  transition: all 0.6s ease;
  pointer-events: none;
}

.ticket-card:hover .ticket-card-shine {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) rotate(45deg);
}

.ticket-card:hover {
  transform: translateY(-12px) scale(1.05);
  border-color: #18723F;
  box-shadow: 0 15px 40px rgba(24, 114, 63, 0.25);
  background: linear-gradient(135deg, #fff 0%, rgba(24, 114, 63, 0.02) 100%);
}

.ticket-card:active {
  transform: translateY(-6px) scale(1.02);
}

.ticket-card-content {
  position: relative;
  z-index: 2;
  text-align: center;
}

.ticket-price-wrapper {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 0.25rem;
  margin-bottom: 0.5rem;
}

.ticket-price {
  font-size: 2.5rem;
  font-weight: 900;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  letter-spacing: -1px;
}

.ticket-currency {
  font-size: 1.5rem;
  font-weight: 700;
  color: #18723F;
  opacity: 0.8;
}

.ticket-vip {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
  border-color: #ffc107;
  box-shadow: 0 8px 30px rgba(255, 193, 7, 0.3);
  position: relative;
  overflow: visible;
}

.ticket-vip::before {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: 20px;
  background: linear-gradient(135deg, #ffc107, #ff9800, #ffc107);
  background-size: 200% 200%;
  animation: vipBorderGlow 3s ease infinite;
  z-index: -1;
  opacity: 0.5;
}

@keyframes vipBorderGlow {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.ticket-vip .ticket-card-glow {
  background: radial-gradient(circle, rgba(255, 193, 7, 0.2) 0%, transparent 70%);
}

.ticket-vip:hover {
  border-color: #ff9800;
  box-shadow: 0 20px 50px rgba(255, 193, 7, 0.4);
  transform: translateY(-15px) scale(1.08);
}

.ticket-vip .ticket-price {
  background: linear-gradient(135deg, #fff 0%, #ffc107 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.ticket-vip .ticket-currency {
  color: #fff;
  opacity: 0.9;
}

.ticket-vip-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  margin-top: 0.75rem;
  font-weight: 700;
  font-size: 0.9rem;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  animation: vipBadgePulse 2s ease-in-out infinite;
}

@keyframes vipBadgePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
  }
}

.ticket-vip-badge i {
  font-size: 1rem;
  animation: starRotate 3s ease-in-out infinite;
}

@keyframes starRotate {
  0%, 100% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.2);
  }
}

.tickets-note-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.25rem 1.5rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(111, 66, 193, 0.05) 100%);
  border-radius: 16px;
  border: 2px solid rgba(24, 114, 63, 0.2);
}

.tickets-note-block i {
  font-size: 1.5rem;
  color: #18723F;
  animation: infoPulse 2s ease-in-out infinite;
}

@keyframes infoPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.tickets-note-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.tickets-cta-wrapper {
  text-align: center;
}

.tickets-cta-button {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 50%, #ffc107 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.tickets-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.tickets-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.tickets-cta-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(24, 114, 63, 0.4);
}

.tickets-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.tickets-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

.tickets-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Tickets Section */
@media (max-width: 991px) {
  .tickets-card {
    padding: 2rem 2rem;
  }

  .tickets-title {
    font-size: 1.875rem;
  }

  .tickets-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.25rem;
  }
}

@media (max-width: 768px) {
  .tickets-section {
    padding: 1.5rem 0 !important;
  }

  .tickets-card {
    padding: 1.5rem 1.25rem;
    border-radius: 20px;
    margin: 0 0.5rem;
  }

  .tickets-header {
    margin-bottom: 1.5rem;
  }

  .tickets-title {
    font-size: 1.625rem;
    line-height: 1.2;
  }

  .tickets-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 0.875rem;
  }

  .tickets-icon-wrapper i {
    font-size: 1.75rem;
  }

  .tickets-intro-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
  }

  .tickets-intro {
    font-size: 1.1rem;
    line-height: 1.4;
  }

  .tickets-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
    margin-bottom: 1.5rem;
  }

  .ticket-card {
    padding: 1.5rem 1rem;
    border-radius: 16px;
    border-width: 2px;
  }

  .ticket-price {
    font-size: 2rem;
  }

  .ticket-currency {
    font-size: 1.25rem;
  }

  .ticket-vip-badge {
    font-size: 0.85rem;
    padding: 0.4rem 0.875rem;
  }

  .tickets-note-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    gap: 0.625rem;
  }

  .tickets-note-block i {
    font-size: 1.3rem;
  }

  .tickets-note-text {
    font-size: 1rem;
  }

  .tickets-cta-button {
    font-size: 1.05rem;
    padding: 1rem 2.5rem;
    width: 100%;
    max-width: 100%;
    border-radius: 40px;
  }

  .tickets-cta-icon {
    font-size: 1.35rem;
  }
}

@media (max-width: 576px) {
  .tickets-section {
    padding: 1.25rem 0 !important;
  }

  .tickets-card {
    padding: 1.25rem 0.875rem;
    border-radius: 18px;
    margin: 0 0.75rem;
  }

  .tickets-header {
    margin-bottom: 1.25rem;
  }

  .tickets-title {
    font-size: 1.5rem;
    line-height: 1.25;
  }

  .tickets-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.75rem;
  }

  .tickets-icon-wrapper i {
    font-size: 1.5rem;
  }

  .tickets-intro-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    border-left-width: 3px;
  }

  .tickets-intro {
    font-size: 1rem;
    line-height: 1.4;
  }

  .tickets-grid {
    grid-template-columns: 1fr;
    gap: 0.875rem;
    margin-bottom: 1.25rem;
  }

  .ticket-card {
    padding: 1.25rem 1rem;
    border-radius: 14px;
  }

  .ticket-price {
    font-size: 1.75rem;
  }

  .ticket-currency {
    font-size: 1.1rem;
  }

  .ticket-vip-badge {
    font-size: 0.8rem;
    padding: 0.35rem 0.75rem;
    margin-top: 0.5rem;
  }

  .tickets-note-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
  }

  .tickets-note-block i {
    font-size: 1.2rem;
  }

  .tickets-note-text {
    font-size: 0.95rem;
  }

  .tickets-cta-button {
    font-size: 0.95rem;
    padding: 0.875rem 2rem;
    border-radius: 35px;
    gap: 0.625rem;
  }

  .tickets-cta-icon {
    font-size: 1.25rem;
  }

  .tickets-cta-text {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .tickets-section {
    padding: 1rem 0 !important;
  }

  .tickets-card {
    padding: 1rem 0.75rem;
    margin: 0 0.5rem;
  }

  .tickets-header {
    margin-bottom: 1rem;
  }

  .tickets-title {
    font-size: 1.375rem;
  }

  .tickets-icon-wrapper {
    width: 48px;
    height: 48px;
    margin-bottom: 0.625rem;
  }

  .tickets-icon-wrapper i {
    font-size: 1.35rem;
  }

  .tickets-intro-block {
    padding: 0.75rem 0.875rem;
    margin-bottom: 1rem;
  }

  .tickets-intro {
    font-size: 0.95rem;
  }

  .tickets-grid {
    gap: 0.75rem;
    margin-bottom: 1rem;
  }

  .ticket-card {
    padding: 1rem 0.875rem;
    border-radius: 12px;
  }

  .ticket-price {
    font-size: 1.5rem;
  }

  .ticket-currency {
    font-size: 1rem;
  }

  .ticket-vip-badge {
    font-size: 0.75rem;
    padding: 0.3rem 0.625rem;
  }

  .tickets-note-block {
    padding: 0.75rem 0.875rem;
    margin-bottom: 1rem;
  }

  .tickets-note-text {
    font-size: 0.9rem;
  }

  .tickets-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.75rem;
  }

  .tickets-cta-icon {
    font-size: 1.15rem;
  }

  .tickets-cta-text {
    font-size: 0.9rem;
  }
}

/* Urgency Section - Attention Grabbing Design */
.urgency-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.urgency-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #ffc107 50%, #18723F 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.urgency-card {
  background: #fff;
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(24, 114, 63, 0.1);
  border: 2px solid transparent;
  background-image: linear-gradient(#fff, #fff), 
                    linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(255, 193, 7, 0.1));
  background-origin: border-box;
  background-clip: padding-box, border-box;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.urgency-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(24, 114, 63, 0.05) 0%, transparent 70%);
  animation: pulseGlow 4s ease-in-out infinite;
  pointer-events: none;
}

.urgency-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(24, 114, 63, 0.2);
}

.urgency-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.urgency-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #18723F 0%, #ffc107 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  animation: iconFloat 3s ease-in-out infinite;
  position: relative;
}

.urgency-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  background: linear-gradient(135deg, #18723F 0%, #ffc107 100%);
  opacity: 0.3;
  z-index: -1;
  animation: iconPulse 2s ease-in-out infinite;
}

.urgency-icon-wrapper i {
  font-size: 2rem;
  color: #fff;
}

.urgency-title {
  font-size: 2.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, #18723F 0%, #ffc107 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.urgency-content {
  position: relative;
  z-index: 1;
}

.urgency-intro-block {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(255, 193, 7, 0.05) 100%);
  border-radius: 16px;
  border-left: 4px solid #18723F;
}

.urgency-intro {
  font-size: 1.25rem;
  font-weight: 700;
  color: #18723F;
  margin: 0;
  line-height: 1.5;
}

.urgency-features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.urgency-feature-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: #fff;
  border-radius: 14px;
  border: 2px solid rgba(24, 114, 63, 0.15);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  opacity: 0;
  animation: featureSlideIn 0.6s ease-out forwards;
}

.urgency-feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(24, 114, 63, 0.1), transparent);
  transition: left 0.5s ease;
}

.urgency-feature-item:hover::before,
.urgency-feature-item:active::before {
  left: 100%;
}

.urgency-feature-item:hover,
.urgency-feature-item:active {
  transform: translateX(8px) translateY(-4px);
  border-color: #18723F;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.15);
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(255, 193, 7, 0.05) 100%);
}

.urgency-feature-item:active {
  transform: translateX(4px) translateY(-2px);
}

.urgency-feature-icon {
  width: 50px;
  height: 50px;
  min-width: 50px;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1) 0%, rgba(255, 193, 7, 0.1) 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  animation: iconBounce 3s ease-in-out infinite;
}

.urgency-feature-item:hover .urgency-feature-icon,
.urgency-feature-item:active .urgency-feature-icon {
  background: linear-gradient(135deg, #18723F 0%, #ffc107 100%);
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.3);
  animation: iconBounceHover 0.6s ease-in-out;
}

.urgency-feature-icon::before {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.2), rgba(255, 193, 7, 0.2));
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.urgency-feature-item:hover .urgency-feature-icon::before,
.urgency-feature-item:active .urgency-feature-icon::before {
  opacity: 0.5;
  animation: iconGlowActive 1s ease-in-out infinite;
}

.urgency-feature-icon i {
  font-size: 1.5rem;
  color: #18723F;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  animation: iconPulse 2s ease-in-out infinite;
}

.urgency-feature-item:hover .urgency-feature-icon i,
.urgency-feature-item:active .urgency-feature-icon i {
  color: #fff;
  transform: scale(1.1);
}

.urgency-feature-content {
  flex: 1;
}

.urgency-feature-text {
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.urgency-warning-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.5rem 1.5rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, #18723F 0%, #ffc107 100%);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.25);
  position: relative;
  overflow: hidden;
}

.urgency-warning-block::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: warningShine 3s ease-in-out infinite;
}

@keyframes warningShine {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.urgency-warning-icon {
  font-size: 2rem;
  color: #fff;
  animation: iconFloat 3s ease-in-out infinite;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
  flex-shrink: 0;
}

.urgency-warning-text {
  font-size: 1.5rem;
  font-weight: 800;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.urgency-cta-wrapper {
  text-align: center;
}

.urgency-cta-button {
  background: linear-gradient(135deg, #18723F 0%, #ffc107 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.urgency-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.urgency-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.urgency-cta-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(24, 114, 63, 0.4);
}

.urgency-cta-button:active {
  transform: translateY(-2px) scale(1.02);
}

.urgency-cta-icon {
  font-size: 1.35rem;
  position: relative;
  z-index: 1;
  animation: pointRight 1s ease-in-out infinite;
}

.urgency-cta-text {
  position: relative;
  z-index: 1;
}

/* Responsive Design for Urgency Section */
@media (max-width: 991px) {
  .urgency-card {
    padding: 2rem 2rem;
  }

  .urgency-title {
    font-size: 1.875rem;
  }

  .urgency-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.875rem;
  }
}

@media (max-width: 768px) {
  .urgency-section {
    padding: 1.5rem 0 !important;
  }

  .urgency-card {
    padding: 1.5rem 1.25rem;
    border-radius: 20px;
    margin: 0 0.5rem;
  }

  .urgency-header {
    margin-bottom: 1.5rem;
  }

  .urgency-title {
    font-size: 1.625rem;
    line-height: 1.2;
  }

  .urgency-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 0.875rem;
  }

  .urgency-icon-wrapper i {
    font-size: 1.75rem;
  }

  .urgency-intro-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
  }

  .urgency-intro {
    font-size: 1.1rem;
    line-height: 1.4;
  }

  .urgency-features-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .urgency-feature-item {
    padding: 1rem 0.75rem;
    gap: 0.625rem;
    border-radius: 12px;
    flex-direction: column;
    text-align: center;
    min-height: 100px;
    justify-content: center;
  }

  .urgency-feature-item:hover,
  .urgency-feature-item:active {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(220, 53, 69, 0.2);
  }

  .urgency-feature-item:active {
    transform: translateY(-2px);
  }

  .urgency-feature-icon {
    width: 48px;
    height: 48px;
    min-width: 48px;
    border-radius: 12px;
    margin: 0 auto;
  }

  .urgency-feature-icon i {
    font-size: 1.4rem;
  }

  .urgency-feature-content {
    width: 100%;
  }

  .urgency-feature-text {
    font-size: 0.95rem;
    line-height: 1.3;
    display: block;
  }

  .urgency-warning-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1.5rem;
    border-radius: 14px;
    gap: 0.75rem;
  }

  .urgency-warning-icon {
    font-size: 1.5rem;
  }

  .urgency-warning-text {
    font-size: 1.2rem;
    line-height: 1.3;
  }

  .urgency-cta-button {
    font-size: 1.05rem;
    padding: 1rem 2.5rem;
    width: 100%;
    max-width: 100%;
    border-radius: 40px;
  }

  .urgency-cta-icon {
    font-size: 1.35rem;
  }
}

@media (max-width: 576px) {
  .urgency-section {
    padding: 1.25rem 0 !important;
  }

  .urgency-card {
    padding: 1.25rem 0.875rem;
    border-radius: 18px;
    margin: 0 0.75rem;
  }

  .urgency-header {
    margin-bottom: 1.25rem;
  }

  .urgency-title {
    font-size: 1.5rem;
    line-height: 1.25;
  }

  .urgency-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.75rem;
  }

  .urgency-icon-wrapper i {
    font-size: 1.5rem;
  }

  .urgency-intro-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    border-left-width: 3px;
  }

  .urgency-intro {
    font-size: 1rem;
    line-height: 1.4;
  }

  .urgency-features-grid {
    gap: 0.625rem;
    margin-bottom: 1.25rem;
  }

  .urgency-feature-item {
    padding: 0.875rem 0.5rem;
    gap: 0.5rem;
    min-height: 95px;
  }

  .urgency-feature-icon {
    width: 44px;
    height: 44px;
    min-width: 44px;
    border-radius: 12px;
  }

  .urgency-feature-icon i {
    font-size: 1.3rem;
  }

  .urgency-feature-text {
    font-size: 0.85rem;
    line-height: 1.25;
  }

  .urgency-warning-block {
    padding: 0.875rem 1rem;
    margin-bottom: 1.25rem;
    border-radius: 12px;
    gap: 0.625rem;
    flex-direction: column;
    text-align: center;
  }

  .urgency-warning-icon {
    font-size: 1.35rem;
  }

  .urgency-warning-text {
    font-size: 1.1rem;
    line-height: 1.3;
  }

  .urgency-cta-button {
    font-size: 0.95rem;
    padding: 0.875rem 2rem;
    border-radius: 35px;
    gap: 0.625rem;
  }

  .urgency-cta-icon {
    font-size: 1.25rem;
  }

  .urgency-cta-text {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .urgency-section {
    padding: 1rem 0 !important;
  }

  .urgency-card {
    padding: 1rem 0.75rem;
    margin: 0 0.5rem;
  }

  .urgency-header {
    margin-bottom: 1rem;
  }

  .urgency-title {
    font-size: 1.375rem;
  }

  .urgency-icon-wrapper {
    width: 48px;
    height: 48px;
    margin-bottom: 0.625rem;
  }

  .urgency-icon-wrapper i {
    font-size: 1.35rem;
  }

  .urgency-intro-block {
    padding: 0.75rem 0.875rem;
    margin-bottom: 1rem;
  }

  .urgency-intro {
    font-size: 0.95rem;
  }

  .urgency-features-grid {
    gap: 0.5rem;
    margin-bottom: 1rem;
  }

  .urgency-feature-item {
    padding: 0.75rem 0.5rem;
    min-height: 90px;
  }

  .urgency-feature-icon {
    width: 40px;
    height: 40px;
    min-width: 40px;
  }

  .urgency-feature-icon i {
    font-size: 1.2rem;
  }

  .urgency-feature-text {
    font-size: 0.8rem;
  }

  .urgency-warning-block {
    padding: 0.75rem 0.875rem;
    margin-bottom: 1rem;
  }

  .urgency-warning-text {
    font-size: 1rem;
  }

  .urgency-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.75rem;
  }

  .urgency-cta-icon {
    font-size: 1.15rem;
  }

  .urgency-cta-text {
    font-size: 0.9rem;
  }
}

/* Info Sections Styles */
.info-section {
  background: #fff;
}

.info-card {
  background: #fff;
  border-radius: 24px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.info-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.info-header {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 3px solid #18723F;
}

.info-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #18723F;
  margin: 0;
}

.info-content {
  color: #333;
}

.info-question {
  font-size: 1.3rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 1rem;
}

.info-answer {
  font-size: 1.15rem;
  margin-bottom: 1.5rem;
  color: #555;
}

.info-intro {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #333;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 1.5rem 0;
}

.info-list li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 0;
  font-size: 1.1rem;
  color: #444;
}

.info-list li i {
  color: #18723F;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.info-list-dots {
  list-style: none;
  padding: 0;
  margin: 1.5rem 0;
}

.info-list-dots li {
  padding: 0.75rem 0;
  padding-left: 1.5rem;
  font-size: 1.1rem;
  color: #444;
  position: relative;
}

.info-list-dots li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #18723F;
  font-size: 1.5rem;
  font-weight: bold;
}

.info-highlight {
  font-size: 1.2rem;
  font-weight: 700;
  color: #18723F;
  margin-top: 1.5rem;
  padding: 1rem;
  background: rgba(24, 114, 63, 0.1);
  border-radius: 12px;
  border-left: 4px solid #18723F;
}

.info-highlight-big {
  font-size: 1.5rem;
  font-weight: 800;
  color: #18723F;
  text-align: center;
  margin-bottom: 2rem;
}

.info-note {
  margin-top: 1.5rem;
  padding: 1rem;
  background: rgba(24, 114, 63, 0.05);
  border-radius: 12px;
  font-size: 1.1rem;
}

.info-notes {
  margin-top: 1.5rem;
}

.info-notes p {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: #555;
}

.info-notes p i {
  color: #18723F;
}

/* Event Grid */
.event-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin: 2rem 0;
}

.event-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: rgba(24, 114, 63, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(24, 114, 63, 0.1);
  transition: all 0.3s ease;
}

.event-item:hover {
  background: rgba(24, 114, 63, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.15);
}

.event-item i {
  font-size: 1.5rem;
  color: #18723F;
  flex-shrink: 0;
}

.event-item span {
  font-size: 1.05rem;
  font-weight: 500;
  color: #333;
}

/* Artist Info */
.artist-info {
  margin-bottom: 2rem;
}

.artist-name {
  font-size: 1.8rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 1.5rem;
}

.guests-info {
  padding-top: 2rem;
  border-top: 2px solid rgba(24, 114, 63, 0.2);
}

.guests-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 1rem;
}

.guests-list {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.guest-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.3rem;
  font-weight: 600;
  color: #18723F;
}

.guest-item i {
  font-size: 1.5rem;
  color: #ffc107;
}

/* Menu Grid */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin: 2rem 0;
}

.menu-category {
  padding: 1.5rem;
  background: rgba(24, 114, 63, 0.05);
  border-radius: 16px;
  border: 2px solid rgba(24, 114, 63, 0.1);
}

.menu-category-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #18723F;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid rgba(24, 114, 63, 0.2);
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-list li {
  padding: 0.5rem 0;
  font-size: 1.1rem;
  color: #444;
  position: relative;
  padding-left: 1.5rem;
}

.menu-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #18723F;
  font-weight: bold;
}

/* Tickets Grid */
.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1.5rem;
  margin: 2rem 0;
}

.ticket-category {
  padding: 2rem 1.5rem;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1) 0%, rgba(111, 66, 193, 0.1) 100%);
  border-radius: 16px;
  border: 2px solid rgba(24, 114, 63, 0.2);
  text-align: center;
  transition: all 0.3s ease;
}

.ticket-category:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.2);
  border-color: #18723F;
}

.ticket-vip {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
  border-color: #ff9800;
}

.ticket-price {
  font-size: 2rem;
  font-weight: 800;
  color: #18723F;
  display: block;
}

.ticket-vip .ticket-price {
  color: #fff;
}

.ticket-label {
  display: block;
  margin-top: 0.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 1px;
}


/* Organizer Section - Elegant Design */
.organizer-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  position: relative;
  overflow: hidden;
}

.organizer-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.organizer-card {
  background: #fff;
  border-radius: 24px;
  padding: 3rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(24, 114, 63, 0.1);
  position: relative;
  overflow: hidden;
  transition: all 0.4s ease;
}

.organizer-card:hover {
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.organizer-header {
  text-align: center;
  margin-bottom: 1rem;
  position: relative;
  z-index: 1;
}

.organizer-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.2);
  position: relative;
}

.organizer-icon-wrapper i {
  font-size: 2.25rem;
  color: #fff;
}

.organizer-title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.organizer-content {
  position: relative;
  z-index: 1;
}

.organizer-logo-wrapper {
  margin: 0 auto 1rem;
  width: 140px;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.organizer-logo {
  width: 100%;
  height: 100%;
  object-fit: contain;
  filter: drop-shadow(0 4px 12px rgba(24, 114, 63, 0.15));
  transition: all 0.4s ease;
}

.organizer-logo-wrapper:hover .organizer-logo {
  filter: drop-shadow(0 8px 20px rgba(24, 114, 63, 0.25));
  transform: scale(1.05);
}

.organizer-description-block {
  margin-bottom: 1.5rem;
  margin-top: 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.03) 0%, rgba(111, 66, 193, 0.03) 100%);
  border-radius: 20px;
  border-left: 4px solid #18723F;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.organizer-description {
  font-size: 1.15rem;
  line-height: 1.8;
  color: #444;
  margin: 0;
  text-align: center;
}

.organizer-highlight-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: 1.25rem 2rem;
  margin-bottom: 1.5rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.2);
  position: relative;
  overflow: hidden;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.organizer-highlight-icon {
  font-size: 1.75rem;
  color: #fff;
  flex-shrink: 0;
}

.organizer-highlight-text {
  font-size: 1.2rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-align: center;
}

.organizer-cta-wrapper {
  text-align: center;
}

.organizer-cta-button {
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border: none;
  color: #fff;
  font-size: 1.1rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.organizer-cta-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(24, 114, 63, 0.4);
}

.organizer-cta-button:active {
  transform: translateY(-1px);
}

.organizer-cta-icon {
  font-size: 1.35rem;
}

/* Videos Section - Elegant Design */
.videos-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.videos-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18723F 0%, #6f42c1 100%);
  background-size: 200% 100%;
  animation: gradientShift 3s ease infinite;
}

.videos-card {
  background: #fff;
  border-radius: 24px;
  padding: 3rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(24, 114, 63, 0.1);
  position: relative;
  overflow: hidden;
  transition: all 0.4s ease;
}

.videos-card:hover {
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.videos-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.videos-icon-wrapper {
  width: 70px;
  height: 70px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(24, 114, 63, 0.2);
  position: relative;
  animation: iconFloat 3s ease-in-out infinite;
}

.videos-icon-wrapper i {
  font-size: 2.25rem;
  color: #fff;
}

.videos-title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #18723F 0%, #6f42c1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -0.5px;
}

.videos-content {
  position: relative;
  z-index: 1;
}

.videos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  justify-items: center;
  align-items: start;
  width: 100%;
}

.video-item {
  width: 100%;
  max-width: 540px;
  opacity: 0;
  animation: cardFadeIn 0.6s ease-out forwards;
  transition: transform 0.3s ease;
  display: block !important;
  visibility: visible !important;
  min-height: 200px;
}

.video-item:hover {
  transform: scale(1.02);
}

@media (max-width: 768px) {
  .videos-section {
    padding: 2rem 0;
  }

  .videos-card {
    padding: 2rem 1.5rem;
  }

  .videos-title {
    font-size: 2rem;
  }

  .videos-icon-wrapper {
    width: 60px;
    height: 60px;
  }

  .videos-icon-wrapper i {
    font-size: 1.75rem;
  }

  .videos-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .video-item {
    max-width: 100%;
  }
}

@media (max-width: 576px) {
  .videos-card {
    padding: 1.5rem 1rem;
  }

  .videos-title {
    font-size: 1.75rem;
  }

  .videos-icon-wrapper {
    width: 50px;
    height: 50px;
  }

  .videos-icon-wrapper i {
    font-size: 1.5rem;
  }

  .videos-grid {
    gap: 1rem;
  }
}

.organizer-cta-text {
  font-weight: 700;
}

/* Responsive Design for Organizer Section */
@media (max-width: 991px) {
  .organizer-card {
    padding: 1.75rem 1.5rem;
  }

  .organizer-header {
    margin-bottom: 0.875rem;
  }

  .organizer-logo-wrapper {
    width: 120px;
    height: 120px;
    margin-bottom: 0.875rem;
  }

  .organizer-description-block {
    margin-bottom: 1.25rem;
    padding: 1.25rem;
  }

  .organizer-highlight-block {
    margin-bottom: 1.25rem;
    padding: 1rem 1.5rem;
  }
}

@media (max-width: 768px) {
  .organizer-section {
    padding: 1.25rem 0 !important;
  }

  .organizer-card {
    padding: 1.5rem 1.25rem;
    border-radius: 20px;
  }

  .organizer-header {
    margin-bottom: 0.75rem;
  }

  .organizer-icon-wrapper {
    width: 60px;
    height: 60px;
    margin-bottom: 0.75rem;
  }

  .organizer-icon-wrapper i {
    font-size: 1.75rem;
  }

  .organizer-title {
    font-size: 1.75rem;
  }

  .organizer-logo-wrapper {
    width: 100px;
    height: 100px;
    margin-bottom: 0.75rem;
  }

  .organizer-description-block {
    padding: 1.25rem;
    margin-bottom: 1.25rem;
  }

  .organizer-description {
    font-size: 1.05rem;
    line-height: 1.7;
  }

  .organizer-highlight-block {
    padding: 1rem 1.5rem;
    margin-bottom: 1.25rem;
    gap: 0.75rem;
  }

  .organizer-highlight-icon {
    font-size: 1.35rem;
  }

  .organizer-highlight-text {
    font-size: 1rem;
  }

  .organizer-cta-button {
    font-size: 1rem;
    padding: 0.875rem 2rem;
    width: 100%;
    max-width: 400px;
  }
}

@media (max-width: 576px) {
  .organizer-section {
    padding: 1rem 0 !important;
  }

  .organizer-card {
    padding: 1.25rem 1rem;
  }

  .organizer-header {
    margin-bottom: 0.625rem;
  }

  .organizer-icon-wrapper {
    width: 52px;
    height: 52px;
    margin-bottom: 0.625rem;
  }

  .organizer-icon-wrapper i {
    font-size: 1.5rem;
  }

  .organizer-title {
    font-size: 1.5rem;
  }

  .organizer-logo-wrapper {
    width: 80px;
    height: 80px;
    margin-bottom: 0.75rem;
  }

  .organizer-description-block {
    padding: 1rem;
    margin-bottom: 1rem;
  }

  .organizer-description {
    font-size: 0.95rem;
    line-height: 1.6;
  }

  .organizer-highlight-block {
    padding: 1rem 1.25rem;
    margin-bottom: 1rem;
    gap: 0.625rem;
    flex-direction: column;
  }

  .organizer-highlight-icon {
    font-size: 1.25rem;
  }

  .organizer-highlight-text {
    font-size: 0.95rem;
  }

  .organizer-cta-button {
    font-size: 0.9rem;
    padding: 0.75rem 1.5rem;
  }
}

@media (max-width: 480px) {
  .organizer-section {
    padding: 0.875rem 0 !important;
  }

  .organizer-card {
    padding: 1rem 0.875rem;
  }

  .organizer-header {
    margin-bottom: 0.5rem;
  }

  .organizer-icon-wrapper {
    width: 48px;
    height: 48px;
    margin-bottom: 0.5rem;
  }

  .organizer-icon-wrapper i {
    font-size: 1.35rem;
  }

  .organizer-title {
    font-size: 1.375rem;
  }

  .organizer-logo-wrapper {
    width: 70px;
    height: 70px;
    margin-bottom: 0.625rem;
  }

  .organizer-description-block {
    padding: 0.875rem;
    margin-bottom: 0.875rem;
  }

  .organizer-description {
    font-size: 0.9rem;
  }

  .organizer-highlight-block {
    padding: 0.875rem 1rem;
    margin-bottom: 0.875rem;
  }

  .organizer-highlight-text {
    font-size: 0.9rem;
  }

  .organizer-cta-button {
    font-size: 0.85rem;
    padding: 0.7rem 1.25rem;
  }
}

/* Final Section */
.final-section {
  position: relative;
  background: linear-gradient(135deg, #18723F 0%, #145a32 50%, #0f4a26 100%);
  color: #fff;
  overflow: hidden;
}

.final-background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
  pointer-events: none;
  animation: backgroundPulse 8s ease-in-out infinite;
}

@keyframes backgroundPulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.final-content {
  position: relative;
  z-index: 1;
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* Title Section - Highlight Words */
.final-title-wrapper {
  text-align: center;
  margin-bottom: 4rem;
}

.final-main-title {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 1.5rem 2.5rem;
  margin-bottom: 2rem;
  position: relative;
}

.final-word-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  opacity: 0;
  transform: translateY(30px);
  animation: wordSlideUp 0.8s ease-out forwards;
}

@keyframes wordSlideUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.final-word-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.final-word-item:hover .final-word-icon {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1) rotate(360deg);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.3);
}

.final-word-icon i {
  font-size: 1.75rem;
  color: #fff;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.3));
}

.final-word-text {
  font-size: 4.5rem;
  font-weight: 900;
  line-height: 1;
  margin: 0;
  color: #fff;
  text-shadow: 
    0 4px 25px rgba(0, 0, 0, 0.5),
    0 0 50px rgba(255, 255, 255, 0.15);
  letter-spacing: -2px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 50%, #ffffff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
  position: relative;
  white-space: nowrap;
}

.final-word-item:hover .final-word-text {
  transform: scale(1.05);
  text-shadow: 
    0 6px 35px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(255, 255, 255, 0.25);
  filter: brightness(1.1);
}

/* Специальные стили для "Родная музыка" с музыкальным эффектом */
.final-word-music {
  cursor: pointer;
  position: relative;
}

.final-word-music::before {
  content: '♪';
  position: absolute;
  top: -20px;
  right: -30px;
  font-size: 1.5rem;
  opacity: 0;
  transition: all 0.3s ease;
  animation: musicNoteFloat 2s ease-in-out infinite;
}

.final-word-music:hover::before {
  opacity: 1;
  transform: translateY(-10px);
}

@keyframes musicNoteFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-5px) rotate(10deg);
  }
}

.final-word-music:hover .final-word-icon {
  animation: musicPulse 1s ease-in-out infinite;
  background: rgba(255, 255, 255, 0.35);
}

@keyframes musicPulse {
  0%, 100% {
    transform: scale(1.1);
    box-shadow: 0 8px 25px rgba(255, 255, 255, 0.3);
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 12px 35px rgba(255, 255, 255, 0.5);
  }
}

.final-word-music:hover .final-word-text {
  animation: musicTextGlow 1.5s ease-in-out infinite;
}

@keyframes musicTextGlow {
  0%, 100% {
    text-shadow: 
      0 6px 35px rgba(0, 0, 0, 0.6),
      0 0 60px rgba(255, 255, 255, 0.25);
  }
  50% {
    text-shadow: 
      0 6px 35px rgba(0, 0, 0, 0.6),
      0 0 80px rgba(255, 255, 255, 0.4),
      0 0 100px rgba(255, 255, 255, 0.2);
  }
}

.final-word-divider {
  font-size: 3rem;
  color: rgba(255, 255, 255, 0.4);
  margin: 0 0.5rem;
  font-weight: 300;
  animation: dividerPulse 2s ease-in-out infinite;
  opacity: 0;
  animation: fadeIn 0.8s ease-out 0.6s forwards;
}

@keyframes dividerPulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

.final-subtitle {
  font-size: 1.5rem;
  font-weight: 300;
  opacity: 0.9;
  margin: 0;
  animation: fadeIn 1s ease-out 0.8s forwards;
  opacity: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  letter-spacing: 1px;
}

@keyframes fadeIn {
  to {
    opacity: 0.9;
  }
}

/* Details Cards */
.final-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.final-detail-card {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px) scale(0.95);
  animation: cardSlideIn 0.6s ease-out forwards;
  cursor: default;
}

@keyframes cardSlideIn {
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.final-detail-card:hover {
  background: rgba(255, 255, 255, 0.18);
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.3);
}

.final-detail-icon-wrapper {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
  transition: all 0.3s ease;
}

.final-detail-card:hover .final-detail-icon-wrapper {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1) rotate(5deg);
}

.final-detail-icon-wrapper i {
  font-size: 1.75rem;
  color: #fff;
}

.final-detail-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.final-detail-label {
  font-size: 0.875rem;
  font-weight: 500;
  opacity: 0.85;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.final-detail-value {
  font-size: 1.125rem;
  font-weight: 700;
  color: #fff;
}

/* CTA Button */
.final-cta-wrapper {
  text-align: center;
  animation: fadeIn 1s ease-out 1.2s forwards;
  opacity: 0;
}

.final-cta-button {
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  color: #18723F;
  border: none;
  border-radius: 50px;
  padding: 1.25rem 3rem;
  font-size: 1.25rem;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  position: relative;
  overflow: hidden;
}

.final-cta-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(24, 114, 63, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.final-cta-button:hover::before {
  width: 300px;
  height: 300px;
}

.final-cta-button:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
}

.final-cta-button:active {
  transform: translateY(-1px) scale(1.02);
}

.final-cta-icon {
  font-size: 1.5rem;
  position: relative;
  z-index: 1;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.final-cta-text {
  position: relative;
  z-index: 1;
}

.final-cta-arrow {
  position: relative;
  z-index: 1;
  font-size: 1.5rem;
  transition: transform 0.3s ease;
}

.final-cta-button:hover .final-cta-arrow {
  transform: translateX(5px);
}

.final-cta-hint {
  margin-top: 1rem;
  font-size: 0.9rem;
  opacity: 0.9;
  font-weight: 400;
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .final-content {
    padding: 1.5rem 1rem;
  }

  .final-title-wrapper {
    margin-bottom: 3rem;
  }

  .final-main-title {
    gap: 1.25rem 2rem;
    margin-bottom: 1.5rem;
  }

  .final-word-item {
    gap: 0.75rem;
  }

  .final-word-icon {
    width: 50px;
    height: 50px;
  }

  .final-word-icon i {
    font-size: 1.5rem;
  }

  .final-word-text {
    font-size: 3rem;
    letter-spacing: -1.5px;
  }

  .final-word-divider {
    font-size: 2.5rem;
    margin: 0 0.25rem;
  }

  .final-subtitle {
    font-size: 1.25rem;
  }

  .final-details {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 1rem;
    margin-bottom: 2.5rem;
  }

  .final-detail-card {
    padding: 1.25rem 1rem;
  }

  .final-detail-icon-wrapper {
    width: 56px;
    height: 56px;
    margin-bottom: 0.75rem;
  }

  .final-detail-icon-wrapper i {
    font-size: 1.5rem;
  }

  .final-detail-label {
    font-size: 0.8rem;
  }

  .final-detail-value {
    font-size: 1rem;
  }

  .final-cta-button {
    padding: 1rem 2rem;
    font-size: 1.1rem;
  }

  .final-cta-icon {
    font-size: 1.25rem;
  }

  .final-cta-arrow {
    font-size: 1.25rem;
  }
}

@media (max-width: 576px) {
  .final-content {
    padding: 1.25rem 0.75rem;
  }

  .final-title-wrapper {
    margin-bottom: 2.5rem;
  }

  .final-main-title {
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 1.25rem;
  }

  .final-word-item {
    gap: 0.75rem;
    width: 100%;
    justify-content: center;
  }

  .final-word-icon {
    width: 45px;
    height: 45px;
  }

  .final-word-icon i {
    font-size: 1.35rem;
  }

  .final-word-text {
    font-size: 2.25rem;
    letter-spacing: -1px;
    white-space: normal;
    text-align: center;
  }

  .final-word-divider {
    display: none;
  }

  .final-subtitle {
    font-size: 1.1rem;
    padding: 0 0.5rem;
  }

  .final-details {
    grid-template-columns: 1fr;
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .final-detail-card {
    padding: 1.25rem;
  }

  .final-detail-icon-wrapper {
    width: 52px;
    height: 52px;
  }

  .final-detail-icon-wrapper i {
    font-size: 1.35rem;
  }

  .final-cta-button {
    padding: 0.9rem 1.75rem;
    font-size: 1rem;
    gap: 0.5rem;
  }

  .final-cta-icon {
    font-size: 1.15rem;
  }

  .final-cta-arrow {
    font-size: 1.15rem;
  }

  .final-cta-hint {
    font-size: 0.85rem;
    padding: 0 0.5rem;
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .info-card {
    padding: 1.5rem 1.25rem;
  }

  .info-header {
    margin-bottom: 1.5rem;
    padding-bottom: 0.75rem;
  }

  .info-title {
    font-size: 2rem;
  }

  .info-list,
  .info-list-dots {
    margin: 1rem 0;
  }

  .info-list li,
  .info-list-dots li {
    padding: 0.5rem 0;
  }

  .info-highlight {
    margin-top: 1rem;
    padding: 0.875rem;
  }

  .info-intro {
    margin-bottom: 1rem;
    font-size: 1.1rem;
  }

  .info-question {
    font-size: 1.1rem;
  }

  .info-answer {
    font-size: 1rem;
  }

  .info-list li,
  .info-list-dots li {
    font-size: 1rem;
  }

  .event-grid {
    grid-template-columns: 1fr;
  }

  .menu-grid {
    grid-template-columns: 1fr;
  }

  .tickets-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .guests-list {
    flex-direction: column;
    gap: 1rem;
  }

  .final-details {
    flex-direction: column;
    gap: 1rem;
  }

  .artist-name {
    font-size: 1.5rem;
  }

}

@media (max-width: 576px) {
  .info-card {
    padding: 1.25rem 0.875rem;
    border-radius: 16px;
  }

  .info-header {
    margin-bottom: 1.25rem;
    padding-bottom: 0.625rem;
  }

  .info-title {
    font-size: 1.75rem;
  }

  .info-list,
  .info-list-dots {
    margin: 0.875rem 0;
  }

  .info-list li,
  .info-list-dots li {
    padding: 0.5rem 0;
    font-size: 0.95rem;
  }

  .info-highlight {
    margin-top: 0.875rem;
    padding: 0.75rem;
    font-size: 1.05rem;
  }

  .info-intro {
    margin-bottom: 0.875rem;
    font-size: 1.05rem;
  }

  .info-question {
    font-size: 1rem;
    margin-bottom: 0.75rem;
  }

  .info-answer {
    font-size: 0.95rem;
    margin-bottom: 1rem;
  }

  .info-highlight {
    font-size: 1.1rem;
  }

  .info-highlight-big {
    font-size: 1.2rem;
  }

  .event-item {
    padding: 0.75rem;
  }

  .event-item span {
    font-size: 0.95rem;
  }

  .tickets-grid {
    grid-template-columns: 1fr;
  }

  .ticket-price {
    font-size: 1.5rem;
  }

  .final-title {
    font-size: 1.5rem;
  }

  .final-detail-item {
    font-size: 1rem;
  }
}

/* Нижнее меню навигации */
.bottom-nav {
  position: fixed !important;
  bottom: 0 !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  z-index: 10001 !important;
  display: flex !important;
  justify-content: center;
  align-items: center;
  background: rgba(24, 114, 63, 0.95) !important;
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.2);
  padding: 0.75rem 1.5rem;
  border-radius: 24px 24px 0 0;
  gap: 0.5rem;
  max-width: fit-content;
  min-width: fit-content;
  width: auto;
  overflow-x: auto;
  overflow-y: visible;
  scrollbar-width: none;
  -ms-overflow-style: none;
  visibility: visible !important;
  opacity: 1 !important;
  pointer-events: auto !important;
  margin: 0 !important;
}

.bottom-nav::-webkit-scrollbar {
  display: none;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  padding: 0.5rem 0.75rem;
  background: transparent;
  border: none;
  color: #fff;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  min-width: 60px;
  flex-shrink: 0;
  position: relative;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  color: #fff;
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  top: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 3px;
  background: #fff;
  border-radius: 0 0 3px 3px;
}

.nav-item i {
  font-size: 1.5rem;
  color: #fff;
  transition: all 0.3s ease;
}

.nav-item.active i {
  transform: scale(1.1);
}

.nav-item span {
  font-size: 0.7rem;
  font-weight: 600;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  opacity: 0.9;
}

.nav-item.active span {
  opacity: 1;
  font-weight: 700;
}

@media (max-width: 768px) {
  .bottom-nav {
    padding: 0.5rem 1rem;
    left: 50%;
    transform: translateX(-50%);
    border-radius: 20px 20px 0 0;
    gap: 0.4rem;
  }

  .nav-item {
    padding: 0.4rem 0.6rem;
    min-width: 50px;
  }

  .nav-item i {
    font-size: 1.25rem;
  }

  .nav-item span {
    font-size: 0.65rem;
  }
}

@media (max-width: 576px) {
  .bottom-nav {
    padding: 0.5rem 0.75rem;
    left: 50%;
    transform: translateX(-50%);
    border-radius: 16px 16px 0 0;
    gap: 0.3rem;
  }

  .nav-item {
    padding: 0.35rem 0.5rem;
    min-width: 45px;
  }

  .nav-item i {
    font-size: 1.1rem;
  }

  .nav-item span {
    font-size: 0.6rem;
  }
}
</style>

