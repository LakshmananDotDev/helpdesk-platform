# Project Context & Decisions Log

## Architecture Overview
Multi-tenant helpdesk platform. Event-driven backend using Kafka as the event backbone, Redis for SLA TTL tracking and WebSocket pub/sub fan-out, Elasticsearch for full-text ticket search.

## Decisions Log

### 2026-09-05 — Module 0: Project Setup
- Spring Boot [paste version from pom.xml] with Java 17, Maven build
- Dependencies chosen: Web, JPA, PostgreSQL Driver, Validation, DevTools, Lombok
- Kafka/Redis/Security/Elasticsearch deliberately deferred to later modules to keep Module 0 scoped to "does the app start and build cleanly"