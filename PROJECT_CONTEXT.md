# Project Context & Decisions Log

## Architecture Overview
Multi-tenant helpdesk platform. Event-driven backend using Kafka as the event backbone, Redis for SLA TTL tracking and WebSocket pub/sub fan-out, Elasticsearch for full-text ticket search.

## Decisions Log

### 2026-09-05 — Module 0: Project Setup
- Spring Boot [paste version from pom.xml] with Java 17, Maven build
- Dependencies chosen: Web, JPA, PostgreSQL Driver, Validation, DevTools, Lombok
- Kafka/Redis/Security/Elasticsearch deliberately deferred to later modules to keep Module 0 scoped to "does the app start and build cleanly"

### 2026-09-05 — Module 0: Environment troubleshooting
- Postgres port conflict with pre-existing local install; remapped Docker Postgres to host port 5433 instead of 5432
- Windows JVM timezone "Asia/Calcutta" rejected by postgres:16 image (deprecated tz alias); fixed via -Duser.timezone=UTC JVM option and hibernate.jdbc.time_zone=UTC
- Disabled spring.jpa.open-in-view to avoid lazy-loading leaks into the view layer ahead of Module 1