# Introduction

## Project context

Every semester, the Systems Engineering, Artificial Intelligence, Cybersecurity and Statistical Engineering programs at **Escuela Colombiana de Ingeniería Julio Garavito** run an internal football tournament. Until now its organization relied on manual processes: WhatsApp messages, Google Forms and spreadsheets, which caused disorder, delays and confusion for both participants and organizers.

**TECHCUP FÚTBOL** is the answer to that problem: a web platform that centralizes the entire tournament management in a single organized, transparent and accessible system.

---

## The problem

| Problem | Impact |
|---|---|
| Unclear enrollment process | Students don't know how or when to enroll |
| Completing a team's roster is hard | Captains have no way to find free players |
| Manual payment verification | Administrative delays and errors |
| Manually updated results | Standings table is always out of date |
| Brackets organized on paper | Errors and conflicts in the bracket |
| Scattered information | No official channel: WhatsApp, spreadsheets, email |
| Referees without a digital tool | Live management is impossible |

---

## The solution: a microservices ecosystem

TechCup is built as a distributed system of **14 microservices** grouped into 4 domains:

| Domain | Services |
|---|---|
| D1 — Identity and People | Identity Service, Users and Players Service, Teams Service |
| **D2 — Tournament and Competition** | **Tournament Service** ← this service, Payment Service, Tournament Enrollment Service |
| D3 — Operations and Communication | Matches Service, Logistics Service, Communications Service, Notifications Service |
| D4 — Statistics | Tournament, Player, Team and Match Statistics Services |

The **MK** team owns the Tournament Service, the Payment Service and the Tournament Enrollment Service, within domain D2. See [Team](equipo.md) for the full organizational breakdown.

---

## Responsibilities of `mk-tournament-service`

This service is the tournament's **administrative core**. It manages:

- **Tournament lifecycle**: draft → active → in progress → finished.
- **Enrollments and payments**: captains upload proof of payment; the organizer approves or rejects.
- **Match schedule**: dates, times, courts and teams.
- **Campus map**: interactive view of courts with real-time status.
- **Elimination brackets**: automatic bracket generation once enrollment closes.
- **Public visibility**: tournament information accessible to all users.
- **History**: browsing finished tournaments with all their information.

---

## System actors (from this service's perspective)

| Actor | Main actions |
|---|---|
| **Organizer** | Create, activate, start and finalize tournaments; approve/reject enrollments; manage courts and schedule |
| **Captain** | Enroll team, upload proof of payment, cancel enrollment, check status |
| **Player / Student** | View tournaments, schedule, brackets and public statistics |

---

## Technology

The service is built on:

- **Java 21** with **Spring Boot 3.5.6**
- **Hexagonal architecture** (ports and adapters)
- **Spring Data MongoDB** for document persistence
- **Spring Security** for JWT-based authentication and authorization
- **Lombok** to reduce boilerplate
- **Maven** as the dependency manager

The image is distributed as a **Docker** image and can be deployed on AWS (ECS + Fargate) or Azure (Container Apps).
