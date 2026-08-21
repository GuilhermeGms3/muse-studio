# Project Memory

## Project identity

- Purpose: Muse Studio, professor musical local orientado por currículo, evidências, Missions e repertório.
- Repository root: `D:\dsf\muse-studio`.
- Main stack: Java 21, Spring Boot 4.0.7, Maven, H2/Flyway; React 19, TanStack Start/Router, Vite 8.

## Canonical sources

- Architecture: `docs/architecture/**`.
- Requirements: `docs/product/PEDAGOGICAL_PRODUCT_DIRECTION.md`, `docs/product/LEARNING_MISSIONS.md`.
- API/contracts: `backend/README.md`, `backend/src/main/java/com/musicos/api/ApiModels.java`.
- Data model: `backend/src/main/java/com/musicos/domain/**`, `backend/src/main/resources/db/migration/**`.
- Frontend/design system: `docs/ui/**`, `docs/ux/**`.
- Tests/build: `package.json`, `backend/pom.xml`, `backend/src/test/**`.

## Architecture map

- Modules: React workspace; Spring API; pedagogical domain; catalogs/configuration; JPA repositories; H2 persistence.
- Dependency direction: UI -> API -> services/engines -> domain/repositories; catalogs seed canonical editorial definitions.
- Important flows: Diagnostic -> CurriculumEngine -> Coach -> MissionExperience -> Assessment/Evidence -> Mastery -> review.
- Persistence: Flyway-managed H2; editorial catalog synchronizes Missions, Lessons, Exercises, Assessments and relations.
- Auth/security boundaries: local-only API at `127.0.0.1`; no auth redesign in current scope; external keys remain backend environment variables.

## Non-negotiable constraints

- Preserve CurriculumEngine, Coach, EvidenceEngine, Mastery and existing pedagogical entities.
- Preserve product shell and visual identity; no frontend redesign for editorial expansion.
- Never infer mastery from completion, elapsed time or self-assessment alone.
- Lovable-connected history must not be rewritten.

## Active scope

- Requested outcome: one active Muse product, with legacy capabilities migrated into Mission, Practice, Músicas and Studio.
- In scope: guided learning, free practice, repertoire, creation, Web Studio and a localhost Windows Reaper Agent used by the Docker API.
- Out of scope: full browser DAW, automatic audio analysis, generic remote execution, cloud synchronization or changes to Evidence/Mastery truth rules.

## Decisions

- 2026-08-13 — Keep `TeachingContentCatalog` as public composition root and split definitions by instrument — avoids a monolithic file without introducing a parallel content system.
- 2026-08-13 — Minimum coverage gate is all real tracks and stages plus structural integrity, not an arbitrary Mission count alone — follows the pedagogical quality requirement.
- 2026-08-13 — Repertoire links remain optional when no honest song relation exists — prevents artificial associations.
- 2026-08-14 — Every current Mission resolves to a mission-aligned Lesson before the technical fallback — the 77 linked Lessons now teach from the observable Exercise and musical family; 12 audited Missions have hand-authored reference material.
- 2026-08-14 — Application is an Exercise activity with transfer semantics, not a mandatory Mission string — only 32 Missions expose a structured application and duplicate textual applications were removed.
- 2026-08-14 — Assessment cardinality is optional and qualitative criteria carry persisted rubric bands — 9 exploratory Missions have no formal Assessment; 98 remain linked and 46 use rubrics.
- 2026-08-14 — User-entered BPM, accuracy, repetitions and duration are explicitly labeled configured/reported — they remain low-confidence provisional process Evidence.
- 2026-08-15 — `StudioProject` is a separate persistent aggregate linked by optional context IDs — Missions, repertoire, practice and creation remain the sources of pedagogical truth.
- 2026-08-15 — Studio recordings and takes are artifacts, never automatic Evidence or Mastery — promotion remains an explicit future assessment decision.
- 2026-08-15 — The Web Studio uses the existing recorder/metronome plus a small `@waveform-playlist/core` peak adapter — no parallel recording stack or browser DAW was introduced.
- 2026-08-15 — REAPER integration is user-configured and action-driven — it validates local paths, renders a managed `.RPP`, opens only on explicit command and distinguishes not configured, disconnected, available and connected states.
- 2026-08-16 — Default Docker Compose builds `music-os-api:local` and `music-os-web:local` from this checkout with `pull_policy: build` — `docker compose up -d` now uses current source instead of stale GHCR `latest` images; Watchtower is opt-in under `remote-updates`.
- 2026-08-20 — `/pratica` is the sole practice experience; `/sessao` and `/plano` redirect to it, while StudyPlan/Progress services no longer command learning.
- 2026-08-20 — `/musicas` is the sole repertoire experience; legacy repertoire/training routes are aliases, and Song owns section/material/Studio entry points.
- 2026-08-20 — Docker API talks only to the typed Windows Reaper Agent; `PROJECT_CONNECTED` requires a fresh ReaScript heartbeat for the matching immutable project revision.
- 2026-08-20 — REAPER project revisions are immutable managed sources; Muse never overwrites the working file, and transport is refused without a matching live project.
- 2026-08-20 — Flyway V12 owns schema and conservative cleanup of exact personal fixtures; Hibernate is validation-only and missing user history renders honest empty states.

## Current state

- Completed: 107 Missions, 77 linked mission-specific Lessons and 111 Exercises; the generic fallback remains only for skills outside the current Mission catalog.
- Completed: 32 structured Applications; 75 duplicate/text-only applications removed; 98 linked Assessments and 9 exploratory Missions without formal Assessment.
- Completed: 46 Assessments use 168 persisted rubric levels across three qualitative bands.
- Completed: original Muse stimuli cover the audited tuning, muting, lead form, acoustic accompaniment/fingerstyle/arrangement, triads/syncopation/reharmonization and drum groove/harmony/composition Missions; AudioCue supports pitched, rest and percussion tokens.
- Completed: known prerequisite shortcuts were removed from the four advanced journeys and decorative repertoire links were removed from the affected Missions.
- Completed: integrity gates include fallback/material checks, optional Assessment, structured Application, rubric persistence, template diversity, four journeys and a 24-Mission autonomy sample.
- Completed: Studio persistence, contextual resume, tracks/clips/regions/markers/takes, autosave, shared Web Audio transport/cache, managed backing audio and recorder with count-in; Flyway schema is at V12.
- Completed: contextual Studio entry points exist in eligible Mission applications, repertoire, practice sessions, creation projects and execution/recording exercises.
- Completed: localhost Windows Agent, typed project commands, immutable `.RPP` revisions, managed path mapping, persistent external IDs and Docker-to-host status polling.
- Limited: the ReaScript heartbeat was not loaded in the live REAPER process during validation; handoff/open was observed, while transport correctly remained disabled at `REAPER_OFFLINE`.
- In progress: none for the current Studio layer.
- Known issues: full frontend ESLint retains pre-existing formatting debt; changed files have zero lint errors and two Fast Refresh warnings. `npm audit` reports zero vulnerabilities.
- Completed: Docker Compose validates as a local-source build configuration; frontend Docker build uses the committed lockfile with `npm ci`.
- Verification performed: backend 79/79; frontend 11/11; Agent 2/2; clean and existing-volume Flyway V12; production and Docker builds; Docker runtime smoke; browser QA desktop/390 px; Agent offline and ReaScript-offline state transitions; real REAPER opened a managed test project.

## Next safe actions

- Add richer Studio editing only when a pedagogical workflow requires it; keep full production in REAPER.
- If Studio recordings later feed assessment, require an explicit evidence policy and never infer correctness from upload or completion alone.
