<template>
  <div v-if="open && concert" class="modal_overlay" @click.self="$emit('close')">
    <div class="modal_content" role="dialog" aria-modal="true">
      <button class="modal_close" @click="$emit('close')" aria-label="Закрыть">
        <span></span>
      </button>

      <section class="modal_inner">
        <h1 class="modal_title">{{ concert.title }}</h1>

        <div class="event-info">
          <time :datetime="concert.concertDate" class="event-info__date">
            {{ formattedDate }}
          </time>
          <div class="event-info__venue-block">
            <span class="event-info__venue">{{ concert.venue }}</span>
            <span class="event-info__age" v-if="concert.ageRestriction">
              {{ concert.ageRestriction }}
            </span>
          </div>
        </div>

        <div class="center_area">
          <div class="top_btn for-mobile" v-if="formattedPrice">
            <button class="btn btn_buy" @click="onBuy">
              Купить от {{ formattedPrice }}
            </button>
          </div>

          <div class="poster_wrapper" v-if="posterImage">
            <img :src="posterImage" :alt="concert.title" class="modal_poster" />
          </div>

          <div class="text_block">
            <div class="text" v-html="formattedDescription"></div>

            <button
              class="btn btn_buy"
              @click="onBuy"
              v-if="formattedPrice"
            >
              Купить от {{ formattedPrice }}
            </button>
          </div>
        </div>

        <div class="bottom_area">
          <h2 class="bottom_area__venue">{{ concert.venue }}</h2>
          <div class="bottom_area__address" v-if="concert.city">
            {{ concert.city }}
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

const props = defineProps<{
  open: boolean;
  concert: Concert | null;
}>();

const emit = defineEmits<{
  close: [];
  buy: [];
}>();

const posterImage = computed(() =>
  props.concert?.posterUrl ? props.concert.posterUrl : fallbackPoster
);

const formattedDate = computed(() => {
  if (!props.concert?.concertDate) return '';
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
  const cents = props.concert?.minTicketPriceCents;
  if (cents == null || cents < 0) return null;
  return `${Math.floor(cents / 100).toLocaleString('ru-RU')} руб.`;
});

const formattedDescription = computed(() => {
  const desc = props.concert?.description || '';
  return desc.replace(/\n/g, '<br>');
});

const onBuy = () => {
  emit('buy');
};
</script>

<style scoped>
.modal_overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
  overflow-y: auto;
}

.modal_content {
  position: relative;
  background: #fff;
  border-radius: 16px;
  max-width: 600px;
  width: 100%;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal_close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
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

.modal_close:hover {
  background: rgba(0, 0, 0, 0.7);
}

.modal_close span {
  position: relative;
  display: block;
  width: 20px;
  height: 2px;
  background: #fff;
  transform: rotate(45deg);
}

.modal_close span::after {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 20px;
  height: 2px;
  background: #fff;
  transform: rotate(90deg);
}

.modal_inner {
  padding: 32px 24px 24px;
}

.modal_title {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 20px;
  line-height: 1.3;
  color: #1a1a1a;
}

.event-info {
  margin-bottom: 24px;
}

.event-info__date {
  display: block;
  font-size: 1rem;
  color: #555;
  margin-bottom: 8px;
}

.event-info__venue-block {
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

.center_area {
  margin-bottom: 24px;
}

.top_btn.for-mobile {
  margin-bottom: 16px;
}

.poster_wrapper {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.modal_poster {
  width: 100%;
  display: block;
  max-height: 400px;
  object-fit: cover;
}

.text_block {
  margin-top: 20px;
}

.text {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #444;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.text :deep(br) {
  display: block;
  content: '';
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

.bottom_area__venue {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 8px;
  color: #1a1a1a;
}

.bottom_area__address {
  font-size: 0.9rem;
  color: #666;
}

@media (min-width: 576px) {
  .btn_buy {
    width: auto;
  }

  .top_btn.for-mobile {
    display: none;
  }
}
</style>
