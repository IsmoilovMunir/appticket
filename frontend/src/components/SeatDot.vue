<template>
  <g @click="handleClick" class="seat-node">
    <circle
      :cx="x"
      :cy="y"
      r="16"
      :class="['seat-dot', statusClass, { selected }]"
      :data-seat-id="seat.id"
      :fill="fillColor"
      :stroke="selected ? '#18723F' : 'transparent'"
      :stroke-width="selected ? 4 : 0"
    />
    <text
      v-if="holdLabel"
      :x="x"
      :y="y - 24"
      text-anchor="middle"
      font-size="10"
      fill="#6c757d"
    >
      {{ holdLabel }}
    </text>
    <title>
      Стол {{ seat.tableNumber }}, место {{ seat.chairNumber }} · {{ seat.categoryName }} ·
      {{ formatPrice(seat.priceCents) }}
    </title>
  </g>
</template>

<script setup lang="ts">
import type { Seat } from '../types';
import { computed } from 'vue';

import { resolveCategoryColor, resolveStatusColor } from '../utils/seatColors';

const props = defineProps<{
  seat: Seat;
  selected: boolean;
  holdSeconds?: number;
}>();

const emit = defineEmits<{
  toggle: [];
}>();

const angle = computed(() => ((props.seat.chairNumber - 1) / 10) * Math.PI * 2);
const col = computed(() => (props.seat.tableNumber - 1) % 5);
const row = computed(() => Math.floor((props.seat.tableNumber - 1) / 5));
const baseX = computed(() => 120 + col.value * 160);
const baseY = computed(() => 140 + row.value * 80);
const x = computed(() => baseX.value + Math.cos(angle.value) * 28);
const y = computed(() => baseY.value + Math.sin(angle.value) * 28);

const statusClass = computed(() => {
  switch (props.seat.status) {
    case 'AVAILABLE':
      return 'available';
    case 'HELD':
      return 'held';
    case 'SOLD':
      return 'sold';
    default:
      return 'blocked';
  }
});

const fillColor = computed(() => {
  if (props.seat.status === 'AVAILABLE') {
    return resolveCategoryColor(props.seat.priceCents, props.seat.categoryColorHex);
  }
  return resolveStatusColor(props.seat.status);
});

const handleClick = () => {
  if (props.seat.status !== 'AVAILABLE') return;
  emit('toggle');
};

const formatPrice = (cents: number) =>
  new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB' }).format(cents / 100);

const holdLabel = computed(() => {
  if (!props.holdSeconds || props.holdSeconds <= 0) return '';
  const minutes = String(Math.floor(props.holdSeconds / 60)).padStart(2, '0');
  const seconds = String(props.holdSeconds % 60).padStart(2, '0');
  return `${minutes}:${seconds}`;
});
</script>

