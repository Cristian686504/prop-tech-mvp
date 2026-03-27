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

# Create uploads and cache directories with proper permissions
RUN mkdir -p /app/uploads /cache && chown -R appuser:appgroup /app /cache

# Install su-exec for better user switching
RUN apk add --no-cache su-exec

# Copy entrypoint script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# JVM optimization flags
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

# Run as root in entrypoint, then switch to appuser
ENTRYPOINT ["docker-entrypoint.sh"]
