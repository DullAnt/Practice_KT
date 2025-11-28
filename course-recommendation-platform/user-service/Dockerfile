# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /project

# Copy all project files
COPY build.gradle.kts settings.gradle.kts ./
COPY common ./common
COPY user-service ./user-service

# Build the service
RUN gradle :user-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /project/user-service/build/libs/user-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]