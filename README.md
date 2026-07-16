# mk-tournament-service

## Descripción

`mk-tournament-service` es un microservicio REST encargado de gestionar todo el ciclo de vida de los torneos dentro de la plataforma **TECH CUP 2026**. Permite crear, configurar, ejecutar y finalizar torneos, así como administrar inscripciones de equipos, canchas, fixtures, partidos programados, sanciones y auditoría de eventos.

El servicio está construido sobre una **arquitectura hexagonal (Ports & Adapters)**, garantizando separación de responsabilidades entre el dominio, los casos de uso y la infraestructura.

---

## Funcionalidades

- Crear, editar, pausar, inactivar y eliminar torneos
- Registrar y consultar reglamentos (PDF via GridFS)
- Registrar canchas y consultar mapa de canchas
- Generar fixture y agendar partidos
- Inscribir y retirar equipos, validar inscripciones y subir comprobantes de pago
- Inactivar equipos, usuarios y partidos
- Aplicar sanciones a jugadores y descalificar equipos
- Finalizar torneos y asignar campeón
- Consultar histórico de torneos y auditoría de eventos
- Notificaciones asíncronas mediante RabbitMQ

---

## Diagramas

| Diagrama | Archivo |
|---|---|
| Clases del torneo | [`docs/assets/diagrams/TC-tournament-clases.png`](docs/assets/diagrams/TC-tournament-clases.png) |
| Componentes generales | [`docs/assets/diagrams/Componenetes generales.png`](docs/assets/diagrams/Componenetes%20generales.png) |
| Diagrama de componentes | [`docs/assets/diagrams/Diagrama de componentes.png`](docs/assets/diagrams/Diagrama%20de%20componentes.png) |
| Diagramas de secuencia TC-29 al TC-56 | [`docs/assets/diagrams/`](docs/assets/diagrams/) |

---

## Integrantes

| Nombre | Rol |
|---|---|
| Hever Barrera Batero | Desarrollador |
| Juan Eduardo Vera Acerto | Desarrollador |
| Mabel Fernanda Bernal Amaya | Desarrolladora |
| Nicolás David Prieto Ramos | Desarrollador |

---

## Tecnologías usadas

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.6 |
| Spring Security | - |
| Spring Data MongoDB | - |
| Spring Cloud OpenFeign | 2025.0.0 |
| Spring AMQP (RabbitMQ) | - |
| MapStruct | 1.5.5.Final |
| Lombok | - |
| SpringDoc OpenAPI (Swagger) | 2.8.17 |
| Cucumber | 7.20.1 |
| JaCoCo | 0.8.12 |
| Maven | - |

---

## Cómo ejecutar el proyecto

### Prerrequisitos

- Java 21
- Maven 3.8+
- MongoDB (URI configurada en variable de entorno)
- RabbitMQ (credenciales configuradas)

### Clonar el repositorio

```bash
git clone https://github.com/TECH-CUP-2026-INT/mk-tournament-service.git
cd mk-tournament-service
```

### Configurar variables de entorno

```bash
# URI de conexión a MongoDB
SPRING_DATA_MONGODB_URI=mongodb+srv://<user>:<password>@<cluster>/tournament

# Contraseña RabbitMQ
RABBITMQ_PASS=<password>
```

### Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

### Ejecutar tests

```bash
mvn test
```

### Acceder a Swagger UI

Una vez levantado el servicio, la documentación de la API está disponible en:

```
http://localhost:8080/swagger-ui.html
```
