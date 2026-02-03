<template>
  <div class="concert-detail-card" :class="{ 'concert-detail-card--modal': asModal }">
    <div v-if="asModal" class="concert-detail-card__overlay" @click.self="$emit('close')"></div>

    <div class="concert-detail-card__content">
      <button
        v-if="asModal"
        class="concert-detail-card__close"
        @click="$emit('close')"
        aria-label="Закрыть"
      >
        <span></span>
      </button>

      <section class="concert-detail-card__inner">
        <!-- На мобильном: заголовок, дата, кнопка, место — сверху -->
        <div class="concert-detail-card__mobile-top">
          <h1 class="concert-detail-card__title">{{ concert.title }}</h1>
          <time :datetime="concert.concertDate" class="event-info__time">
            {{ formattedDate }}
          </time>
          <div class="top_btn for-mobile" v-if="formattedPrice">
            <button class="btn btn_buy" @click="$emit('buy')">
              Купить от {{ formattedPrice }}
            </button>
          </div>
          <div class="event-info__mobile for-mobile">
            <span v-if="concert.ageRestriction" class="event-info__age">{{ concert.ageRestriction }}</span>
            <span class="event-info__venue">{{ concert.venue }}</span>
          </div>
        </div>

        <!-- Баннер (картинка) -->
        <div class="poster_wrapper poster_wrapper--top" v-if="posterImage">
          <img :src="posterImage" :alt="concert.title" class="poster_img" />
        </div>

        <div class="event-info for-desktop">
          <time :datetime="concert.concertDate" class="event-info__time">
            {{ formattedDate }}
          </time>
          <span class="event-info__name-block">
            <span class="event-info__venue">{{ concert.venue }}</span>
            <span v-if="concert.ageRestriction" class="event-info__age">
              {{ concert.ageRestriction }}
            </span>
          </span>
        </div>

        <div class="col">
          <div class="text" v-html="formattedDescription"></div>
          <button
            v-if="formattedPrice"
            class="btn btn_buy"
            @click="$emit('buy')"
          >
            Купить от {{ formattedPrice }}
          </button>
        </div>

        <div v-if="addressForMaps || hasCoordinates" class="bottom_area">
          <a
            v-if="addressForMaps"
            :href="yandexMapsUrl"
            target="_blank"
            rel="noopener noreferrer"
            class="bottom_area__address"
          >
            <i class="bi bi-geo-alt me-2"></i>{{ addressForMaps }}
          </a>
          <div v-if="hasCoordinates" class="bottom_area__map">
            <YandexMap
              :lat="concert.venueLat!"
              :lon="concert.venueLon!"
              :address="addressForMaps || undefined"
            />
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { Concert } from '../types';
import fallbackPoster from '@/assets/post.png';
import YandexMap from './YandexMap.vue';

const props = withDefaults(
  defineProps<{
    concert: Concert;
    asModal?: boolean;
  }>(),
  { asModal: false }
);

defineEmits<{
  close: [];
  buy: [];
}>();

const posterImage = computed(() => props.concert.posterUrl || fallbackPoster);

const formattedDate = computed(() => {
  if (!props.concert.concertDate) return '';
  const date = new Date(props.concert.concertDate);
  if (isNaN(date.getTime())) return props.concert.concertDate;
  return date.toLocaleDateString('ru-RU', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    hour: '2-digit',
    minute: '2-digit'
  });
});

const formattedPrice = computed(() => {
  const cents = props.concert.minTicketPriceCents;
  if (cents == null || cents < 0) return null;
  return `${Math.floor(cents / 100).toLocaleString('ru-RU')} руб.`;
});

const formattedDescription = computed(() => {
  const desc = props.concert.description || '';
  return desc.replace(/\n/g, '<br>');
});

const addressForMaps = computed(() => {
  return props.concert.city || props.concert.venue || '';
});

const yandexMapsUrl = computed(() => {
  if (!addressForMaps.value) return '#';
  return `https://yandex.ru/maps/?text=${encodeURIComponent(addressForMaps.value)}`;
});

const hasCoordinates = computed(() => {
  const lat = props.concert.venueLat;
  const lon = props.concert.venueLon;
  return lat != null && lon != null && !isNaN(lat) && !isNaN(lon);
});
</script>

<style scoped>
.concert-detail-card {
  position: relative;
  width: 100%;
}

.concert-detail-card--modal {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow-y: auto;
}

.concert-detail-card__overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  cursor: pointer;
}

.concert-detail-card__content {
  position: relative;
  background: #fff;
  border-radius: 16px;
  max-width: 640px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
}

.concert-detail-card--modal .concert-detail-card__content {
  margin: auto;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.concert-detail-card__close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 44px;
  height: 44px;
  border: none;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  cursor: pointer;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.concert-detail-card__close:hover {
  background: rgba(0, 0, 0, 0.7);
}

.concert-detail-card__close span {
  position: relative;
  display: block;
  width: 22px;
  height: 2px;
  background: #fff;
  transform: rotate(45deg);
}

.concert-detail-card__close span::after {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 22px;
  height: 2px;
  background: #fff;
  transform: rotate(90deg);
}

.concert-detail-card__inner {
  padding: 0 0 28px;
}

.poster_wrapper--top {
  margin: 0 0 24px;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.concert-detail-card__title,
.event-info,
.top_btn,
.col,
.bottom_area,
.concert-detail-card__mobile-top {
  padding-left: 24px;
  padding-right: 24px;
}

.concert-detail-card__mobile-top {
  display: none;
}

.concert-detail-card__title {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 20px;
  padding-top: 24px;
  line-height: 1.3;
  color: #1a1a1a;
}

.concert-detail-card__mobile-top .concert-detail-card__title {
  padding-top: 0;
  margin-bottom: 12px;
}

.event-info {
  margin-bottom: 24px;
}

.event-info__time {
  display: block;
  font-size: 1rem;
  color: #555;
  margin-bottom: 8px;
}

.event-info__name-block {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.event-info__venue {
  font-size: 1rem;
  color: #333;
}

.event-info__age {
  background: #f0f0f0;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #555;
}

.event-info__mobile {
  display: none;
}

.center_area {
  margin-bottom: 24px;
}

.top_btn.for-mobile {
  margin-bottom: 16px;
}

.top_btn.for-mobile .btn_buy {
  width: 100%;
}

.poster_wrapper {
  overflow: hidden;
}

.poster_img {
  width: 100%;
  display: block;
  max-height: 420px;
  object-fit: cover;
}

.col {
  margin-top: 20px;
}

.text {
  font-size: 0.95rem;
  line-height: 1.65;
  color: #444;
  margin-bottom: 24px;
}

.text :deep(br) {
  display: block;
  margin-top: 0.5em;
}

.btn {
  display: inline-block;
  padding: 14px 28px;
  background: #1a1a1a;
  color: #fff;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.btn:hover,
.btn:active,
.btn:focus {
  background: #DBFF06;
  color: #000;
}

.btn_buy {
  width: 100%;
  text-align: center;
}

.bottom_area {
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.bottom_area__address {
  display: inline-flex;
  align-items: center;
  font-size: 0.95rem;
  color: #1a73e8;
  text-decoration: none;
  transition: color 0.2s;
}

.bottom_area__address:hover {
  color: #1557b0;
  text-decoration: underline;
}

.bottom_area__map {
  margin-top: 16px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #eee;
}


@media (min-width: 576px) {
  .btn_buy {
    width: auto;
  }

  .top_btn.for-mobile {
    display: none;
  }
}

@media (max-width: 575px) {
  .concert-detail-card__mobile-top {
    display: block;
    padding-top: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
  }

  .concert-detail-card__mobile-top .event-info__time {
    margin-bottom: 12px;
  }

  .concert-detail-card__mobile-top .top_btn {
    margin: 16px 0 12px;
    padding-left: 0;
    padding-right: 0;
  }

  .concert-detail-card__mobile-top .event-info__mobile {
    margin: 0;
    padding: 0;
  }

  .for-desktop {
    display: none;
  }

  .for-mobile {
    display: block;
  }

  .event-info__mobile {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .poster_wrapper--top {
    border-radius: 0;
    margin-bottom: 24px;
  }
}
</style>
