#!/bin/bash

# Запуск только инфраструктуры (PostgreSQL, Kafka, Zookeeper)

echo "Starting infrastructure services..."

docker-compose up -d postgres zookeeper kafka kafka-ui

echo "Waiting for PostgreSQL to be ready..."
sleep 10

echo "Waiting for Kafka to be ready..."
sleep 15

echo "Infrastructure is ready!"
echo ""
echo "Services:"
echo "  - PostgreSQL: localhost:5432"
echo "  - Kafka: localhost:9092"
echo "  - Kafka UI: http://localhost:8090"
echo ""
echo "To start application services, run: ./scripts/start-services.sh"
