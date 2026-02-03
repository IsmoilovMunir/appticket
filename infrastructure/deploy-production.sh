#!/bin/bash
# Деплой на удалённый сервер (production)
set -e

cd "$(dirname "$0")"

echo "🚀 Production Deploy — App Ticket"
echo ""

# Проверка .env
if [ ! -f .env ]; then
    echo "❌ Файл .env не найден!"
    echo "   cp env.example .env"
    echo "   nano .env"
    exit 1
fi

# Загрузка .env
set -a
source .env
set +a

# Проверка обязательных переменных
if [ -z "$DB_PASSWORD" ] || [ "$DB_PASSWORD" = "your_secure_password_here" ]; then
    echo "❌ Установите DB_PASSWORD в .env"
    exit 1
fi
if [ -z "$JWT_SECRET" ] || [ "$JWT_SECRET" = "your_jwt_secret_key_here_min_32_chars" ]; then
    echo "❌ Установите JWT_SECRET в .env (openssl rand -base64 64)"
    exit 1
fi

# Создаём папку ssl если её нет (для nginx)
mkdir -p ssl

echo "📦 Остановка контейнеров..."
docker compose down

echo "🔨 Сборка и запуск (production)..."
NGINX_CONF=nginx.conf.prod SPRING_PROFILES_ACTIVE=prod docker compose up -d --build

echo "⏳ Ожидание запуска (15 сек)..."
sleep 15

echo ""
echo "✅ Деплой завершён!"
echo ""
echo "📊 Проверка:"
docker compose ps
echo ""
echo "🌐 Сайт: http://apptickit.ru (или ваш домен)"
echo "📝 Логи: docker compose logs -f"
echo ""
echo "🔒 Для HTTPS: получите сертификат Let's Encrypt и переключите на nginx.conf"
echo "   certbot certonly --standalone -d apptickit.ru"
echo "   cp cert.pem key.pem infrastructure/ssl/"
echo "   NGINX_CONF=nginx.conf docker compose up -d"
echo ""
