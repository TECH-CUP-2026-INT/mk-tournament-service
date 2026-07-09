---
name: hexagonal-microservice
description: Apply hexagonal architecture to Spring Boot microservices in this project. Use when creating new classes, adding features, defining layers, or when the user asks about project structure, package organization, or where to place code. This project is a microservice: stateless, single-responsibility, communicates via REST.
---

# Hexagonal Architecture — Tournament Microservice

## Context

This is a **Spring Boot microservice** (stateless, MongoDB, REST API). All new code must follow hexagonal architecture.

## Package structure

Base package: `co.edu.escuelaing.techcup.tournament`

```
domain/
  model/          ← Pure Java classes. No framework annotations.
  port/in/        ← Interfaces defining what the domain exposes (use cases).
  port/out/       ← Interfaces defining what the domain needs (repositories, etc.).

application/
  service/        ← Implements port/in interfaces. Orchestrates domain logic.

infrastructure/
  rest/           ← Controllers, DTOs, mappers (REST adapter).
  persistence/    ← Entities, Mongo repositories, adapters (persistence adapter).
```

## Layer rules

| Layer | Allowed dependencies | Forbidden |
|---|---|---|
| `domain` | None | Spring, JPA, Mongo, Lombok |
| `application` | `domain` only | `infrastructure` |
| `infrastructure` | `domain`, `application` | Direct cross-infra calls |

## Naming conventions

| Concept | Suffix | Example |
|---|---|---|
| Use case port | `UseCase` | `CreateTournamentUseCase` |
| Repository port | `RepositoryPort` | `TournamentRepositoryPort` |
| Application service | `Service` | `CreateTournamentService` |
| REST controller | `Controller` | `TournamentController` |
| Persistence entity | `Entity` | `TournamentEntity` |
| Repository (Mongo) | `Repository` | `TournamentMongoRepository` |
| Persistence adapter | `RepositoryAdapter` | `TournamentRepositoryAdapter` |

## Microservice constraints

- No shared state between requests.
- Configuration via environment variables (never hardcoded).
- MongoDB URI injected through `SPRING_DATA_MONGODB_URI`.

## Additional resources

For concrete code examples per layer, see [layer-guide.md](layer-guide.md).
