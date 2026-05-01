$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Set-Location $Root
docker compose --env-file .env -f docker/docker-compose.local.yml up -d
Write-Host "Local middleware started: MySQL 3306, Redis 6379, MinIO 9000/9001, Elasticsearch 9200, Kibana 5601."
