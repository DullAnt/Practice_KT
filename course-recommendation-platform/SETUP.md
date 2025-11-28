# Инструкция по настройке проекта

## Получение Gradle Wrapper

Для работы проекта необходим файл `gradle-wrapper.jar`. Есть несколько способов его получить:

### Способ 1: Использование системного Gradle

Если у вас установлен Gradle глобально:

```bash
cd course-recommendation-platform
gradle wrapper --gradle-version 8.5
```

### Способ 2: Скачивание вручную

1. Скачайте Gradle 8.5 с официального сайта:
   https://gradle.org/releases/

2. Распакуйте архив и скопируйте файл `gradle-wrapper.jar` из папки `lib` в `gradle/wrapper/`

### Способ 3: Через Docker

```bash
docker run --rm -v $(pwd):/project -w /project gradle:8.5-jdk17 gradle wrapper
```

## Проверка установки

После получения wrapper выполните:

```bash
./gradlew --version
```

Вы должны увидеть информацию о версии Gradle 8.5.

## Сборка проекта

```bash
# Сборка всех модулей
./gradlew build

# Сборка без тестов
./gradlew build -x test
```

## Запуск через Docker Compose

Если вы используете Docker, то Gradle не нужен для запуска:

```bash
docker-compose up -d --build
```

Docker сам соберёт проекты используя образ gradle.
