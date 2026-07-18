# mk-tournament-service

## Environment variables (production / Azure App Service)

All service-to-service URLs and credentials are configurable via environment variables — `application.yml` only carries `localhost` values as **development defaults**, which don't resolve outside a local machine.

| Variable | Required | Default (dev only) | Notes |
|---|---|---|---|
| `SPRING_DATA_MONGODB_URI` | **Yes** | *(none — must be set)* | MongoDB connection string. |
| `RABBITMQ_PASS` | **Yes** | *(none — blank)* | Password for the shared CloudAMQP broker. Without it, the RabbitMQ listener fails authentication at startup (see `ServiceTournamentApplicationTests` for why that no longer crashes the whole app in tests — in a real deployment it still means the async match-result flow won't work). |
| `PAYMENT_SERVICE_URL` | Recommended | `http://localhost:8081` | Base URL of Payment Service. |
| `TEAM_SERVICE_URL` | Recommended | `http://localhost:8082` | Base URL of Team Service. |
| `MATCHES_SERVICE_URL` | Recommended | `http://localhost:8083` | Base URL of Matches, used to push match definitions (`POST /api/partidos`). |
| `INTERNAL_API_KEY` | Recommended | *(blank)* | Shared service-to-service key sent as `X-Internal-Api-Key` to Matches — must match the value configured on Matches' side. |
| `SERVER_PORT` | No | `5623` | Already matches the port the `Dockerfile` exposes and what's configured on Azure — don't override unless you know why. |
| `TECHCUP_RABBITMQ_EXCHANGE` | No | `techcup.exchange` | Only needed if the shared topic exchange name ever diverges from the default. |
| `SPRING_RABBITMQ_HOST` / `_PORT` / `_USERNAME` / `_VIRTUAL_HOST` / `_SSL_ENABLED` | No | Pre-set to the shared CloudAMQP instance | Standard Spring Boot relaxed-binding env vars; only needed to point at a different broker (e.g. local RabbitMQ for the Docker smoke test in `smoke-test/`). |

"Recommended" means: the app starts fine without it (falls back to a `localhost` default that won't be reachable), but the corresponding integration silently degrades (payment/team lookups time out to a fallback value, match definitions never reach Matches) rather than failing loudly — set it explicitly for any non-local environment.
