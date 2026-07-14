# Configuración

## Prerrequisitos

| Herramienta | Versión mínima | Notas |
|---|---|---|
| Java (JDK) | 21 | Temurin/Eclipse recomendado |
| Maven | 3.9+ | O usar el wrapper `./mvnw` incluido |
| Docker | 24+ | Para levantar MongoDB y el servicio |
| Docker Compose | 2.x | Incluido en Docker Desktop |
| Git | cualquier | |

---

## Clonar el repositorio

```bash
git clone https://github.com/TECH-CUP-2026-INT/mk-tournament-service.git
cd mk-tournament-service
```

Rama principal: `main`. Ramas de trabajo por feature: `feature/<nombre>`.

---

## Variables de entorno

El servicio usa `application.properties`. Para desarrollo local las variables clave son:

```properties
# MongoDB — base de datos del servicio
spring.data.mongodb.uri=mongodb://localhost:27017/techcup_tournaments

# Servicios externos (deben estar corriendo o se obtiene timeout)
payment-service.base-url=http://localhost:8081
team-service.base-url=http://localhost:8082
```

Para Docker, estas se sobreescriben con variables de entorno del contenedor (ver sección Docker Compose abajo).

> **Nunca subas credenciales reales al repo.** Usa GitHub Secrets en el pipeline de CI/CD.

---

## Ejecución local (sin Docker)

```bash
# Asegúrate de tener MongoDB corriendo en localhost:27017
# Compilar y ejecutar
./mvnw spring-boot:run

# O construir el JAR y ejecutarlo
./mvnw clean package -DskipTests
java -jar target/service-tournament-0.0.1-SNAPSHOT.jar
```

---

## Ejecución con Docker Compose

La forma recomendada para desarrollo. Levanta el servicio y MongoDB juntos:

```bash
docker compose up --build
```

El `docker-compose.yml` del repositorio define:

```yaml
version: '3.8'

services:
  mk-tournaments:
    build: ./
    container_name: mk-tournaments
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/techcup_tournaments
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

Para detener:

```bash
docker compose down          # detiene y elimina contenedores
docker compose down -v       # también elimina el volumen de datos
```

---

## Dockerfile (build en dos etapas)

El `Dockerfile` del repositorio usa la técnica de multi-stage build para producir una imagen liviana:

```dockerfile
# Etapa 1: compilar con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen de producción (solo JRE + JAR)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/service-tournament-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Instalación de MkDocs (esta documentación)

```bash
# Instalar Python 3.10+ y pip
pip install mkdocs-material

# Previsualizar en local (desde la raíz del proyecto)
mkdocs serve

# Construir el sitio estático
mkdocs build

# Publicar en GitHub Pages
mkdocs gh-deploy
```

La documentación queda disponible en: `https://tech-cup-2026-int.github.io/mk-tournament-service/`

---

## Verificar que el servicio responde

```bash
# Health check básico (endpoint de Spring Boot Actuator o ping propio)
curl http://localhost:8080/actuator/health

# Listar torneos públicos (sin autenticación)
curl http://localhost:8080/api/v1/tournaments
```
