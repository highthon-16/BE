# Multi-stage build for Spring Boot application
FROM gradle:8.11-jdk21 AS build

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src src

RUN gradle build --no-daemon -x test

FROM openjdk:21-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
