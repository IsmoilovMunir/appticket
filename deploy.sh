#!/bin/bash

# Скрипт для быстрого деплоя проекта
# Локально: использует nginx.conf.local
# Production: используйте infrastructure/deploy-production.sh

set -e

echo "🚀 Начало деплоя проекта..."

# Проверка доступности Docker
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker daemon не запущен!"
    echo "📝 Пожалуйста, запустите Docker Desktop и попробуйте снова"
    exit 1
fi

# Переход в директорию infrastructure
cd "$(dirname "$0")/infrastructure" || exit 1

# Проверка .env (для production — в infrastructure)
if [ ! -f .env ]; then
    echo "⚠️  Файл .env не найден в infrastructure/"
    echo "   cp env.example .env"
    echo "   Отредактируйте .env (обязательно: DB_PASSWORD, JWT_SECRET)"
    echo ""
    echo "   Для production: ./infrastructure/deploy-production.sh"
    exit 1
fi

echo "📦 Остановка существующих контейнеров..."
docker compose down

echo "🔨 Сборка и запуск контейнеров (локальный режим)..."
docker compose up -d --build

echo "⏳ Ожидание запуска сервисов..."
sleep 10

echo "✅ Проверка статуса контейнеров..."
docker compose ps

echo ""
echo "🎉 Деплой завершен!"
echo ""
echo "📊 Статус сервисов:"
echo "   - Frontend: http://localhost"
echo "   - Backend API: http://localhost/api"
echo "   - Health check: http://localhost:8080/actuator/health"
echo ""
echo "📝 Полезные команды:"
echo "   - Просмотр логов: docker compose logs -f"
echo "   - Остановка: docker compose down"
echo "   - Перезапуск: docker compose restart [service_name]"
echo ""

