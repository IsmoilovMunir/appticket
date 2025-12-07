#!/bin/bash

# Скрипт для быстрого деплоя проекта

set -e

echo "🚀 Начало деплоя проекта..."

# Проверка наличия .env файла
if [ ! -f .env ]; then
    echo "⚠️  Файл .env не найден!"
    echo "📝 Создайте файл .env на основе .env.example"
    echo "   cp .env.example .env"
    echo "   Затем отредактируйте .env и заполните все переменные"
    exit 1
fi

# Переход в директорию infrastructure
cd infrastructure || exit 1

echo "📦 Остановка существующих контейнеров..."
docker compose down

echo "🔨 Сборка и запуск контейнеров..."
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

