# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /project

# Copy project files
COPY recommendation-service ./recommendation-service

WORKDIR /project/recommendation-service

# Build the service
RUN gradle jar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /project/recommendation-service/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]