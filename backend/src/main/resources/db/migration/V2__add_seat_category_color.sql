-- Добавляем колонку color_hex только если её нет
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'seat_categories' AND column_name = 'color_hex'
    ) THEN
        ALTER TABLE seat_categories ADD COLUMN color_hex VARCHAR(7);
    END IF;
END $$;

-- Обновляем значения цвета для категорий
UPDATE seat_categories
SET color_hex = '#d4af37'
WHERE LOWER(name) LIKE 'golden%' AND (color_hex IS NULL OR color_hex != '#d4af37');

UPDATE seat_categories
SET color_hex = '#1f9d6c'
WHERE LOWER(name) LIKE 'silver%' AND (color_hex IS NULL OR color_hex != '#1f9d6c');

UPDATE seat_categories
SET color_hex = '#6f42c1'
WHERE LOWER(name) LIKE 'bronze%' AND (color_hex IS NULL OR color_hex != '#6f42c1');

UPDATE seat_categories
SET color_hex = '#1f9d6c'
WHERE color_hex IS NULL;

