---
hide:
  - navigation
---

# Tournament Service — TechCup

<div class="grid cards" markdown>

-   **Tournament Service**

    Microservice responsible for the full tournament lifecycle: creation, enrollments, match scheduling, elimination brackets and public visibility.

-   **Spring Boot 3.5.6 + MongoDB**

    Built with Java 21, hexagonal architecture and MongoDB persistence. Integrates with the rest of the TechCup microservices through the API Gateway.

-   **JWT security (planned)**

    Role-based access control (Organizer, Captain, Player) via JWT from `cc-identity-service` is the documented business intent. Endpoints are currently open — see [Architecture](arquitectura.md#security).

-   **Part of the TechCup ecosystem**

    A microservice within the DOSW platform that digitizes the semester football tournament at Escuela Colombiana de Ingeniería Julio Garavito.

</div>

---

## What does this service do?

`mk-tournament-service` centralizes all tournament logic:

- The **organizer** creates and manages tournaments (draft → active → in progress → finished).
- **Captains** enroll teams by uploading proof of payment.
- The **organizer** approves or rejects enrollments.
- The system automatically generates **elimination brackets** once enrollment closes.
- All users can view the **schedule**, the **court map** and the **brackets** in real time.

---

## Repository

```
https://github.com/TECH-CUP-2026-INT/mk-tournament-service
```

## Quick start

```bash
# 1. Clone
git clone https://github.com/TECH-CUP-2026-INT/mk-tournament-service.git
cd mk-tournament-service

# 2. Start with Docker Compose (MongoDB included)
docker compose up --build

# 3. The service is available at
http://localhost:8080
```

[See full configuration](configuracion.md){ .md-button .md-button--primary }
[See REST API](api.md){ .md-button }
