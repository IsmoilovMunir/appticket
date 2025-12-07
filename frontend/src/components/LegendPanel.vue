<template>
  <div class="legend-card border rounded-4 p-3 bg-body">
    <div class="mb-2 fw-semibold small text-uppercase text-body-secondary">Категории</div>
    <div class="d-flex flex-wrap gap-4 mb-3">
      <div v-for="item in priceLegend" :key="item.label" class="d-flex align-items-center gap-2">
        <span class="legend-dot" :style="{ background: item.color }"></span>
        <span class="small">{{ item.label }}</span>
      </div>
    </div>
    <div class="mb-2 fw-semibold small text-uppercase text-body-secondary">Статус</div>
    <div class="d-flex flex-wrap gap-4">
      <div v-for="item in statusLegend" :key="item.label" class="d-flex align-items-center gap-2">
        <span class="legend-dot" :style="{ background: item.color }"></span>
        <span class="small">{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useSeatStore } from '../stores/seatStore';
import { getPriceLegendFallback, resolveCategoryColor, resolveStatusColor } from '../utils/seatColors';

const seatStore = useSeatStore();

const formatPrice = (cents: number) =>
  new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    maximumFractionDigits: 0
  }).format(cents / 100);

const priceLegend = computed(() => {
  if (!seatStore.seats.length) {
    return getPriceLegendFallback();
  }
  const map = new Map<string, { label: string; color: string }>();
  seatStore.seats.forEach((seat) => {
    const key = seat.categoryName || `price-${seat.priceCents}`;
    if (map.has(key)) {
      return;
    }
    const priceLabel = formatPrice(seat.priceCents);
    map.set(key, {
      label: seat.categoryName ? `${seat.categoryName} · ${priceLabel}` : priceLabel,
      color: resolveCategoryColor(seat.priceCents, seat.categoryColorHex)
    });
  });
  return Array.from(map.values());
});

const statusLegend = [
  { label: 'Свободно', color: resolveStatusColor('AVAILABLE') },
  { label: 'Удерживается', color: resolveStatusColor('HELD') },
  { label: 'Продано', color: resolveStatusColor('SOLD') },
  { label: 'Заблокировано', color: resolveStatusColor('BLOCKED') }
];
</script>
