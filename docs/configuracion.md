# Configuration

## Prerequisites

| Tool | Minimum version | Notes |
|---|---|---|
| Java (JDK) | 21 | Temurin/Eclipse recommended |
| Maven | 3.9+ | Or use the included `./mvnw` wrapper |
| Docker | 24+ | To run MongoDB and the service |
| Docker Compose | 2.x | Bundled with Docker Desktop |
| Git | any | |

---

## Clone the repository

```bash
git clone https://github.com/TECH-CUP-2026-INT/mk-tournament-service.git
cd mk-tournament-service
```

Main branch: `main`. Feature branches: `feature/<name>`.

---

## Environment variables

The service uses `application.properties`. For local development, the key variables are:

```properties
# MongoDB — the service's database
spring.data.mongodb.uri=mongodb://localhost:27017/techcup_tournaments

# External services (must be running or calls will time out)
payment-service.base-url=http://localhost:8081
team-service.base-url=http://localhost:8082
```

For Docker, these are overridden with container environment variables (see the Docker Compose section below).

> **Never commit real credentials to the repo.** Use GitHub Secrets in the CI/CD pipeline.

---

## Running locally (without Docker)

```bash
# Make sure MongoDB is running on localhost:27017
# Build and run
./mvnw spring-boot:run

# Or build the JAR and run it
./mvnw clean package -DskipTests
java -jar target/service-tournament-0.0.1-SNAPSHOT.jar
```

---

## Running with Docker Compose

The recommended way for development. Starts the service and MongoDB together:

```bash
docker compose up --build
```

The repository's `docker-compose.yml` defines:

```yaml
version: '3.8'

services:
  mk-tournaments:
    build: ./
    container_name: mk-tournaments
    ports:
      - "5623:5623"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/techcup_tournaments
      SERVER_PORT: 5623
    depends_on:
      - mongo
    restart: on-failure

  mongo:
    image: mongo:7-jammy
    container_name: docker-tournaments
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

volumes:
  mongo_data:
```

To stop it:

```bash
docker compose down          # stops and removes containers
docker compose down -v       # also removes the data volume
```

---

## Dockerfile (two-stage build)

The repository's `Dockerfile` uses a multi-stage build to produce a lightweight image:

```dockerfile
# Stage 1: build with Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: production image (JRE + JAR only)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/service-tournament-*.jar app.jar
EXPOSE 5623
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Installing MkDocs (this documentation)

```bash
# Install Python 3.10+ and pip
pip install mkdocs-material

# Preview locally (from the project root)
mkdocs serve

# Build the static site
mkdocs build

# Publish to GitHub Pages
mkdocs gh-deploy
```

The documentation is published at: `https://tech-cup-2026-int.github.io/mk-tournament-service/`

---

## Verify the service is responding

```bash
# Basic health check (Spring Boot Actuator endpoint or custom ping)
curl http://localhost:5623/actuator/health

# List public tournaments (no authentication required)
curl http://localhost:5623/api/v1/tournaments
```
