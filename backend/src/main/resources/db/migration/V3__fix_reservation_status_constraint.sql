-- Drop the existing constraint if it exists (may not include PARTIALLY_CONFIRMED)
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_status_check;

-- Recreate the constraint with all valid status values including PARTIALLY_CONFIRMED
ALTER TABLE reservations
ADD CONSTRAINT reservations_status_check
CHECK (status IN (
    'HELD',
    'PARTIALLY_CONFIRMED',
    'PARTIALLY_CANCELLED',
    'CONFIRMED',
    'CANCELLED',
    'EXPIRED'
));

