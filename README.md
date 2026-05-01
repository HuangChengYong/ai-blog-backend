# AI Blog Backend

Spring Boot 3.x backend scaffold for the current `ai-blog` Vue admin and blog UI.

## Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security + JWT
- MyBatis Plus
- MySQL 8
- Liquibase
- Redis
- MinIO with an object-storage factory for COS/S3/OSS style providers
- ELK logging

## Local Start On Windows

1. Copy `.env.example` to `.env` and adjust secrets if needed.
2. Start middleware:

```powershell
.\scripts\windows\up-local.ps1
```

3. Start backend:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
mvn spring-boot:run
```

Default local admin account:

- username: `admin`
- password: `huangcy125643`

The seed password is stored with `{noop}` for local bootstrap only. Replace it with a BCrypt hash before production.

## Production On Linux

1. Copy `.env.example` to `.env` and replace all passwords and keys.
2. Start middleware:

```bash
chmod +x scripts/linux/*.sh
./scripts/linux/up-prod.sh
```

3. Run the application as a systemd service or container.

## Database

Liquibase loads:

- `src/main/resources/db/changelog/001-schema.sql`
- `src/main/resources/db/changelog/002-seed.sql`

The schema includes RBAC, menus, users, roles, permissions, content, articles, drafts, reviews, comments, AI generation tasks, storage providers, storage objects, audit logs, operation logs, login logs, dictionaries, notices, and system config.

## Technology Options

See `docs/technology-options.md`. Required choices are already wired; optional choices are listed so you can choose before I add them.
# ai-blog-backend
