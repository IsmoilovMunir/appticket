const PRICE_COLOR_FALLBACKS: Record<number, string> = {
  1_000_000: '#d4af37',
  700_000: '#1f9d6c',
  500_000: '#6f42c1'
};

export const STATUS_COLORS: Record<string, string> = {
  AVAILABLE: 'var(--seat-available)',
  HELD: 'var(--seat-held)',
  SOLD: 'var(--seat-sold)',
  BLOCKED: 'var(--seat-blocked)'
};

export const resolveCategoryColor = (priceCents: number, categoryColorHex?: string | null) => {
  if (categoryColorHex && /^#[0-9A-Fa-f]{6}$/.test(categoryColorHex)) {
    return categoryColorHex.toUpperCase();
  }
  return PRICE_COLOR_FALLBACKS[priceCents] ?? 'var(--seat-available)';
};

export const resolveStatusColor = (status: string) => STATUS_COLORS[status] ?? 'var(--seat-blocked)';

export const getPriceLegendFallback = () =>
  Object.entries(PRICE_COLOR_FALLBACKS).map(([price, color]) => ({
    label: new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      maximumFractionDigits: 0
    }).format(Number(price) / 100),
    color
  }));

