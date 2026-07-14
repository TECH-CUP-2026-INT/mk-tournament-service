---
hide:
  - navigation
---

# Tournament Service — TechCup

<div class="grid cards" markdown>

-   **Servicio de Torneos**

    Microservicio responsable de la gestión completa del ciclo de vida de los torneos: creación, inscripciones, calendario de encuentros, llaves eliminatorias y visibilidad pública.

-   **Spring Boot 3.5.6 + MongoDB**

    Construido con Java 21, arquitectura hexagonal y persistencia en MongoDB. Se integra con los demás microservicios de TechCup a través del API Gateway.

-   **Seguridad con JWT (planeada)**

    El control de acceso por rol (organizador, capitán, jugador) vía JWT del `cc-identity-service` es la intención de negocio. Hoy los endpoints están abiertos — ver [Arquitectura](arquitectura.md#seguridad).

-   **Parte del ecosistema TechCup**

    Un microservicio dentro de la plataforma DOSW que digitaliza el torneo semestral de fútbol de la Escuela Colombiana de Ingeniería Julio Garavito.

</div>

---

## ¿Qué hace este servicio?

El `mk-tournament-service` centraliza toda la lógica del torneo:

- El **organizador** crea y gestiona torneos (borrador → activo → en progreso → finalizado).
- Los **capitanes** inscriben equipos cargando el comprobante de pago.
- El **organizador** aprueba o rechaza inscripciones.
- El sistema genera **llaves eliminatorias** automáticamente al cerrar inscripciones.
- Todos los usuarios consultan el **calendario**, el **mapa de canchas** y las **llaves** en tiempo real.

---

## Repositorio

```
https://github.com/TECH-CUP-2026-INT/mk-tournament-service
```

## Cómo empezar rápido

```bash
# 1. Clonar
git clone https://github.com/TECH-CUP-2026-INT/mk-tournament-service.git
cd mk-tournament-service

# 2. Levantar con Docker Compose (MongoDB incluido)
docker compose up --build

# 3. El servicio queda disponible en
http://localhost:8080
```

[Ver configuración completa](configuracion.md){ .md-button .md-button--primary }
[Ver API REST](api.md){ .md-button }
