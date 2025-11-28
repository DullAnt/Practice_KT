# Course Recommendation Platform

Микросервисная платформа рекомендаций курсов на Java и Kotlin с использованием Spring Boot, Ktor, Apache Kafka и PostgreSQL.

## 📸 Демонстрация работы

<img width="1818" height="825" alt="image" src="https://github.com/user-attachments/assets/76d464b3-ce7f-443a-8434-fed8d333eaf5" />


## 📋 Описание проекта

Платформа позволяет:
- Регистрировать и авторизовывать пользователей (JWT)
- Управлять каталогом курсов
- Оценивать курсы
- Получать персонализированные рекомендации на основе оценок

---

## 🏗️ Архитектура
```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   User      │     │   Course    │     │   Rating    │
│   Service   │     │   Service   │     │   Service   │
│  (8085)     │     │  (8082)     │     │  (8083)     │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
       │                   │                   │ Kafka
       │                   │                   ▼
       │                   │           ┌─────────────┐
       │                   │           │   Kafka     │
       │                   │           │  (ratings)  │
       │                   │           └──────┬──────┘
       │                   │                  │
       ▼                   ▼                  ▼
┌─────────────────────────────────────────────────────┐
│                    PostgreSQL                        │
│  userdb  │  coursedb  │  ratingdb  │ recommendationdb│
└─────────────────────────────────────────────────────┘
                                              │
                                              ▼
                                    ┌─────────────────┐
                                    │ Recommendation  │
                                    │    Service      │
                                    │    (8084)       │
                                    └─────────────────┘
```

<img width="1507" height="464" alt="image" src="https://github.com/user-attachments/assets/59755ca9-3a67-4ba0-b8ae-cbefde6ec4cb" />


## 🛠️ Технологии

| Компонент | Технологии |
|-----------|------------|
| **User Service** | Java 17, Spring Boot 3.2, Spring Security, JWT |
| **Course Service** | Java 17, Spring Boot 3.2, Spring Data JPA |
| **Rating Service** | Java 17, Spring Boot 3.2, Apache Kafka |
| **Recommendation Service** | Kotlin 1.9, Ktor 2.3, Exposed ORM |
| **База данных** | PostgreSQL 15 |
| **Очередь сообщений** | Apache Kafka |
| **Контейнеризация** | Docker, Docker Compose |
| **Сборка** | Gradle 8.5 (Kotlin DSL) |

---

## 🚀 Быстрый старт

### Требования
- Docker Desktop
- PowerShell или терминал

### Запуск
```bash
# Клонировать/распаковать проект
cd course-recommendation-platform

# Запустить все сервисы
docker-compose up -d --build

# Подождать 2-3 минуты для запуска всех сервисов
```

### Проверка статуса
```bash
docker ps
```
<img width="1470" height="602" alt="image" src="https://github.com/user-attachments/assets/aca3bde7-7873-4ee9-97bf-c1c42f7e71da" />


## 📡 API Endpoints

### User Service (порт 8085)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/auth/register` | Регистрация пользователя |
| POST | `/api/auth/login` | Авторизация (получение JWT) |
| GET | `/api/users/{id}` | Получить пользователя по ID |
| GET | `/actuator/health` | Health check |

### Course Service (порт 8082)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/courses` | Получить все курсы |
| GET | `/api/courses/{id}` | Получить курс по ID |
| POST | `/api/courses` | Создать курс |
| PUT | `/api/courses/{id}` | Обновить курс |
| DELETE | `/api/courses/{id}` | Удалить курс |
| GET | `/api/courses/category/{category}` | Курсы по категории |
| GET | `/api/courses/search?keyword=` | Поиск курсов |

### Rating Service (порт 8083)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/ratings` | Создать оценку (отправляется в Kafka) |
| GET | `/api/ratings/user/{userId}` | Оценки пользователя |
| GET | `/api/ratings/course/{courseId}` | Оценки курса |
| GET | `/api/ratings/course/{courseId}/average` | Средний рейтинг курса |

### Recommendation Service (порт 8084)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/recommendations/{userId}` | Получить рекомендации |
| POST | `/api/recommendations/sync-courses` | Синхронизировать курсы |
| GET | `/health` | Health check |

---

## 🧪 Тестирование API

### 1. Регистрация пользователя
```powershell
Invoke-RestMethod -Uri "http://localhost:8085/api/auth/register" -Method Post -ContentType "application/json" -Body '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

### 2. Создание курсов
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/courses" -Method Post -ContentType "application/json" -Body '{"title":"Java Programming","description":"Learn Java from scratch","category":"Programming","instructor":"John Doe"}'

Invoke-RestMethod -Uri "http://localhost:8082/api/courses" -Method Post -ContentType "application/json" -Body '{"title":"Python Basics","description":"Python for beginners","category":"Programming","instructor":"Jane Smith"}'

Invoke-RestMethod -Uri "http://localhost:8082/api/courses" -Method Post -ContentType "application/json" -Body '{"title":"Machine Learning","description":"ML fundamentals","category":"Data Science","instructor":"Mike Brown"}'
```

### 3. Получение списка курсов
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/courses" -Method Get
```

<img width="566" height="207" alt="image" src="https://github.com/user-attachments/assets/870bdf55-22f7-4f09-8540-06ba68bb6351" />

### 4. Оценка курса
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/ratings" -Method Post -ContentType "application/json" -Body '{"userId":1,"courseId":1,"rating":5,"comment":"Excellent course!"}'

Invoke-RestMethod -Uri "http://localhost:8083/api/ratings" -Method Post -ContentType "application/json" -Body '{"userId":1,"courseId":2,"rating":4,"comment":"Good content"}'
```

<img width="1821" height="753" alt="image" src="https://github.com/user-attachments/assets/f81431dd-f4d3-4ac1-93c8-912faccd0e64" />

### 5. Проверка Kafka

<img width="809" height="111" alt="image" src="https://github.com/user-attachments/assets/9f302399-1cd0-43bd-8c94-6d5e0715bfa8" />

### 6. Получение рекомендаций
```powershell
Invoke-RestMethod -Uri "http://localhost:8084/api/recommendations/1" -Method Get
```

<img width="1788" height="465" alt="image" src="https://github.com/user-attachments/assets/94895b2b-2cf9-4199-b02a-755a18b3cb41" />


---

## 📊 Мониторинг

### Kafka UI

Доступен по адресу: http://localhost:8090

Позволяет просматривать:
- Топики
- Сообщения
- Consumer Groups
- Брокеры

<img width="1453" height="679" alt="image" src="https://github.com/user-attachments/assets/1d7f1d84-ae55-43b3-b6c1-0979ee369eec" />

### Health Checks
```powershell
# User Service
Invoke-RestMethod -Uri "http://localhost:8085/actuator/health"

# Course Service
Invoke-RestMethod -Uri "http://localhost:8082/actuator/health"

# Rating Service
Invoke-RestMethod -Uri "http://localhost:8083/actuator/health"

# Recommendation Service
Invoke-RestMethod -Uri "http://localhost:8084/health"
```

![Uploading image.png…]()

---

## 📁 Структура проекта
```
course-recommendation-platform/
├── common/                          # Общие DTO классы
│   └── src/main/java/.../dto/
├── user-service/                    # Сервис пользователей
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── security/
│   │   └── config/
│   └── Dockerfile
├── course-service/                  # Сервис курсов
│   ├── src/main/java/.../
│   └── Dockerfile
├── rating-service/                  # Сервис оценок
│   ├── src/main/java/.../
│   │   └── kafka/                   # Kafka Producer
│   └── Dockerfile
├── recommendation-service/          # Сервис рекомендаций (Kotlin)
│   ├── src/main/kotlin/.../
│   │   ├── kafka/                   # Kafka Consumer
│   │   ├── repository/
│   │   ├── service/
│   │   └── routes/
│   └── Dockerfile
├── docker-compose.yml
├── init-db.sql
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🔄 Алгоритм рекомендаций

Recommendation Service использует две стратегии:

1. **Category-based** — рекомендует курсы из категорий, которые пользователь оценил высоко (≥4)

2. **Collaborative filtering** — находит пользователей с похожими интересами и рекомендует курсы, которые им понравились


---

## 🛑 Остановка проекта
```bash
# Остановить все контейнеры
docker-compose down

# Остановить и удалить volumes (данные БД)
docker-compose down -v
```

---

## ⚙️ Порты сервисов

| Сервис | Порт |
|--------|------|
| User Service | 8085 |
| Course Service | 8082 |
| Rating Service | 8083 |
| Recommendation Service | 8084 |
| PostgreSQL | 5432 |
| Kafka | 9092 |
| Zookeeper | 2181 |
| Kafka UI | 8090 |

---

## 📝 Логи
```bash
# Все сервисы
docker-compose logs

# Конкретный сервис
docker-compose logs user-service
docker-compose logs rating-service
docker-compose logs recommendation-service

# В реальном времени
docker-compose logs -f recommendation-service
```

