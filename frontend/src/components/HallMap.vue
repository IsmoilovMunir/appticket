<template>
  <div class="hall-map-container">
    <div class="hall-map-toolbar">
      <button type="button" class="zoom-btn" :disabled="!canZoomOut" @click="zoomOut">−</button>
      <span class="zoom-value">
        <span v-if="zoomPercent > 0">+{{ zoomPercent }}%</span>
        <span v-else>{{ zoomPercent }}%</span>
      </span>
      <button type="button" class="zoom-btn" :disabled="!canZoomIn" @click="zoomIn">+</button>
      <button type="button" class="zoom-btn reset" :disabled="zoom === baseZoom" @click="resetZoom">100%</button>
    </div>
    <div class="hall-map-viewport" ref="viewport">
      <div class="hall-map" ref="svgHost" :style="zoomStyle" v-html="svgMarkup"></div>
    </div>

    <div v-if="blockedMessage" class="seat-toast">
      {{ blockedMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, watch, nextTick, computed } from 'vue';
import type { Seat } from '../types';
import hallSvg from '../assets/hall.svg?raw';
import { useSeatStore } from '../stores/seatStore';
import { resolveCategoryColor } from '../utils/seatColors';

const props = defineProps<{
  schemeUrl?: string | null;
}>();

const emit = defineEmits<{
  danceFloorClicked: [data: { availableSeats: number; price: number }];
}>();

const svgHost = ref<HTMLElement | null>(null);
const viewport = ref<HTMLElement | null>(null);
const svgMarkup = ref<string>(hallSvg);
const loadingScheme = ref(false);
const seatStore = useSeatStore();
const seatElementsById = ref<Map<number, SVGElement>>(new Map());
const seatElementsByLayout = ref<Map<number, SVGElement>>(new Map());
const seatElementsByPosition = ref<Map<string, SVGElement>>(new Map());
const tableElementsByNumber = ref<Map<number, SVGElement>>(new Map());
const MIN_ZOOM = 0.05;
const MAX_ZOOM = 3;
const ZOOM_STEP = 0.2;
const DESKTOP_BASE_ZOOM = 3.6;
const MOBILE_EXTRA_ZOOM = 2.85;
const SVG_FALLBACK_WIDTH = 2200;
const SVG_FALLBACK_HEIGHT = 3569;
const zoom = ref(1);
const baseZoom = ref(1);
let viewportObserver: ResizeObserver | null = null;

const blockedMessage = ref<string | null>(null);
let blockedTimer: number | null = null;

type SeatMeta = {
  seatId?: number;
  tableNumber?: number;
  chairNumber?: number;
};

const parseNumber = (value?: string | null) => {
  if (value == null || value === '') return undefined;
  const numeric = Number(value);
  return Number.isNaN(numeric) ? undefined : numeric;
};

const clampZoom = (value: number) => Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, value));
const setZoom = (value: number) => {
  zoom.value = Number(clampZoom(value).toFixed(3));
};
const zoomIn = () => setZoom(zoom.value + ZOOM_STEP);
const zoomOut = () => setZoom(zoom.value - ZOOM_STEP);
const resetZoom = () => setZoom(baseZoom.value);
const bumpZoom = () => setZoom(zoom.value * 1.16);
const zoomPercent = computed(() => Math.round((zoom.value / baseZoom.value - 1) * 100));
const canZoomIn = computed(() => zoom.value < MAX_ZOOM - 0.02);
const canZoomOut = computed(() => zoom.value > baseZoom.value * 0.4 && zoom.value > MIN_ZOOM + 0.02);
const zoomStyle = computed(() => ({
  transform: `scale(${zoom.value})`,
  transformOrigin: 'center top'
}));

const getSvgElement = () => svgHost.value?.querySelector('svg');

const getSvgDimensions = () => {
  const svgElement = getSvgElement();
  if (!svgElement) {
    return { width: SVG_FALLBACK_WIDTH, height: SVG_FALLBACK_HEIGHT };
  }
  const viewBox = svgElement.viewBox?.baseVal;
  if (viewBox && viewBox.width && viewBox.height) {
    return { width: viewBox.width, height: viewBox.height };
  }
  const width = svgElement.width?.baseVal?.value || SVG_FALLBACK_WIDTH;
  const height = svgElement.height?.baseVal?.value || SVG_FALLBACK_HEIGHT;
  return { width, height };
};

const centerViewport = (behavior: ScrollBehavior = 'auto') => {
  const viewportEl = viewport.value;
  if (!viewportEl) return;
  const contentWidth = viewportEl.scrollWidth;
  const viewportWidth = viewportEl.clientWidth;
  const targetTop = 0;
  const targetLeft = Math.max(0, (contentWidth - viewportWidth) / 2);
  viewportEl.scrollTo({ top: targetTop, left: targetLeft, behavior });
};

const currentBaseFactor = () => {
  if (typeof window === 'undefined') return DESKTOP_BASE_ZOOM;
  return (window.innerWidth || 0) <= 768 ? DESKTOP_BASE_ZOOM * MOBILE_EXTRA_ZOOM : DESKTOP_BASE_ZOOM;
};

const fitToViewport = (options: { preserveRelative?: boolean } = {}) => {
  const { preserveRelative = true } = options;
  const viewportEl = viewport.value;
  if (!viewportEl) return;
  const { width, height } = getSvgDimensions();
  const viewportWidth = viewportEl.clientWidth || window.innerWidth;
  const viewportHeight = viewportEl.clientHeight || window.innerHeight * 0.8;
  // Используем меньший масштаб, чтобы карта помещалась и была возможность прокрутки
  const widthScale = viewportWidth / width;
  const heightScale = viewportHeight / height;
  const baseFactor = currentBaseFactor();
  // Используем 90% от минимального масштаба для лучшей видимости и прокрутки
  const targetBase = clampZoom(Math.min(widthScale, heightScale) * baseFactor * 0.9);
  const relative = preserveRelative ? zoom.value / baseZoom.value : 1;
  baseZoom.value = targetBase;
  setZoom(baseZoom.value * relative);
  if (!preserveRelative) {
    // Не центрируем автоматически, чтобы была видна левая часть карты
    viewportEl.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  }
};

const deriveTableAndChair = (value?: number) => {
  if (value == null || Number.isNaN(value)) {
    return { table: undefined, chair: undefined };
  }
  const normalized = Math.abs(Math.trunc(value));
  if (normalized < 100) {
    return { table: normalized, chair: undefined };
  }
  const table = Math.floor(normalized / 100);
  const remainder = normalized % 100;
  return {
    table,
    chair: remainder === 0 ? undefined : remainder
  };
};

const buildLayoutKey = (tableNumber?: number, chairNumber?: number) => {
  // tableNumber может быть 0 для танцпола, поэтому проверяем !== undefined
  if (tableNumber === undefined || tableNumber === null || chairNumber === undefined || chairNumber === null) return undefined;
  return tableNumber * 100 + chairNumber;
};

const extractSeatMeta = (element: Element | null): SeatMeta | null => {
  if (!element) return null;
  const dataset = (element as HTMLElement).dataset;
  if (!dataset) return null;

  let seatId = parseNumber(dataset.seatId ?? dataset.seat ?? dataset.seatNumber ?? dataset.seatindex);
  let tableNumber = parseNumber(dataset.table ?? dataset.tableNumber ?? dataset.tablenumber);
  let chairNumber = parseNumber(dataset.chair ?? dataset.chairNumber ?? dataset.seatindex ?? dataset.seat);

  if (tableNumber && tableNumber >= 100 && !chairNumber) {
    const composite = deriveTableAndChair(tableNumber);
    tableNumber = composite.table;
    chairNumber = composite.chair ?? chairNumber;
  }

  if ((!tableNumber || !chairNumber) && seatId) {
    const composite = deriveTableAndChair(seatId);
    tableNumber = composite.table ?? tableNumber;
    chairNumber = composite.chair ?? chairNumber;
  }

  if ((!tableNumber || !chairNumber) && dataset.table) {
    const compositeValue = parseNumber(dataset.table);
    if (compositeValue) {
      const composite = deriveTableAndChair(compositeValue);
      tableNumber = composite.table ?? tableNumber;
      chairNumber = composite.chair ?? chairNumber;
    }
  }

  if (!seatId && dataset.seatId) {
    seatId = parseNumber(dataset.seatId);
  }

  return { seatId, tableNumber, chairNumber };
};

const buildSeatIndex = () => {
  const host = svgHost.value;
  if (!host) return;
  const idMap = new Map<number, SVGElement>();
  const layoutMap = new Map<number, SVGElement>();
  const positionMap = new Map<string, SVGElement>();
  const tableMap = new Map<number, SVGElement>();
  const nodes = host.querySelectorAll<SVGElement>('[data-seat-id], [data-table], [data-chair]');
  let currentTableNumber: number | undefined;
  nodes.forEach((node) => {
    const tableAttrRaw = node.getAttribute('data-table');
    const tableAttrNumber = parseNumber(tableAttrRaw);
    const isSeatNode =
      node.hasAttribute('data-seat-id') ||
      node.hasAttribute('data-chair') ||
      (tableAttrNumber != null && tableAttrNumber >= 100);

    if (tableAttrNumber != null && !Number.isNaN(tableAttrNumber) && !isSeatNode) {
      currentTableNumber = tableAttrNumber;
      tableMap.set(tableAttrNumber, node);
    }
    // Специальная обработка для танцпола (tableNumber = 0)
    if (tableAttrNumber === 0) {
      tableMap.set(0, node);
    }

    const meta = extractSeatMeta(node);
    if (!meta) return;
    if (currentTableNumber != null && isSeatNode) {
      meta.tableNumber = currentTableNumber;
    }
    if (meta.seatId && !Number.isNaN(meta.seatId)) {
      idMap.set(meta.seatId, node);
    }
    // tableNumber может быть 0 для танцпола, поэтому проверяем !== undefined
    if (meta.tableNumber !== undefined && meta.tableNumber !== null && meta.chairNumber !== undefined && meta.chairNumber !== null) {
      const key = `${meta.tableNumber}-${meta.chairNumber}`;
      if (!positionMap.has(key)) {
        positionMap.set(key, node);
      }
      const layoutKey = buildLayoutKey(meta.tableNumber, meta.chairNumber);
      if (layoutKey !== undefined && !layoutMap.has(layoutKey)) {
        layoutMap.set(layoutKey, node);
      }
    }
  });
  seatElementsById.value = idMap;
  seatElementsByLayout.value = layoutMap;
  seatElementsByPosition.value = positionMap;
  tableElementsByNumber.value = tableMap;
};

const resolveSeatElement = (seatId: number, tableNumber: number, chairNumber: number) => {
  const layoutKey = buildLayoutKey(tableNumber, chairNumber);
  if (layoutKey != null) {
    const byLayout = seatElementsByLayout.value.get(layoutKey);
    if (byLayout) return byLayout;
  }
  const byPosition = seatElementsByPosition.value.get(`${tableNumber}-${chairNumber}`);
  if (byPosition) return byPosition;
  const byId = seatElementsById.value.get(seatId);
  if (byId) return byId;
  return null;
};

const applySeatColor = (node: SVGElement, seat: Seat) => {
  // Танцпол (tableNumber = 0) не красим, сохраняем оригинальный цвет
  if (seat.tableNumber === 0) {
    // Для танцпола сохраняем оригинальный цвет #E46904, если не выбран
    // Используем !important чтобы переопределить CSS правила
    if (seatStore.selected.has(seat.id)) {
      node.style.setProperty('fill', '#6c757d', 'important');
    } else if (seat.status === 'AVAILABLE') {
      node.style.setProperty('fill', '#E46904', 'important');
    } else {
      node.style.setProperty('fill', '#6c757d', 'important');
    }
    return;
  }
  
  // Если место выбрано, делаем его серым
  if (seatStore.selected.has(seat.id)) {
    node.style.fill = '#6c757d';
    return;
  }
  if (seat.status === 'AVAILABLE') {
    const color = resolveCategoryColor(seat.priceCents, seat.categoryColorHex);
    node.style.fill = color;
  } else if (seat.status === 'SOLD' || seat.status === 'BLOCKED') {
    // Купленные или заблокированные места подсвечиваем тем же серым цветом
    node.style.fill = '#6c757d';
  } else {
    node.style.removeProperty('fill');
  }
};

const updateSeatAppearance = () => {
  if (!seatStore.seats.length) return;
  seatStore.seats.forEach((seat) => {
    const node = resolveSeatElement(seat.id, seat.tableNumber, seat.chairNumber);
    if (!node) return;
    node.classList.remove(
      'seat-available',
      'seat-held',
      'seat-sold',
      'seat-blocked',
      'seat-selected',
      'seat-filtered'
    );
    node.classList.add(`seat-${seat.status.toLowerCase()}`);
    if (seatStore.selected.has(seat.id)) {
      node.classList.add('seat-selected');
    }
    if (seatStore.priceFilter && seat.priceCents !== seatStore.priceFilter) {
      node.classList.add('seat-filtered');
    }
    applySeatColor(node, seat);
  });
  updateDanceFloorInfo();
  updateTableColors();
};

const updateDanceFloorInfo = () => {
  const host = svgHost.value;
  if (!host) return;

  // Находим все элементы танцпола
  const danceFloorElements = host.querySelectorAll('circle[data-table="0"].dance-floor');

  if (danceFloorElements.length === 0) return;

  // Получаем информацию о местах танцпола
  const danceFloorSeats = seatStore.seats.filter(s => s.tableNumber === 0);
  const availableSeats = danceFloorSeats.filter(s => s.status === 'AVAILABLE');
  const totalSeats = danceFloorSeats.length;

  // Получаем цену (берем цену первого доступного места)
  const price = availableSeats.length > 0 ? availableSeats[0].priceCents : 0;

  // Форматируем цену
  const formatPrice = (cents: number) => {
    return new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      maximumFractionDigits: 0
    }).format(cents / 100);
  };

  const priceText = price > 0 ? formatPrice(price) : 'Цена не установлена';

  // Создаем текст для title
  const titleText = `Танцпол · ${priceText} · Доступно: ${availableSeats.length}/${totalSeats} мест`;

  // Обновляем title для всех элементов танцпола
  danceFloorElements.forEach((element) => {
    const svgElement = element as SVGElement;
    svgElement.setAttribute('title', titleText);
  });
};

const updateTableColors = () => {
  if (!seatStore.seats.length) return;
  const seen = new Set<number>();

  seatStore.seats.forEach((seat) => {
    const tableNumber = seat.tableNumber;
    // tableNumber может быть 0 для танцпола, поэтому проверяем !== undefined вместо truthy
    if (tableNumber === undefined || tableNumber === null || seen.has(tableNumber)) return;
    seen.add(tableNumber);

    const node = tableElementsByNumber.value.get(tableNumber);
    if (!node) return;

    const seatsForTable = seatStore.seats.filter((s) => s.tableNumber === tableNumber);
    if (!seatsForTable.length) return;

    const totalSeats = seatsForTable.length;
    const soldOrBlockedCount = seatsForTable.filter(
      (s) => s.status === 'SOLD' || s.status === 'BLOCKED'
    ).length;
    const allSoldOrBlocked = soldOrBlockedCount === totalSeats && totalSeats > 0;

    if (allSoldOrBlocked) {
      // Если все места за столом куплены/заблокированы — делаем стол полностью "погашенным"
      node.setAttribute('fill', '#6c757d');
      node.setAttribute('opacity', '0.6');
      return;
    }

    // Частично занятый стол: гасим его пропорционально доле купленных мест
    const soldFraction = totalSeats > 0 ? soldOrBlockedCount / totalSeats : 0;
    const availableSeat =
      seatsForTable.find((s) => s.status === 'AVAILABLE') ?? seatsForTable[0];
    const color = resolveCategoryColor(availableSeat.priceCents, availableSeat.categoryColorHex);
    node.setAttribute('fill', color);

    // opacity от 1.0 (0% продано) до 0.0 (100% продано, но этот случай уже обработан выше)
    const opacity = 1 - soldFraction;
    node.setAttribute('opacity', opacity.toFixed(2));
  });
};

const findSeatByMeta = (meta: SeatMeta) => {
  // tableNumber может быть 0 для танцпола, поэтому проверяем !== undefined
  if (meta.tableNumber !== undefined && meta.tableNumber !== null && meta.chairNumber !== undefined && meta.chairNumber !== null) {
    return seatStore.seats.find(
      (seat) => seat.tableNumber === meta.tableNumber && seat.chairNumber === meta.chairNumber
    );
  }
  if (meta.seatId) {
    const byId = seatStore.seats.find((seat) => seat.id === meta.seatId);
    if (byId) return byId;
  }
  return null;
};

const scrollIntoView = (element: Element | null | undefined) => {
  if (!element) return;
  const viewportEl = viewport.value;
  if (!viewportEl) {
    element.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'center' });
    return;
  }
  const elementRect = element.getBoundingClientRect();
  const viewportRect = viewportEl.getBoundingClientRect();
  const offsetX = elementRect.left - viewportRect.left - viewportRect.width / 2 + elementRect.width / 2;
  const offsetY = elementRect.top - viewportRect.top - viewportRect.height / 2 + elementRect.height / 2;
  viewportEl.scrollBy({ left: offsetX, top: offsetY, behavior: 'smooth' });
};

const handleSeatClick = (event: Event) => {
  const target = (event.target as HTMLElement)?.closest<HTMLElement>('[data-seat-id],[data-table],[data-chair]');
  const meta = extractSeatMeta(target as Element);
  if (!meta) return;
  
  // Специальная обработка для танцпола (tableNumber = 0)
  if (meta.tableNumber === 0) {
    const danceFloorSeats = seatStore.seats.filter(
      (s) => s.tableNumber === 0 && s.status === 'AVAILABLE'
    );
    
    if (danceFloorSeats.length === 0) {
      blockedMessage.value = 'Танцпол полностью занят. Пожалуйста, выберите другое место.';
      if (blockedTimer !== null) {
        clearTimeout(blockedTimer);
      }
      blockedTimer = window.setTimeout(() => {
        blockedMessage.value = null;
        blockedTimer = null;
      }, 3000);
      return;
    }
    
    // Отправляем событие родительскому компоненту для показа quantity selector
    emit('danceFloorClicked', {
      availableSeats: danceFloorSeats.length,
      price: danceFloorSeats[0].priceCents
    });
    return;
  }
  
  const seat = findSeatByMeta(meta);
  if (!seat) {
    if (meta.tableNumber) {
      const seatsForTable = seatStore.seats.filter(
        (s) => s.tableNumber === meta.tableNumber
      );
      const allSoldOrBlocked =
        seatsForTable.length > 0 &&
        seatsForTable.every((s) => s.status !== 'AVAILABLE');

      // Если весь стол уже занят — показываем такое же всплывающее сообщение
      if (allSoldOrBlocked) {
        blockedMessage.value = 'Этот стол уже полностью занят. Пожалуйста, выберите другой стол.';
        if (blockedTimer !== null) {
          clearTimeout(blockedTimer);
        }
        blockedTimer = window.setTimeout(() => {
          blockedMessage.value = null;
          blockedTimer = null;
        }, 3000);
        return;
      }

      // Иначе просто фокусируем и немного увеличиваем зум
      setZoom(zoom.value * 1.2);
      scrollIntoView(tableElementsByNumber.value.get(meta.tableNumber) ?? target ?? null);
    }
    return;
  }
  // Если место уже занято или недоступно — показываем сообщение и не даём его выбрать
  if (seat.status !== 'AVAILABLE') {
    blockedMessage.value = 'Это место уже занято. Пожалуйста, выберите другое место.';
    if (blockedTimer !== null) {
      clearTimeout(blockedTimer);
    }
    blockedTimer = window.setTimeout(() => {
      blockedMessage.value = null;
      blockedTimer = null;
    }, 3000);
    return;
  }
  // При клике на место (стул) только выбираем, без зума
  scrollIntoView(resolveSeatElement(seat.id, seat.tableNumber, seat.chairNumber));
  seatStore.toggleSeat(seat.id);
};

const loadSchemeFromUrl = async (url: string) => {
  loadingScheme.value = true;
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Failed to load scheme: ${response.statusText}`);
    }
    const svgText = await response.text();
    svgMarkup.value = svgText;
    await nextTick();
    hydrateSvg();
    fitToViewport({ preserveRelative: false });
    centerViewport('auto');
  } catch (error) {
    console.error('Error loading scheme from URL:', error);
    // Fallback to default scheme
    svgMarkup.value = hallSvg;
    await nextTick();
    hydrateSvg();
    fitToViewport({ preserveRelative: false });
    centerViewport('auto');
  } finally {
    loadingScheme.value = false;
  }
};

const hydrateSvg = () => {
  // Находим элемент танцпола и добавляем атрибут data-table="0"
  const host = svgHost.value;
  if (host) {

    // Ищем все кружки с data-table="0" (танцпол)
    const danceFloorCircles = host.querySelectorAll('circle[data-table="0"]');
    danceFloorCircles.forEach((circle) => {
      const circleEl = circle as SVGElement;
      circleEl.classList.add('dance-floor');
      // Принудительно устанавливаем оранжевый цвет для танцпола, если не выбран
      if (!circleEl.classList.contains('seat-selected')) {
        circleEl.style.setProperty('fill', '#E46904', 'important');
      }
    });

    // Ищем элемент танцпола по data-seat-id="7" или по цвету #E46904
    const danceFloorElement = host.querySelector('[data-seat-id="7"]') as SVGElement;
    if (danceFloorElement) {
      danceFloorElement.setAttribute('data-table', '0');
      // Добавляем класс для идентификации танцпола
      danceFloorElement.classList.add('dance-floor');
      if (!danceFloorElement.classList.contains('seat-selected')) {
        danceFloorElement.style.setProperty('fill', '#E46904', 'important');
      }
    }

    // Также ищем по цвету на случай, если data-seat-id другой
    const allCircles = host.querySelectorAll('circle[fill="#E46904"], circle[fill="#e46904"]');
    allCircles.forEach((circle) => {
      if (!circle.hasAttribute('data-table')) {
        const circleEl = circle as SVGElement;
        circleEl.setAttribute('data-table', '0');
        circleEl.classList.add('dance-floor');
        if (!circleEl.classList.contains('seat-selected')) {
          circleEl.style.setProperty('fill', '#E46904', 'important');
        }
      }
    });
  }
  buildSeatIndex();
  updateSeatAppearance();
  updateDanceFloorInfo();
};

onMounted(async () => {
  if (props.schemeUrl) {
    await loadSchemeFromUrl(props.schemeUrl);
  } else {
    await nextTick();
    hydrateSvg();
    fitToViewport({ preserveRelative: false });
    centerViewport('auto');
  }
  
  svgHost.value?.addEventListener('click', handleSeatClick);
  viewportObserver = new ResizeObserver(() => fitToViewport());
  if (viewport.value) {
    viewportObserver.observe(viewport.value);
  }
});

watch(
  () => props.schemeUrl,
  async (newUrl) => {
    if (newUrl) {
      await loadSchemeFromUrl(newUrl);
    } else {
      svgMarkup.value = hallSvg;
      await nextTick();
      hydrateSvg();
      fitToViewport({ preserveRelative: false });
      centerViewport('auto');
    }
  }
);

onBeforeUnmount(() => {
  svgHost.value?.removeEventListener('click', handleSeatClick);
  viewportObserver?.disconnect();
  viewportObserver = null;
});

watch(
  () => ({
    seats: seatStore.seats.map((seat) => ({
      id: seat.id,
      status: seat.status,
      price: seat.priceCents
    })),
    selected: Array.from(seatStore.selected.values()),
    filter: seatStore.priceFilter
  }),
  () => {
    updateSeatAppearance();
    updateDanceFloorInfo();
  },
  { deep: true }
);

watch(
  () => seatStore.seats.length,
  () => {
    nextTick(() => {
      hydrateSvg();
    });
  }
);
</script>


