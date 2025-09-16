# Docker Setup for Spring Boot Application

This project includes Docker configuration to run the Spring Boot application in a containerized environment.

## How Docker Handles Dependencies

Docker doesn't directly know about your dependencies. Instead, the process works like this:

1. Docker builds an image using the Dockerfile
2. During the build, Maven reads your `pom.xml` file to understand your dependencies
3. Maven downloads all dependencies listed in `pom.xml` from Maven Central
4. Maven packages your application and all its dependencies into a single "fat JAR"
5. The Docker image includes this JAR file, which contains everything needed to run

## Files

1. `Dockerfile` - Multi-stage build for optimal image size
2. `docker-compose.yml` - Defines services for your application
3. `.dockerignore` - Excludes unnecessary files from Docker build context

## Building and Running

To build and run your application:

```bash
docker-compose up --build
```

This command will:
1. Build your Spring Boot application using Maven (with all dependencies)
2. Package it into a JAR file
3. Create a Docker image
4. Start your application (connected to your NeonDB PostgreSQL database)

To run in detached mode:
```bash
docker-compose up --build -d
```

To stop:
```bash
docker-compose down
```