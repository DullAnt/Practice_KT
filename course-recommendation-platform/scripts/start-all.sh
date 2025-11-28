#!/bin/bash

# Запуск всех сервисов через Docker Compose

echo "Starting all services..."

docker-compose up -d

echo "Waiting for services to start..."
sleep 30

echo ""
echo "All services are starting!"
echo ""
echo "Services:"
echo "  - User Service: http://localhost:8081"
echo "  - Course Service: http://localhost:8082"
echo "  - Rating Service: http://localhost:8083"
echo "  - Recommendation Service: http://localhost:8084"
echo "  - Kafka UI: http://localhost:8090"
echo ""
echo "To check service health:"
echo "  curl http://localhost:8081/actuator/health"
echo "  curl http://localhost:8082/actuator/health"
echo "  curl http://localhost:8083/actuator/health"
echo "  curl http://localhost:8084/health"
echo ""
echo "To view logs: docker-compose logs -f [service-name]"
