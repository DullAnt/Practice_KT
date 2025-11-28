#!/bin/bash

# Запуск всех тестов

echo "Running tests for all services..."
echo ""

echo "=== Testing User Service ==="
cd user-service
../gradlew test --info
cd ..

echo ""
echo "=== Testing Course Service ==="
cd course-service
../gradlew test --info
cd ..

echo ""
echo "=== Testing Rating Service ==="
cd rating-service
../gradlew test --info
cd ..

echo ""
echo "=== Testing Recommendation Service ==="
cd recommendation-service
../gradlew test --info
cd ..

echo ""
echo "All tests completed!"
