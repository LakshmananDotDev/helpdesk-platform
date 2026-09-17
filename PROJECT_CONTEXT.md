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

### 2026-09-17 — Module 1: Entities & Repositories
- Built all 4 core entities: Organization, User (Role enum: ADMIN/AGENT/CUSTOMER), Ticket (Status/Priority enums, @Version for optimistic locking), Comment
- Clarified multi-tenancy model: Organization represents one tenant's helpdesk deployment, not a real-world employer — the same person interacting with two different tenants becomes two separate User rows
- Relationships kept unidirectional by design (ManyToOne only, no reverse OneToMany) to avoid Lombok equals/hashCode/toString recursion risk and unused complexity, since all queries start from the "many" side
- Built and verified all 4 Spring Data JPA repositories (Organization, User, Ticket, Comment)
- Fixed a typo bug: JoinColumn on Comment.author was "suthor_id", caught via generated SQL, cleaned up directly in Postgres
- Next: DTOs, service layer, exception handling, controllers, validation — then Security/JWT and multi-tenancy filters once learned