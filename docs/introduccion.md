# Introducción

## Contexto del proyecto

Los programas de Ingeniería de Sistemas, Inteligencia Artificial, Ciberseguridad e Ingeniería Estadística de la **Escuela Colombiana de Ingeniería Julio Garavito** realizan cada semestre un torneo interno de fútbol. Hasta ahora su organización dependía de procesos manuales: mensajes de WhatsApp, formularios de Google y hojas de cálculo, lo que generaba desorden, retrasos y confusión tanto en participantes como en organizadores.

**TECHCUP FÚTBOL** es la respuesta a ese problema: una plataforma web que centraliza toda la gestión del torneo en un único sistema organizado, transparente y accesible.

---

## El problema

| Problema | Impacto |
|---|---|
| Proceso de inscripción no claro | Los estudiantes no saben cómo ni cuándo inscribirse |
| Completar equipos es difícil | Los capitanes no tienen forma de encontrar jugadores libres |
| Verificación manual de pagos | Retrasos y errores administrativos |
| Resultados actualizados a mano | Tabla de posiciones siempre desactualizada |
| Llaves organizadas en papel | Errores y conflictos en el bracket |
| Información dispersa | Sin canal oficial: WhatsApp, hojas de cálculo, email |
| Árbitros sin herramienta digital | Gestión en vivo imposible |

---

## La solución: ecosistema de microservicios

TechCup está construido como un sistema distribuido de **14 microservicios** agrupados en 4 dominios:

| Dominio | Servicios |
|---|---|
| D1 — Identidad y Personas | Servicio de Identidad, Servicio de Usuarios y Jugadores, Servicio de Equipos |
| **D2 — Torneo y Competencia** | **Servicio de Torneos** ← este servicio, Servicio de Pagos, Servicio de Inscripción del torneo |
| D3 — Operaciones y Comunicación | Servicio de Partidos, Servicio de Logística, Servicio de Comunicaciones, Servicio de Notificaciones |
| D4 — Estadísticas | Servicio de Estadísticas de Torneo, de Jugador, de Equipo, de Partido |

El equipo **MK** es dueño del Servicio de Torneos, el Servicio de Pagos y el Servicio de Inscripción del torneo, dentro del dominio D2. Ver [Equipo](equipo.md) para el detalle completo de la organización.

---

## Responsabilidades de `mk-tournament-service`

Este servicio es el **núcleo administrativo** del torneo. Gestiona:

- **Ciclo de vida del torneo**: borrador → activo → en progreso → finalizado.
- **Inscripciones y pagos**: los capitanes cargan comprobantes; el organizador aprueba o rechaza.
- **Calendario de encuentros**: fechas, horas, canchas y equipos.
- **Mapa del campus**: vista interactiva de canchas con estado en tiempo real.
- **Llaves eliminatorias**: generación automática de bracket al cerrar inscripciones.
- **Visibilidad pública**: información del torneo accesible para todos los usuarios.
- **Histórico**: consulta de torneos finalizados con toda su información.

---

## Actores del sistema (perspectiva de este servicio)

| Actor | Acciones principales |
|---|---|
| **Organizador** | Crear, activar, iniciar y finalizar torneos; aprobar/rechazar inscripciones; gestionar canchas y calendario |
| **Capitán** | Inscribir equipo, cargar comprobante, cancelar inscripción, consultar estado |
| **Jugador / Estudiante** | Consultar torneos, calendario, llaves y estadísticas públicas |

---

## Tecnología

El servicio está construido sobre:

- **Java 21** con **Spring Boot 3.5.6**
- **Arquitectura hexagonal** (puertos y adaptadores)
- **Spring Data MongoDB** para persistencia de documentos
- **Spring Security** para autenticación y autorización por JWT
- **Lombok** para reducción de boilerplate
- **Maven** como gestor de dependencias

La imagen se distribuye en **Docker** y puede desplegarse en AWS (ECS + Fargate) o Azure (Container Apps).
