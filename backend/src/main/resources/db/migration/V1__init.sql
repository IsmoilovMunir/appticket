CREATE TABLE concerts (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    concert_date    TIMESTAMPTZ NOT NULL,
    venue           VARCHAR(255),
    poster_url      TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE seat_categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(64) NOT NULL UNIQUE,
    price_cents INTEGER NOT NULL,
    description TEXT
);

CREATE TABLE seats (
    id              BIGSERIAL PRIMARY KEY,
    concert_id      BIGINT NOT NULL REFERENCES concerts(id) ON DELETE CASCADE,
    table_number    INTEGER NOT NULL,
    chair_number    INTEGER NOT NULL,
    category_id     BIGINT NOT NULL REFERENCES seat_categories(id),
    status          VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    blocked_reason  TEXT,
    UNIQUE (concert_id, table_number, chair_number)
);

CREATE TABLE reservations (
    id                  UUID PRIMARY KEY,
    concert_id          BIGINT NOT NULL REFERENCES concerts(id) ON DELETE CASCADE,
    buyer_name          VARCHAR(255),
    buyer_phone         VARCHAR(64),
    buyer_email         VARCHAR(255),
    status              VARCHAR(32) NOT NULL,
    expires_at          TIMESTAMPTZ NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT NOW(),
    telegram_message_id BIGINT,
    created_by          VARCHAR(255)
);

CREATE TABLE reservation_seats (
    reservation_id  UUID REFERENCES reservations(id) ON DELETE CASCADE,
    seat_id         BIGINT REFERENCES seats(id) ON DELETE CASCADE,
    PRIMARY KEY (reservation_id, seat_id)
);

CREATE TABLE tickets (
    id              UUID PRIMARY KEY,
    reservation_id  UUID REFERENCES reservations(id) ON DELETE SET NULL,
    seat_id         BIGINT NOT NULL REFERENCES seats(id),
    buyer_name      VARCHAR(255),
    buyer_phone     VARCHAR(64),
    ticket_token    VARCHAR(128) UNIQUE NOT NULL,
    qr_code_url     TEXT,
    status          VARCHAR(32) NOT NULL,
    issued_at       TIMESTAMPTZ NOT NULL,
    checked_in_at   TIMESTAMPTZ,
    checked_in_by   VARCHAR(255)
);

CREATE TABLE admin_users (
    id              UUID PRIMARY KEY,
    username        VARCHAR(128) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE admin_users_roles (
    admin_user_id   UUID REFERENCES admin_users(id) ON DELETE CASCADE,
    role            VARCHAR(32) NOT NULL
);

CREATE TABLE telegram_logs (
    id              BIGSERIAL PRIMARY KEY,
    direction       VARCHAR(16) NOT NULL,
    payload         JSONB,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE checkin_logs (
    id              BIGSERIAL PRIMARY KEY,
    ticket_id       UUID REFERENCES tickets(id),
    operator        VARCHAR(255),
    result          VARCHAR(32) NOT NULL,
    metadata        JSONB,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

