# Appendices

## A. Glossary

| Term | Definition |
|---|---|
| **Tournament** | Sporting event configured by the organizer with dates, capacity, cost and courts. |
| **Enrollment** | A captain's request for their team to participate in a tournament. Includes proof of payment. |
| **Bracket** | Diagram of elimination matchups automatically generated when the tournament starts. |
| **Court** | Physical space on campus where matches are played, with name, image and geo-referenced location. |
| **Tournament status** | Phase the tournament is in: `DRAFT → ACTIVE → IN_PROGRESS → FINISHED`. |
| **Enrollment status** | Phase of the request: `UNDER_REVIEW → APPROVED / REJECTED / CANCELLED`. |
| **OTP** | One-Time Password. Single-use code to verify sensitive actions (registration, first login). |
| **JWT** | JSON Web Token. Signed token issued by `cc-identity-service` upon authentication. |
| **Hexagonal architecture** | Pattern where the domain (business rules) sits at the center and does not depend on external frameworks. |
| **Port** | Interface defining what the domain can receive (inbound port) or needs (outbound port). |
| **Adapter** | Concrete implementation of a port: the REST controller (inbound) or the MongoDB repository (outbound). |
| **MapStruct** | Library that generates conversion code between DTOs and domain objects at compile time. |
| **WCAG 2.1 AA** | Web accessibility standard (Web Content Accessibility Guidelines) the frontend must comply with. |
| **MoSCoW** | Prioritization technique: Must / Should / Could / Won't. |

---

## B. Full technology stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.5.6 |
| Security | Spring Security + JWT | — |
| Persistence | Spring Data MongoDB | — |
| Database | MongoDB | 7 |
| Boilerplate reduction | Lombok | — |
| Project management | Maven | 3.9+ |
| Containers | Docker + Docker Compose | 24+ |
| CI/CD | GitHub Actions | — |
| Frontend (other team) | React + TypeScript | — |
| Task management | Jira (Scrum) | — |
| Documentation | MkDocs Material | — |

---

## C. `pom.xml` dependencies

```xml
<!-- Spring Boot starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Tests -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## D. Full flow: from requirement to production

```
Requirement (TC-46)
  → Specification using a template (actor, flow, criteria)
  → User story in Jira + subtasks [back] [front] [test]
  → Back-end: endpoint → use case → domain → MongoDB repository
  → Every business rule has its own unit test
  → The service is dockerized with a multi-stage Dockerfile
  → CI: mvn verify (green tests = merge allowed)
  → CD: builds the image → pushes to the container registry → deploys
```

---

## E. References

- [Spring Boot 3.5 Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/reference/)
- [Spring Security Architecture](https://docs.spring.io/spring-security/reference/)
- [MkDocs Material](https://squidfunk.github.io/mkdocs-material/)
- [Hexagonal Architecture — Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [WCAG 2.1 (W3C)](https://www.w3.org/TR/WCAG21/)
- [MapStruct](https://mapstruct.org/documentation/stable/reference/html/)
- [Docker multi-stage builds](https://docs.docker.com/build/building/multi-stage/)
- [GitHub Actions — Java CI](https://docs.github.com/en/actions/use-cases-and-examples/building-and-testing/building-and-testing-java-with-maven)
