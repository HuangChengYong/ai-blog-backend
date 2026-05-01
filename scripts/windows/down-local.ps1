$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Set-Location $Root
docker compose --env-file .env -f docker/docker-compose.local.yml down
Write-Host "Local middleware stopped."
