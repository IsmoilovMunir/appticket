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

    <!-- Program Section -->
    <section class="py-5 py-md-4 py-sm-3 bg-body">
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
    <section class="py-5 py-md-4 py-sm-3 bg-light">
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
    <section class="py-5 py-md-4 py-sm-3 bg-body">
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
                    href="https://t.me/surnekevents" 
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
    <section class="py-5 bg-primary text-white">
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
        </div>
      </div>
    </section>

    <SeatMapModal :open="modalOpen" :concert-id="concertId" @close="modalOpen = false" />

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
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import ConcertHero from '../components/ConcertHero.vue';
import SeatMapModal from '../components/SeatMapModal.vue';
import { useConcertStore } from '../stores/concertStore';
import { storeToRefs } from 'pinia';
import { submitPartnerRequest as apiSubmitPartnerRequest } from '../services/api';
import chidLogo from '../assets/chid.svg';
import asakiLogo from '../assets/asaki.svg';
import drnematboevLogo from '../assets/drnematboev.svg';
import smartxLogo from '../assets/smartx.svg';
import chidSvgLogo from '../assets/chid.svg';

const concertId = 1;
const modalOpen = ref(false);
const selectedSponsor = ref<any>(null);
const showPartnerRequestModal = ref(false);
const partnerRequestForm = ref({
  fullName: '',
  company: '',
  phone: '',
  email: ''
});
const partnerRequestLoading = ref(false);
const partnerRequestError = ref('');
const partnerRequestSuccess = ref(false);

const concertStore = useConcertStore();
const { concert } = storeToRefs(concertStore);

onMounted(() => {
  concertStore.load(concertId);
});

const openModal = () => {
  modalOpen.value = true;
};

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
</style>

