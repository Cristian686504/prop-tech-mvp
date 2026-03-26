# Multi-stage build for PropTech backend

# Stage 1: Build
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test -x validateStructure

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the executable JAR
COPY --from=build /app/applications/app-service/build/libs/proptech.jar proptech.jar

# Create cache directory and set permissions
RUN mkdir -p /cache && chown -R appuser:appgroup /app /cache

# JVM optimization flags
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Switch to non-root user
USER appuser

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /app/proptech.jar"]
