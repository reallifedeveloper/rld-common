# Copilot Instructions for rld-common

## Project Overview
- **Purpose:** Shared Java library for the [RealLifeDeveloper blog](https://reallifedeveloper.com/), providing reusable code for multiple projects.
- **Distribution:** Published to Maven Central as `com.reallifedeveloper:rld-common`.
- **Documentation:** Main docs and Javadoc are generated via Maven site (`target/site/index.html`).

## Architecture & Structure
- **Source code:**
  - Main: `src/main/java/com/reallifedeveloper/common/`
  - Tests: `src/test/java/com/reallifedeveloper/common/`
  - Resources: `src/main/resources/`, `src/test/resources/`
- **Key patterns:**
  - Follows DDD (Domain-Driven Design) concepts: `domain`, `application`, `infrastructure` packages.
  - Messaging and event store support: see `infrastructure.messaging` and `application.eventstore`.
  - Integration with Kafka and RabbitMQ for messaging (see test resources and integration tests).

## Build & Quality
- **Standard build:** `mvn clean install`
- **Full quality checks:** `mvn -DcheckAll clean install`
- **Site & coverage:** `mvn -P coverage clean integration-test site`
- **CI/CD:** GitHub Actions workflows in `.github/workflows/` (not in repo, but referenced in badges).

## Conventions & Practices
- **Testing:**
  - Unit and integration tests in `src/test/java/`
  - Integration test output in `target/failsafe-reports/`
  - Kafka test certs/keys in `src/test/resources/kafka/` (see local README for generation steps)
- **Quality tools:**
  - Checkstyle, PMD, SpotBugs configs in `target/` and root
  - Suppressions and exclusions are project-specific (see `checkstyle-suppressions.xml`, `spotbugs-exclude.xml`)
- **Contribution:**
  - See `CONTRIBUTING.md` for guidelines
  - Issues and suggestions via GitHub Issues

## Integration Points
- **Messaging:**
  - Kafka and RabbitMQ integration for event publishing (see `infrastructure.messaging`)
  - Event store and notification log patterns (see `application.eventstore`, `application.notification`)
- **External dependencies:**
  - Managed via Maven (`pom.xml`)
  - Test resources may require local setup (see Kafka certs)

## Examples
- **Add a new domain event:**
  - Place in `domain` package, implement serialization if needed
- **Add a new integration test:**
  - Place in `src/test/java/com/reallifedeveloper/common/infrastructure/`
  - Use existing test resources or generate new ones as described in test resource READMEs

---
For more, see the [README.md](../README.md) and [CONTRIBUTING.md](../CONTRIBUTING.md).
