# Concert Ticketing Platform

Веб‑платформа для продажи билетов на концерт с выбором мест по SVG‑схеме, резервированием на 30 минут, интеграцией с Telegram, админ‑панелью и системой проверки QR‑кодов.

## Стек

- **Frontend:** Vue 3 (Composition API), Vite, Pinia, Bootstrap 5, STOMP/WebSocket.
- **Backend:** Spring Boot 3, Spring Data JPA, Spring Security (JWT), Redis (hold мест), PostgreSQL, WebSocket/STOMP.
- **Интеграции:** Telegram Bot API (уведомления + inline подтверждения), QR‑коды для билетов, check‑in панель.
- **Инфра:** Docker, docker-compose (frontend + backend + Postgres + Redis + Nginx).

## Структура репозитория

```
backend/   — Spring Boot приложение
frontend/  — Vue SPA
infrastructure/
  ├── docker-compose.yml
  ├── backend/Dockerfile
  └── frontend/Dockerfile
```

## Быстрый старт (локально)

1. Установите Node 20+, pnpm/npm и JDK 21.
2. Скопируйте `.env.example` в `.env` для фронтенда и `backend/.env.example` (переменные DB, Redis, JWT, Telegram).
3. Соберите и запустите:
   ```bash
   cd infrastructure
   docker compose up --build
   ```
4. Frontend доступен на `http://localhost:4173`, backend — `http://localhost:8080`.

## Основные возможности

- SVG‑схема зала с 500 местами и статусами (`AVAILABLE/HELD/SOLD/BLOCKED`), выбор до 10 мест.
- Резервы в PostgreSQL + Redis (hold на 30 минут), уведомления в Telegram и realtime обновления через WebSocket/STOMP.
- Админ‑панель: просмотр резервов, подтверждение/отмена, ручные блокировки мест, логирование действий.
- Генерация QR‑кодов для билетов, web‑интерфейс check‑in с фиксацией дублей.

## API (кратко)

- `GET /api/concerts/{id}` — данные концерта.
- `GET /api/concerts/{id}/seats` — статусы мест.
- `POST /api/reservations` — создать резерв (до 10 мест).
- `POST /api/admin/reservations/{id}/confirm|cancel` — менеджер подтверждает/отменяет.
- `POST /api/telegram/webhook` — callback от Telegram.
- `POST /api/checkin` — проверка билета QR.
- `POST /api/auth/login` — JWT для админки.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`.

## Дальнейшая работа

- Подключить реальные SVG/контент заказчика.
- Настроить Telegram bot token и webhook URL.
- Добавить платёжный шлюз и уведомления (email/SMS), если требуется.

