# Smoke test de integracion: tournament <-> matches

Prueba de **cableado**, no de logica de negocio (eso ya lo cubre
`EightTeamGroupsToChampionIntegrationTest`). Levanta ambos servicios en
Docker, cada uno con su propio Mongo, mas un RabbitMQ **local**
(`rabbitmq:3-management`, nada de CloudAMQP: todo corre offline) y valida
el circuito real que las pruebas unitarias no cubren: el push HTTP con
API key hacia matches, y el flujo de RabbitMQ hacia tournament.

## Requisitos

- Docker + Docker Compose v2 (probado con Docker 29.6.1 / Compose v5.2.0).
- El repo `am-matches-service` clonado localmente, rama `prueba_mabel2`
  (o el HEAD que quieras probar — ver nota de commit abajo).
- Python 3 con la libreria `requests` (`pip install requests`).

## Como correrlo

```bash
cd mk-tournament-service/smoke-test
./run.sh
```

Eso hace `docker compose up -d --build --wait` (construye ambas imagenes,
espera a que Mongo x2 y RabbitMQ pasen su healthcheck) y despues corre
`smoke_test.py`, que imprime PASS/FAIL por cada paso y un resumen final.
Sale con exit code 0 si los 3 chequeos pasan, 1 si alguno falla.

Si tu clon de `am-matches-service` no esta en `../../am-matches-service`
(es decir, como hermano de esta carpeta del repo de tournament), exporta
la ruta antes de correr `run.sh`:

```bash
export MATCHES_SERVICE_PATH=/ruta/a/am-matches-service
./run.sh
```

Para bajar todo: `docker compose down -v` (el `-v` tambien borra los
volumenes de Mongo, para arrancar limpio la proxima vez).

## Commit de matches usado

El contrato lo confirmo el equipo de Matches en el commit `3958872`
(agregado de `ausenteId`). El clon local usado para este harness esta un
commit mas adelante, en `08e61f6` (rama `prueba_mabel2`, working tree
limpio al momento de armar esto) — ya se revisó el diff entre ambos commits
y es un refinamiento compatible, no rompe el contrato (mismos campos en
`ReceiveMatchDefinitionRequest`/`MatchFinishedEvent`, mismo exchange). El
harness construye la imagen de matches desde el HEAD actual de tu clon, sea
cual sea — si quieres fijar exactamente `3958872`, haz `git checkout 3958872`
en `am-matches-service` antes de correr `run.sh`.

## Decision: como se "aprueban" los 8 equipos sin team-service

El spec pide explicitamente **no** levantar team-service. El enroll real
(`POST /tournaments/{id}/enrollments`) llama a `TeamServiceClientPort` para
traer el roster del equipo antes de reservar el cupo, asi que sin
team-service ese endpoint no sirve para este harness (se cuelga hasta el
timeout de Feign).

Se eligio **sembrar directo en Mongo**, no mockear el Feign client via un
profile de test: el script crea el torneo y lo activa por REST como
siempre, y despues escribe directamente los arrays `teams` (con
`registrationStatus: APPROVED`) y `enrollments` (con `status: ENROLLED`)
del documento del torneo, usando `docker exec ... mongosh` contra el mismo
Mongo del contenedor `tournament` — sin tocar codigo de produccion ni
agregar un profile Spring nuevo solo para el harness. El seed ocurre
**despues** de activar y registrar la cancha, y **antes** de `prepare`:
`prepare` es la primera llamada REST que vuelve a leer y regrabar el
torneo completo, asi que cualquier seed hecho despues de ese punto se
perderia (Spring reemplaza el documento entero al guardar).

## Que hace cada chequeo

**CHEQUEO 1 — recepcion + auth en matches** (`POST /api/partidos`):
- con `X-Internal-Api-Key` correcta -> espera 2xx.
- sin el header, y con una llave incorrecta -> espera 401/403 en ambos casos.
- PASS solo si las tres se cumplen.

**CHEQUEO 2 — broker + forma del DTO en tournament**:
- publica un `MatchFinishedEvent` de muestra directo al exchange
  `techcup.exchange` (routing key `techcup.match.finished`) usando la API
  HTTP de administracion de RabbitMQ (`:15672`), con un `tournamentId`
  aleatorio que a proposito no existe.
- verifica en los logs del contenedor `tournament` que el listener recibio
  y deserializo el evento: como el torneo no existe, el logueo va a ser la
  excepcion `TournamentNotFoundException` con ese mismo id — eso es
  evidencia de que el binding y la forma del DTO son correctos (si la
  deserializacion fallara, el error séria otro, y si el evento se perdiera
  silenciosamente, ese id nunca aparecería en los logs).

**CHEQUEO 3 — un partido real, de punta a punta** (el chequeo fuerte):
crea un torneo de 8 equipos, lo activa, siembra las 8 inscripciones
(ver seccion anterior), prepara (genera el fixture de grupos) e inicia.
Toma un partido de grupos, lo programa en tournament (dispara el push a
matches), y confirma en matches que llego con los datos correctos.

Nota sobre esa confirmacion: `GET /api/partidos/{matchId}` en matches
espera **el id interno de matches**, no el `matchId` que conoce tournament
(`competenciaMatchId`) — `MatchAccessService.requireOwnedMatch` busca por
`matchRepository.findById(...)`, y tournament nunca se entera de ese id
interno (el push es fire-and-forget). Por eso el script confirma la
llegada con `GET /api/partidos` (el listado "mis partidos asignados" del
arbitro autenticado), correlacionando por `competenciaMatchId` — que si
viaja en ese listado — y validando que los nombres de los equipos
coincidan con los que se sembraron. De ahi saca el id interno para el
resto del flujo.

Ojo con la asimetria de ids en matches: `iniciar` (`POST /{id}/iniciar`)
es el unico endpoint que sigue usando `competenciaMatchId` (el matchId de
tournament); `goles` y `finalizar` usan el id interno que se resolvio en
el paso anterior. El script programa el partido con fecha/hora ya pasada
(UTC, "ahora mismo") a proposito: `iniciar` devuelve 409 con "El partido
aún no ha llegado a su hora programada de inicio" si el kickoff
(`matchDate`+`matchTime`) todavía no llegó.

Despues lo inicia como arbitro, registra 2 goles del local, lo finaliza
— y confirma que la tabla de posiciones en tournament
(`GET /tournaments/{id}/standings`) se actualiza sola, sin ninguna
llamada mas, en unos segundos. Si eso pasa, el circuito completo
(push -> arbitraje -> evento -> tabla) esta vivo.

Para autenticarse como arbitro contra matches, el script arma un JWT sin
firmar (`JwtClaimsFilter` en matches no verifica firma, solo decodifica el
payload) con `sub` = un UUID fijo por corrida y `roles: ["ARBITRO"]`, y usa
ese mismo UUID como `refereeId` al programar el partido en tournament —
asi el arbitro autenticado coincide con el arbitro asignado al partido.

## Puertos usados

| Servicio          | Puerto host | Notas                                  |
|--------------------|:-----------:|-----------------------------------------|
| tournament          | 5623        | igual que en producción/Azure           |
| matches              | 8090        | mapea al 8080 interno del contenedor    |
| RabbitMQ (AMQP)      | 5672        | no lo usa el script directamente        |
| RabbitMQ (management)| 15672      | usado por CHEQUEO 2 para publicar       |
| Mongo (tournament)   | 27018       | usado por el seed de CHEQUEO 3          |
| Mongo (matches)      | 27019       | solo para debug manual, no lo usa el script |
