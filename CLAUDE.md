# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Cevi.DB Client: Swing desktop app (Java 21) giving read/export access to the Cevi Schweiz member database (a Hitobito instance at db.cevi.ch). Distributed as signed macOS `.dmg` / Windows `.exe` bundles (see `release/`). MIT licensed.

Single Maven module: `cevi-db-client/` (the repo root only holds release scripts and docs).

## Build & run

All commands run from `cevi-db-client/`:

```
mvn clean package                    # build + generate OpenAPI client + run tests
mvn test                             # run unit tests only
mvn test -Dtest=SessionTest          # run a single test class
mvn test -Dtest=SessionTest#testLogin # run a single test method
mvn exec:java -Dexec.mainClass=ch.cevi.db.client.DBClient   # run the Swing app directly
```

Packaged native artifacts (macOS `.app`/`.dmg` via `appbundle-maven-plugin`, Windows via NSIS) are produced by `build.sh`, which downloads a bundled JRE per OS and invokes `mvn clean package appbundle:bundle` then `makensis Setup.nsi`. This is release tooling, not needed for normal development.

Tests use JUnit 4 (`junit:junit`), not JUnit 5.

## Architecture

### Generated OpenAPI client (critical context)

`src/main/resources/openapi.yaml` (~12k lines) is Hitobito's JSON:API spec. The `openapi-generator-maven-plugin` binds to the `generate-sources` phase and generates a full `resttemplate`-based client into `target/generated-sources/openapi/` on every build:

- `ch.cevi.db.client.api` — `*Api` classes (`GroupsApi`, `PeopleApi`, ...), one method per endpoint
- `ch.cevi.db.client.api.model` — JSON:API resource/attribute DTOs
- `ch.cevi.db.client.api.invoker` — `ApiClient` (Spring `RestTemplate` wrapper)

These generated classes are **not checked in** — they only exist after a Maven build (`mvn generate-sources` or later). If an IDE/tool can't resolve `ch.cevi.db.client.api.*` imports, run a build first rather than searching for missing source files.

Generated JSON:API list/get endpoints (e.g. `GroupsApi.getGroup(...)`) take a large number of positional filter parameters (one triplet per filterable field × comparator), most passed as `null`/`Collections.emptyList()`. This is generator output, not hand-written API design — when calling these methods, copy an existing call site (see `ServerFacade`) rather than reconstructing the signature from scratch.

### Migration in progress

The project is mid-migration from Hitobito's legacy REST API to the new JSON:API-based Cevi.DB API (see recent commits: "Migrated login to new API", "First step to migrate to new Cevi.DB API"). Consequences:

- `Session.login()` performs classic HTML form login (CSRF token scrape via Jsoup, POST to `/users/sign_in`, optional TOTP second-factor step) purely to establish a session cookie, then separately constructs a JSON:API `ApiClient` pointed at `{baseUrl}/api` for all subsequent data access.
- `ServerFacade.loadPersonByEmail` bypasses the generated client and hand-builds a raw `HttpClient` request — used because there was no other way yet to resolve "logged-in user" to a JSON:API person resource at login time.
- Business entities (`Person`, `YGroup`, `Role`, ...) are populated by two different paths depending on the method: some via generated model getters, others via manual `JSONObject` parsing of `.toString()`'d generated models (see `YGroup.initBasicDataFromJSONObject`). Expect both patterns to coexist until the migration finishes; don't "clean up" one without checking whether the other path is still load-bearing.
- Some mappings are intentionally incomplete (`// TODO: Map fields from includedJson to Role` in `ServerFacade.loadGroupWithDetails`).

### Layering

- `ch.cevi.db.client.communication` — `ISession`/`Session` (auth + HTTP + generated `ApiClient` ownership), `SessionFactory`, `ServerFacade` (business-facing API façade; the only place that should call generated `*Api` classes directly).
- `ch.cevi.db.client.business` — domain entities (`Person`, `YGroup`, `Role`, `PhoneNumber`, `SocialAccount`, `AdditionalMail`) and `export/` (CSV via OpenCSV, XLS via Apache POI). Entities know how to hydrate themselves from JSON:API JSON (`initBasicDataFromJSONObject`/`initDetailDataFromJSONObject`), not just plain getters/setters.
- `ch.cevi.db.client.gui` — Swing UI (`MainWindow`, `MainController` orchestrates login/load via `SwingWorker`s to keep the EDT unblocked; `grouptree/` lazy-loads the group hierarchy on expand via `LazyTreeModel`/`LazyTreeNode`; `detailsview/` shows person lists and QR-bill export dialogs).
- `ch.cevi.db.client.configuration` — `Configuration` (bean with `PropertyChangeSupport`, holds base URL enum `Live`/`Test`, credentials, TOTP code), `Constants`, `Texts`/`MessagesBundle_{de,en}.properties` (UI is bilingual DE/EN).

Control flow: `DBClient.main` → `MainWindow` → `MainController.connect()` (background login) → on success, background-loads the root `YGroup` → GUI populates the group tree lazily from there. All server calls happen off the EDT via `SwingWorker`.

### QR-bill export

`net.codecrete.qrbill:qrbill-generator` generates Swiss QR-bills as part of CSV/Excel export flows (`ExportDialog`, `GeneralExporter`); see `QRCodeBillingTest` for the expected input shape.
