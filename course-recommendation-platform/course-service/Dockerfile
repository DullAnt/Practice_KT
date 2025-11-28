# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /project

# Copy all project files
COPY build.gradle.kts settings.gradle.kts ./
COPY common ./common
COPY course-service ./course-service

# Build the service
RUN gradle :course-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /project/course-service/build/libs/course-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]