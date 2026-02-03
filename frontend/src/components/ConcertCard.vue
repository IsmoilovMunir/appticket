<template>
  <RouterLink :to="`/${concert.slug}`" class="concert-card">
    <div class="concert-card__poster">
      <img :src="posterImage" :alt="concert.title" class="concert-card__img" />
      <div class="concert-card__overlay">
        <span class="concert-card__price" v-if="formattedPrice">
          от {{ formattedPrice }}
        </span>
      </div>
    </div>
    <div class="concert-card__body">
      <h3 class="concert-card__title">{{ concert.title }}</h3>
      <div class="concert-card__info">
        <time :datetime="concert.concertDate" class="concert-card__date">
          {{ formattedDate }}
        </time>
        <span class="concert-card__venue">{{ concert.venue }}</span>
        <span class="concert-card__age" v-if="concert.ageRestriction">
          {{ concert.ageRestriction }}
        </span>
      </div>
      <RouterLink :to="`/${concert.slug}?buy=1`" class="concert-card__btn" @click.stop>
        Купить билет
      </RouterLink>
    </div>
  </RouterLink>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { RouterLink } from 'vue-router';
import type { Concert } from '../types';
import fallbackPoster from '@/assets/post.png';

const props = defineProps<{
  concert: Concert;
}>();

const posterImage = computed(() => props.concert.posterUrl || fallbackPoster);

const formattedDate = computed(() => {
  if (!props.concert.concertDate) return '';
  const date = new Date(props.concert.concertDate);
  if (isNaN(date.getTime())) return props.concert.concertDate;
  return date.toLocaleDateString('ru-RU', {
    weekday: 'short',
    day: 'numeric',
    month: 'long',
    hour: '2-digit',
    minute: '2-digit'
  });
});

const formattedPrice = computed(() => {
  const cents = props.concert.minTicketPriceCents;
  if (cents == null || cents < 0) return null;
  return `${Math.floor(cents / 100).toLocaleString('ru-RU')} ₽`;
});
</script>

<style scoped>
.concert-card {
  display: block;
  text-decoration: none;
  color: inherit;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.concert-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.concert-card__poster {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
}

.concert-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.concert-card__overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
}

.concert-card__price {
  color: #fff;
  font-weight: 600;
  font-size: 0.95rem;
}

.concert-card__body {
  padding: 20px;
}

.concert-card__title {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 12px;
  line-height: 1.3;
  color: #1a1a1a;
}

.concert-card__info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;
  margin-bottom: 16px;
  font-size: 0.9rem;
  color: #555;
}

.concert-card__date {
  display: flex;
  align-items: center;
  gap: 6px;
}

.concert-card__date::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='%23666' viewBox='0 0 16 16'%3E%3Cpath d='M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5zM1 4v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4H1z'/%3E%3C/svg%3E") no-repeat center;
}

.concert-card__venue::before {
  content: '•';
  margin-right: 8px;
  color: #999;
}

.concert-card__age {
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}

.concert-card__btn {
  display: inline-block;
  padding: 12px 24px;
  background: #1a1a1a;
  color: #fff !important;
  font-weight: 600;
  text-decoration: none;
  border-radius: 8px;
  transition: background 0.2s;
}

.concert-card__btn:hover,
.concert-card__btn:active,
.concert-card__btn:focus {
  background: #DBFF06;
  color: #000 !important;
}
</style>
