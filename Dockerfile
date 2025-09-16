# Multi-stage build for optimal image size and security

# Stage 1: Build the application
# Using Eclipse Temurin (OpenJDK) version 21 as base image for building
FROM eclipse-temurin:21-jdk AS builder

# Set working directory inside container
WORKDIR /app

# Copy Maven files first (for better layer caching)
# These files define your project dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make Maven wrapper executable and download dependencies
# This step is crucial - Maven reads your pom.xml to know what dependencies to download
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Copy source code after dependencies are downloaded
# This leverages Docker layer caching - if only source changes, 
# we don't need to redownload dependencies
COPY src src

# Build the application JAR file
# Maven uses the information in pom.xml to compile your code with all dependencies
RUN ./mvnw package -DskipTests

# Stage 2: Runtime image (smaller footprint)
# Using just the JRE since we don't need JDK for running
FROM eclipse-temurin:21-jre

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR file from builder stage
# The JAR already contains all your dependencies packaged inside it
COPY --from=builder --chown=appuser:appuser /app/target/*.jar app.jar

# Expose port (default Spring Boot port)
EXPOSE 8080

# Switch to non-root user
USER appuser

# Health check to verify application is running
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
# Java will automatically include all dependencies from the fat JAR
ENTRYPOINT ["java", "-jar", "app.jar"]