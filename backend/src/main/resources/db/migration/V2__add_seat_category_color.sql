ALTER TABLE seat_categories
    ADD COLUMN color_hex VARCHAR(7);

UPDATE seat_categories
SET color_hex = '#d4af37'
WHERE LOWER(name) LIKE 'golden%';

UPDATE seat_categories
SET color_hex = '#1f9d6c'
WHERE LOWER(name) LIKE 'silver%';

UPDATE seat_categories
SET color_hex = '#6f42c1'
WHERE LOWER(name) LIKE 'bronze%';

UPDATE seat_categories
SET color_hex = '#1f9d6c'
WHERE color_hex IS NULL;

