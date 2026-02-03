<template>
  <div class="yandex-map-wrapper">
    <div v-if="useYandex" ref="mapContainer" class="yandex-map-container"></div>
    <div v-else ref="leafletContainer" class="yandex-map-container"></div>
    <a
      :href="yandexMapsLink"
      target="_blank"
      rel="noopener noreferrer"
      class="yandex-map-link"
    >
      <i class="bi bi-geo-alt me-2"></i>Открыть в Яндекс.Картах
    </a>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

const props = defineProps<{
  lat: number;
  lon: number;
  address?: string;
}>();

const mapContainer = ref<HTMLDivElement | null>(null);
const leafletContainer = ref<HTMLDivElement | null>(null);
let leafletMap: L.Map | null = null;

const yandexApiKey = import.meta.env.VITE_YANDEX_MAPS_API_KEY as string | undefined;
const useYandex = computed(() => !!yandexApiKey);

const yandexMapsLink = computed(() => {
  if (props.address) {
    return `https://yandex.ru/maps/?text=${encodeURIComponent(props.address)}`;
  }
  return `https://yandex.ru/maps/?ll=${props.lon},${props.lat}&z=16&pt=${props.lon},${props.lat}`;
});

const redMarkerIcon = L.divIcon({
  className: 'red-marker-icon',
  html: '<div class="red-marker-pin"></div>',
  iconSize: [24, 24],
  iconAnchor: [12, 24]
});

function initLeafletMap() {
  if (!leafletContainer.value) return;
  leafletMap = L.map(leafletContainer.value).setView([props.lat, props.lon], 16);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
  }).addTo(leafletMap);
  L.marker([props.lat, props.lon], { icon: redMarkerIcon })
    .addTo(leafletMap)
    .bindPopup(props.address || 'Место проведения');
}

function initYandexMap() {
  if (!mapContainer.value || typeof (window as any).ymaps === 'undefined') return;

  const ymaps = (window as any).ymaps;
  ymaps.ready(() => {
    try {
      const map = new ymaps.Map(mapContainer.value, {
        center: [props.lat, props.lon],
        zoom: 16,
        controls: ['zoomControl', 'typeSelector', 'fullscreenControl']
      });

      const placemark = new ymaps.Placemark(
        [props.lat, props.lon],
        props.address ? { balloonContent: props.address } : {},
        { preset: 'islands#redDotIcon' }
      );

      map.geoObjects.add(placemark);
    } catch (e) {
      console.error('Yandex Map init error:', e);
    }
  });
}

function loadYandexScript() {
  if ((window as any).ymaps) {
    initYandexMap();
    return;
  }
  const script = document.createElement('script');
  script.src = `https://api-maps.yandex.ru/2.1/?apikey=${yandexApiKey}&lang=ru_RU`;
  script.async = true;
  script.onload = initYandexMap;
  document.head.appendChild(script);
}

onMounted(() => {
  if (useYandex.value) {
    loadYandexScript();
  } else {
    initLeafletMap();
  }
});

onUnmounted(() => {
  if (leafletMap) {
    leafletMap.remove();
    leafletMap = null;
  }
});

</script>

<style scoped>
.yandex-map-wrapper {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #eee;
}

.yandex-map-container {
  width: 100%;
  height: 300px;
  min-height: 300px;
}

:deep(.red-marker-icon) {
  background: none;
  border: none;
}

:deep(.red-marker-pin) {
  width: 24px;
  height: 24px;
  background: #dc3545;
  border: 3px solid #fff;
  border-radius: 50% 50% 50% 0;
  transform: rotate(-45deg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.yandex-map-link {
  display: inline-flex;
  align-items: center;
  padding: 12px 16px;
  font-size: 0.9rem;
  color: #1a73e8;
  text-decoration: none;
  background: #f8f9fa;
  transition: background 0.2s;
}

.yandex-map-link:hover {
  background: #eee;
  color: #1557b0;
}
</style>
