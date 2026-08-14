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
- Requested outcome: substantial, instrument-specific pedagogical content across every real track and stage.
- In scope: modular editorial Missions/Exercises/Assessments, valid repertoire links, structural coverage and Coach progression tests.
- Out of scope: redesign, gamification, CMS, new pedagogical engine, new endpoints or schema changes.

## Decisions
- 2026-08-13 — Keep `TeachingContentCatalog` as public composition root and split definitions by instrument — avoids a monolithic file without introducing a parallel content system.
- 2026-08-13 — Minimum coverage gate is all real tracks and stages plus structural integrity, not an arbitrary Mission count alone — follows the pedagogical quality requirement.
- 2026-08-13 — Repertoire links remain optional when no honest song relation exists — prevents artificial associations.
- 2026-08-14 — Every current Mission resolves to a mission-aligned Lesson before the technical fallback — the 77 linked Lessons now teach from the observable Exercise and musical family; 12 audited Missions have hand-authored reference material.
- 2026-08-14 — Application is an Exercise activity with transfer semantics, not a mandatory Mission string — only 32 Missions expose a structured application and duplicate textual applications were removed.
- 2026-08-14 — Assessment cardinality is optional and qualitative criteria carry persisted rubric bands — 9 exploratory Missions have no formal Assessment; 98 remain linked and 46 use rubrics.
- 2026-08-14 — User-entered BPM, accuracy, repetitions and duration are explicitly labeled configured/reported — they remain low-confidence provisional process Evidence.

## Current state
- Completed: 107 Missions, 77 linked mission-specific Lessons and 111 Exercises; the generic fallback remains only for skills outside the current Mission catalog.
- Completed: 32 structured Applications; 75 duplicate/text-only applications removed; 98 linked Assessments and 9 exploratory Missions without formal Assessment.
- Completed: 46 Assessments use 168 persisted rubric levels across three qualitative bands; Flyway schema is at V6.
- Completed: original Muse stimuli cover the audited tuning, muting, lead form, acoustic accompaniment/fingerstyle/arrangement, triads/syncopation/reharmonization and drum groove/harmony/composition Missions; AudioCue supports pitched, rest and percussion tokens.
- Completed: known prerequisite shortcuts were removed from the four advanced journeys and decorative repertoire links were removed from the affected Missions.
- Completed: integrity gates include fallback/material checks, optional Assessment, structured Application, rubric persistence, template diversity, four journeys and a 24-Mission autonomy sample.
- In progress: none for the current editorial expansion.
- Known issues: full frontend ESLint remains red with 4,768 pre-existing CRLF/Prettier errors and 12 warnings; no files were mass-formatted.
- Verification performed: backend 76/76; frontend 7/7; scoped ESLint; production build; integrity/coverage report; `git diff --check`.

## Next safe actions
- Use coverage report lists to choose future deep competencies; do not auto-generate shallow Missions for every uncovered skill.
- Reduce the remaining skill-without-Mission list in later editorial batches, prioritizing motor and perceptual prerequisites over catalog volume.
