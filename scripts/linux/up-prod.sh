#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT"

docker compose --env-file .env -f docker/docker-compose.prod.yml up -d
echo "Production middleware started."
