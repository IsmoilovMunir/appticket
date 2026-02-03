# Быстрый старт

## Локальная разработка (без Docker)

### Требования
- Node.js 20+
- Java 21+
- PostgreSQL 16+
- Redis 7+

### Запуск бэкенда

```bash
cd backend

# Убедитесь, что PostgreSQL и Redis запущены локально
# PostgreSQL должен быть доступен на localhost:5432 (или измените DB_URL)
# Redis должен быть доступен на localhost:6379

# Запустите Spring Boot приложение
mvn spring-boot:run

# Бэкенд будет доступен на http://localhost:8080
```

### Запуск фронтенда

```bash
cd frontend

# Установите зависимости
npm install

# Запустите dev сервер
npm run dev

# Фронтенд будет доступен на http://localhost:4173
# Vite автоматически проксирует запросы /api на http://localhost:8080
```

**Важно:** Убедитесь, что бэкенд запущен на порту **8080** (по умолчанию), иначе фронтенд не сможет подключиться.

---

## Деплой через Docker

## Шаг 1: Подготовка на сервере

```bash
# Установите Docker и Docker Compose (если еще не установлены)
sudo apt-get update
sudo apt-get install -y docker.io docker-compose

# Добавьте пользователя в группу docker
sudo usermod -aG docker $USER
newgrp docker
```

## Шаг 2: Клонирование проекта

```bash
git clone <ваш-репозиторий> apptickit
cd apptickit
```

## Шаг 3: Создание .env файла

```bash
# Создайте файл .env в корне проекта
cat > .env << EOF
# База данных
DB_URL=jdbc:postgresql://postgres:5432/tickets
DB_USERNAME=ticket
DB_PASSWORD=ваш_надежный_пароль

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT (минимум 32 символа)
JWT_SECRET=ваш_случайный_секретный_ключ_минимум_32_символа

# Админ пароль
ADMIN_DEFAULT_PASSWORD=пароль_для_первого_админа

# Telegram
TELEGRAM_BOT_TOKEN=ваш_токен_бота
TELEGRAM_CHAT_ID=ваш_chat_id

# Email
MAIL_FROM=info@appticket.ru
EOF
```

## Шаг 4: Настройка домена в nginx.conf

Отредактируйте `infrastructure/nginx.conf` и замените `server_name _;` на ваш домен:

```nginx
server {
    listen 80;
    server_name ваш-домен.ru www.ваш-домен.ru;
    ...
}
```

## Шаг 5: Запуск проекта

```bash
# Вариант 1: Использовать скрипт
./deploy.sh

# Вариант 2: Вручную
cd infrastructure
docker compose up -d --build
```

## Шаг 6: Проверка

```bash
# Проверьте статус контейнеров
cd infrastructure
docker compose ps

# Проверьте логи
docker compose logs -f

# Проверьте доступность
curl http://localhost/actuator/health
```

## Шаг 7: Настройка SSL (опционально, но рекомендуется)

```bash
# Установите Certbot
sudo apt-get install certbot python3-certbot-nginx

# Получите сертификат
sudo certbot --nginx -d ваш-домен.ru -d www.ваш-домен.ru

# Certbot автоматически обновит nginx.conf
```

## Обновление проекта

```bash
# Остановите контейнеры
cd infrastructure
docker compose down

# Обновите код
cd ..
git pull

# Пересоберите и запустите
cd infrastructure
docker compose up -d --build
```

## Полезные команды

```bash
# Просмотр логов
docker compose logs -f [service_name]

# Перезапуск сервиса
docker compose restart [service_name]

# Остановка всех сервисов
docker compose down

# Бэкап базы данных
docker compose exec postgres pg_dump -U ticket tickets > backup.sql
```

