#!/usr/bin/env bash
# Un solo comando: levanta tournament + matches + sus Mongo + RabbitMQ local
# (todo offline, sin CloudAMQP ni team-service) y corre los 3 chequeos.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

echo "==> docker compose up (build + wait a que los healthchecks pasen)..."
docker compose up -d --build --wait

PYTHON_BIN="python3"
if ! command -v python3 >/dev/null 2>&1; then
  PYTHON_BIN="python"
fi

echo "==> Corriendo smoke_test.py..."
set +e
"$PYTHON_BIN" smoke_test.py
EXIT_CODE=$?
set -e

echo
echo "Para ver logs:      docker compose logs -f tournament   (o matches)"
echo "Para bajar todo:    docker compose down -v"

exit $EXIT_CODE
