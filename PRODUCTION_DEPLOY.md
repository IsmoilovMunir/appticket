# Деплой на удалённый сервер (Production)

## Требования

- Docker и Docker Compose
- Домен (например apptickit.ru), указывающий на IP сервера
- Порты 80, 443 (для HTTPS), 8080 открыты в firewall

---

## Шаг 1: Подключение к серверу

```bash
ssh user@ваш-сервер-ip
```

---

## Шаг 2: Установка Docker (если ещё не установлен)

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo usermod -aG docker $USER
# Перелогиньтесь или: newgrp docker
```

---

## Шаг 3: Клонирование репозитория

```bash
git clone <ваш-репозиторий-url> apptickit
cd apptickit/infrastructure
```

---

## Шаг 4: Настройка переменных окружения

```bash
cp env.example .env
nano .env
```

**Обязательно заполните:**

| Переменная | Описание |
|------------|----------|
| `DB_PASSWORD` | Надёжный пароль PostgreSQL (минимум 16 символов) |
| `JWT_SECRET` | Случайная строка: `openssl rand -base64 64` |
| `ADMIN_DEFAULT_PASSWORD` | Пароль первого админа |
| `TELEGRAM_BOT_TOKEN` | Токен бота @appticketbot |
| `TELEGRAM_CHAT_ID` | Ваш Chat ID (напишите боту /start) |
| `MAIL_PASSWORD` | Пароль от почты info@appticket.ru |

**Рекомендуется:**

```bash
# Сгенерировать JWT_SECRET
openssl rand -base64 64
```

---

## Шаг 5: Деплой

```bash
chmod +x deploy-production.sh
./deploy-production.sh
```

Скрипт:
- Проверит .env
- Соберёт и запустит контейнеры
- Использует `nginx.conf.prod` (HTTP только)

---

## Шаг 6: Проверка

```bash
# Статус контейнеров
docker compose ps

# Логи backend
docker compose logs -f backend

# Health check
curl http://localhost:8080/api/health
```

Откройте в браузере: `http://apptickit.ru` (или IP сервера)

---

## Шаг 7: Настройка HTTPS (Let's Encrypt)

### 7.1 Временно остановите nginx

```bash
docker compose stop nginx
```

### 7.2 Получите сертификат

```bash
sudo apt install certbot
sudo certbot certonly --standalone -d apptickit.ru -d www.apptickit.ru
```

Сертификаты будут в `/etc/letsencrypt/live/apptickit.ru/`

### 7.3 Скопируйте сертификаты

```bash
sudo mkdir -p infrastructure/ssl
sudo cp /etc/letsencrypt/live/apptickit.ru/fullchain.pem infrastructure/ssl/cert.pem
sudo cp /etc/letsencrypt/live/apptickit.ru/privkey.pem infrastructure/ssl/key.pem
sudo chown -R $USER:$USER infrastructure/ssl
```

### 7.4 Переключитесь на HTTPS

```bash
cd infrastructure
NGINX_CONF=nginx.conf docker compose up -d nginx
```

### 7.5 Автообновление сертификата (cron)

```bash
sudo crontab -e
# Добавьте:
0 3 * * * certbot renew --quiet && cp /etc/letsencrypt/live/apptickit.ru/fullchain.pem /path/to/apptickit/infrastructure/ssl/cert.pem && cp /etc/letsencrypt/live/apptickit.ru/privkey.pem /path/to/apptickit/infrastructure/ssl/key.pem && cd /path/to/apptickit/infrastructure && docker compose exec nginx nginx -s reload
```

---

## Шаг 8: Telegram Webhook

После деплоя установите webhook для бота:

```
https://api.telegram.org/bot<BOT_TOKEN>/setWebhook?url=https://apptickit.ru/api/telegram/webhook
```

---

## Обновление проекта

```bash
cd apptickit
git pull origin main
cd infrastructure
./deploy-production.sh
```

---

## Резервное копирование БД

```bash
docker compose exec postgres pg_dump -U ticket tickets > backup_$(date +%Y%m%d).sql
```

---

## Полезные команды

```bash
# Логи
docker compose logs -f

# Перезапуск backend
docker compose restart backend

# Остановка
docker compose down
```
