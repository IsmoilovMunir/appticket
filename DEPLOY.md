# Инструкция по развертыванию проекта

## Подготовка к деплою

### 1. Требования на сервере

- Docker и Docker Compose установлены
- Домен настроен и указывает на IP сервера
- Порты 80, 443 (для HTTPS), 8080 открыты в firewall

### 2. Клонирование репозитория на сервер

```bash
git clone <ваш-репозиторий-url> apptickit
cd apptickit
```

### 3. Настройка переменных окружения

Создайте файл `.env` в корне проекта (или используйте переменные окружения в docker-compose):

```bash
# База данных
DB_URL=jdbc:postgresql://postgres:5432/tickets
DB_USERNAME=ticket
DB_PASSWORD=<надежный-пароль>

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=<случайная-строка-минимум-32-символа>
JWT_TTL_SECONDS=1800

# Админ
ADMIN_DEFAULT_PASSWORD=<пароль-для-первого-админа>

# Telegram
TELEGRAM_BOT_TOKEN=<токен-бота>
TELEGRAM_CHAT_ID=<chat-id-менеджера>

# Email
MAIL_FROM=info@appticket.ru
```

### 4. Обновление docker-compose.yml

Обновите `infrastructure/docker-compose.yml` с переменными окружения:

```yaml
services:
  backend:
    environment:
      DB_URL: ${DB_URL}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: ${REDIS_HOST}
      REDIS_PORT: ${REDIS_PORT}
      JWT_SECRET: ${JWT_SECRET}
      ADMIN_DEFAULT_PASSWORD: ${ADMIN_DEFAULT_PASSWORD}
      TELEGRAM_BOT_TOKEN: ${TELEGRAM_BOT_TOKEN}
      TELEGRAM_CHAT_ID: ${TELEGRAM_CHAT_ID}
      MAIL_FROM: ${MAIL_FROM}
```

### 5. Настройка Nginx для домена

Обновите `infrastructure/nginx.conf` для вашего домена:

```nginx
server {
    listen 80;
    server_name ваш-домен.ru www.ваш-домен.ru;

    # Редирект на HTTPS (если используете SSL)
    # return 301 https://$server_name$request_uri;

    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /ws-seat-status {
        proxy_pass http://backend:8080/ws-seat-status;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location / {
        proxy_pass http://frontend:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        try_files $uri /index.html;
    }
}

# Если используете HTTPS (рекомендуется)
# server {
#     listen 443 ssl http2;
#     server_name ваш-домен.ru www.ваш-домен.ru;
#
#     ssl_certificate /etc/nginx/ssl/cert.pem;
#     ssl_certificate_key /etc/nginx/ssl/key.pem;
#
#     location /api/ {
#         proxy_pass http://backend:8080/api/;
#         proxy_set_header Host $host;
#         proxy_set_header X-Real-IP $remote_addr;
#         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
#         proxy_set_header X-Forwarded-Proto $scheme;
#     }
#
#     location /ws-seat-status {
#         proxy_pass http://backend:8080/ws-seat-status;
#         proxy_http_version 1.1;
#         proxy_set_header Upgrade $http_upgrade;
#         proxy_set_header Connection "Upgrade";
#         proxy_set_header Host $host;
#         proxy_set_header X-Real-IP $remote_addr;
#         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
#     }
#
#     location / {
#         proxy_pass http://frontend:80;
#         proxy_set_header Host $host;
#         proxy_set_header X-Real-IP $remote_addr;
#         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
#     }
# }
```

## Сборка и запуск

### 1. Перейдите в директорию infrastructure

```bash
cd infrastructure
```

### 2. Соберите и запустите контейнеры

```bash
docker compose up -d --build
```

### 3. Проверьте статус контейнеров

```bash
docker compose ps
```

### 4. Просмотрите логи (если нужно)

```bash
# Все сервисы
docker compose logs -f

# Только backend
docker compose logs -f backend

# Только frontend
docker compose logs -f frontend
```

## Обновление проекта

### 1. Остановите контейнеры

```bash
cd infrastructure
docker compose down
```

### 2. Обновите код

```bash
cd ..
git pull origin main  # или ваша ветка
```

### 3. Пересоберите и запустите

```bash
cd infrastructure
docker compose up -d --build
```

## Настройка SSL (HTTPS) с Let's Encrypt

### 1. Установите Certbot

```bash
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx
```

### 2. Получите сертификат

```bash
sudo certbot --nginx -d ваш-домен.ru -d www.ваш-домен.ru
```

### 3. Обновите nginx.conf для использования SSL

См. пример выше в разделе "Настройка Nginx для домена"

## Резервное копирование базы данных

### Создание бэкапа

```bash
docker compose exec postgres pg_dump -U ticket tickets > backup_$(date +%Y%m%d_%H%M%S).sql
```

### Восстановление из бэкапа

```bash
docker compose exec -T postgres psql -U ticket tickets < backup_YYYYMMDD_HHMMSS.sql
```

## Мониторинг

### Проверка здоровья сервисов

```bash
# Backend health check
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost:80
```

## Устранение проблем

### Проблема: Контейнеры не запускаются

```bash
# Проверьте логи
docker compose logs

# Проверьте, не заняты ли порты
sudo netstat -tulpn | grep -E ':(80|443|8080)'
```

### Проблема: База данных не подключается

```bash
# Проверьте, запущен ли postgres
docker compose ps postgres

# Проверьте логи postgres
docker compose logs postgres
```

### Проблема: Frontend не отображается

```bash
# Проверьте, собран ли frontend
docker compose logs frontend

# Пересоберите frontend
docker compose up -d --build frontend
```

## Полезные команды

```bash
# Остановить все контейнеры
docker compose down

# Остановить и удалить volumes (ОСТОРОЖНО: удалит данные БД!)
docker compose down -v

# Перезапустить конкретный сервис
docker compose restart backend

# Просмотр использования ресурсов
docker stats

# Очистка неиспользуемых образов
docker system prune -a
```

