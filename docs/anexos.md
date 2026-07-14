# Anexos

## A. Glosario

| Término | Definición |
|---|---|
| **Torneo** | Evento deportivo configurado por el organizador con fechas, cupo, costo y canchas. |
| **Inscripción** | Solicitud de un capitán para que su equipo participe en un torneo. Incluye comprobante de pago. |
| **Bracket / Llave** | Diagrama de enfrentamientos eliminatorios generado automáticamente al iniciar el torneo. |
| **Cancha** | Espacio físico del campus donde se juegan los partidos, con nombre, imagen y ubicación georreferenciada. |
| **Estado del torneo** | Fase en que se encuentra: `BORRADOR → ACTIVO → EN_PROGRESO → FINALIZADO`. |
| **Estado de inscripción** | Fase de la solicitud: `EN_REVISION → APROBADO / RECHAZADO / CANCELADO`. |
| **OTP** | One-Time Password. Código de uso único para verificar acciones sensibles (registro, primer ingreso). |
| **JWT** | JSON Web Token. Ficha firmada emitida por `cc-identity-service` al autenticarse. |
| **Arquitectura hexagonal** | Patrón donde el dominio (reglas del negocio) está en el centro y no depende de frameworks externos. |
| **Puerto** | Interfaz que define lo que el dominio puede recibir (puerto de entrada) o necesita (puerto de salida). |
| **Adaptador** | Implementación concreta de un puerto: el controlador REST (entrada) o el repositorio MongoDB (salida). |
| **MapStruct** | Librería que genera código de conversión entre DTOs y objetos de dominio en tiempo de compilación. |
| **WCAG 2.1 AA** | Estándar de accesibilidad web (Web Content Accessibility Guidelines) al que debe ajustarse el frontend. |
| **MoSCoW** | Técnica de priorización: Must / Should / Could / Won't. |

---

## B. Stack tecnológico completo

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 3.5.6 |
| Seguridad | Spring Security + JWT | — |
| Persistencia | Spring Data MongoDB | — |
| Base de datos | MongoDB | 7 |
| Reducción de boilerplate | Lombok | — |
| Gestión de proyecto | Maven | 3.9+ |
| Contenedores | Docker + Docker Compose | 24+ |
| CI/CD | GitHub Actions | — |
| Frontend (otro equipo) | React + TypeScript | — |
| Gestión de tareas | Jira (Scrum) | — |
| Documentación | MkDocs Material | — |

---

## C. Dependencias del `pom.xml`

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

<!-- Utilidades -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Pruebas -->
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

## D. Flujo completo: de requerimiento a producción

```
Requerimiento (TC-46)
  → Especificación con plantilla (actor, flujo, criterios)
  → Historia de usuario en Jira + subtareas [back] [front] [test]
  → Back: endpoint → caso de uso → dominio → repositorio MongoDB
  → Cada regla de negocio tiene su prueba unitaria
  → El servicio se dockeriza con Dockerfile multi-stage
  → CI: mvn verify (pruebas en verde = merge permitido)
  → CD: construye imagen → sube a registro de contenedores → despliega
```

---

## E. Referencias

- [Spring Boot 3.5 Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/reference/)
- [Spring Security Architecture](https://docs.spring.io/spring-security/reference/)
- [MkDocs Material](https://squidfunk.github.io/mkdocs-material/)
- [Hexagonal Architecture — Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [WCAG 2.1 (W3C)](https://www.w3.org/TR/WCAG21/)
- [MapStruct](https://mapstruct.org/documentation/stable/reference/html/)
- [Docker multi-stage builds](https://docs.docker.com/build/building/multi-stage/)
- [GitHub Actions — Java CI](https://docs.github.com/en/actions/use-cases-and-examples/building-and-testing/building-and-testing-java-with-maven)
