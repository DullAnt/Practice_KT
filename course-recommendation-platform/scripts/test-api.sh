#!/bin/bash

# Примеры API запросов для тестирования платформы
# Убедитесь, что все сервисы запущены перед выполнением

BASE_URL_USER="http://localhost:8081"
BASE_URL_COURSE="http://localhost:8082"
BASE_URL_RATING="http://localhost:8083"
BASE_URL_RECOMMENDATION="http://localhost:8084"

echo "=== Course Recommendation Platform API Examples ==="
echo ""

# 1. Регистрация пользователя
echo "1. Registering a new user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL_USER/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "password123"
  }')
echo "Response: $REGISTER_RESPONSE"
echo ""

# Извлечение токена
TOKEN=$(echo $REGISTER_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "JWT Token: $TOKEN"
echo ""

# 2. Вход пользователя
echo "2. User login..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL_USER/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')
echo "Response: $LOGIN_RESPONSE"
echo ""

# 3. Создание курсов
echo "3. Creating courses..."

curl -s -X POST "$BASE_URL_COURSE/api/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Java Programming Masterclass",
    "description": "Complete Java course from beginner to advanced",
    "category": "Programming",
    "instructor": "John Doe"
  }'
echo ""

curl -s -X POST "$BASE_URL_COURSE/api/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Python for Data Science",
    "description": "Learn Python for data analysis and machine learning",
    "category": "Data Science",
    "instructor": "Jane Smith"
  }'
echo ""

curl -s -X POST "$BASE_URL_COURSE/api/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Boot Microservices",
    "description": "Build microservices with Spring Boot",
    "category": "Programming",
    "instructor": "Mike Johnson"
  }'
echo ""

curl -s -X POST "$BASE_URL_COURSE/api/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Kotlin for Android",
    "description": "Android app development with Kotlin",
    "category": "Mobile Development",
    "instructor": "Sarah Wilson"
  }'
echo ""

curl -s -X POST "$BASE_URL_COURSE/api/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Machine Learning Fundamentals",
    "description": "Introduction to machine learning algorithms",
    "category": "Data Science",
    "instructor": "David Brown"
  }'
echo ""

echo "Courses created!"
echo ""

# 4. Получение всех курсов
echo "4. Getting all courses..."
curl -s -X GET "$BASE_URL_COURSE/api/courses" | python3 -m json.tool 2>/dev/null || curl -s -X GET "$BASE_URL_COURSE/api/courses"
echo ""

# 5. Поиск курсов
echo "5. Searching courses by keyword 'Java'..."
curl -s -X GET "$BASE_URL_COURSE/api/courses/search?keyword=Java"
echo ""

# 6. Получение курсов по категории
echo "6. Getting courses by category 'Programming'..."
curl -s -X GET "$BASE_URL_COURSE/api/courses/category/Programming"
echo ""

# 7. Оценка курса (отправка в Kafka)
echo "7. Rating a course (userId=1, courseId=1, rating=5)..."
curl -s -X POST "$BASE_URL_RATING/api/ratings" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "courseId": 1,
    "rating": 5,
    "comment": "Excellent course! Highly recommended."
  }'
echo ""

echo "8. Rating another course (userId=1, courseId=3, rating=4)..."
curl -s -X POST "$BASE_URL_RATING/api/ratings" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "courseId": 3,
    "rating": 4,
    "comment": "Good content on microservices."
  }'
echo ""

# 8. Получение оценок пользователя
echo "9. Getting ratings by user..."
curl -s -X GET "$BASE_URL_RATING/api/ratings/user/1"
echo ""

# 9. Получение рекомендаций
echo "10. Getting recommendations for user 1..."
sleep 2  # Подождём пока Kafka обработает сообщения
curl -s -X GET "$BASE_URL_RECOMMENDATION/api/recommendations/1"
echo ""

# 10. Синхронизация курсов с recommendation service
echo "11. Syncing courses to recommendation service..."
curl -s -X POST "$BASE_URL_RECOMMENDATION/api/recommendations/sync-courses"
echo ""

# 11. Пересчёт рекомендаций
echo "12. Recalculating recommendations for user 1..."
curl -s -X POST "$BASE_URL_RECOMMENDATION/api/recommendations/1/recalculate"
echo ""

# 12. Получение предпочтений пользователя
echo "13. Getting user preferences..."
curl -s -X GET "$BASE_URL_RECOMMENDATION/api/recommendations/1/preferences"
echo ""

# 13. Health check всех сервисов
echo ""
echo "=== Health Check ==="
echo "User Service: $(curl -s "$BASE_URL_USER/actuator/health" | grep -o '"status":"[^"]*')"
echo "Course Service: $(curl -s "$BASE_URL_COURSE/actuator/health" | grep -o '"status":"[^"]*')"
echo "Rating Service: $(curl -s "$BASE_URL_RATING/actuator/health" | grep -o '"status":"[^"]*')"
echo "Recommendation Service: $(curl -s "$BASE_URL_RECOMMENDATION/health")"
echo ""

echo "=== API Testing Complete ==="
