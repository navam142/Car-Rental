# Multi-stage build for Spring Boot application

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install small utilities (curl) and create non-root user for security
# install packages as root, then switch to non-root user
RUN apk add --no-cache curl \
    && addgroup -S spring \
    && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check (use curl which we installed)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
