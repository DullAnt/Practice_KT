# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /project

# Copy all project files
COPY build.gradle.kts settings.gradle.kts ./
COPY common ./common
COPY rating-service ./rating-service

# Build the service
RUN gradle :rating-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /project/rating-service/build/libs/rating-service.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]