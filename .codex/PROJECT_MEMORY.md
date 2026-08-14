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

## Current state
- Completed: expanded catalog to 26 Guitar Missions and 27 each for Acoustic, Keys and Drums; all nine LearningTracks and seven LearningStages covered per instrument (107 Missions, 77 linked Lessons, 111 Exercises and 107 Assessments).
- Completed: each added Mission composes one specific Exercise and one Assessment; existing deep Missions may contain two Exercises.
- Completed: added integrity/coverage tests and four-instrument Coach diagnostic simulations, including prior knowledge.
- In progress: none for the current editorial expansion.
- Known issues: full frontend ESLint remains red with 4,768 pre-existing CRLF/Prettier errors and 12 warnings; no files were mass-formatted.
- Verification performed: backend 71/71; frontend 7/7; TypeScript; production build; integrity/coverage report; `git diff --check`; scoped trailing-whitespace and residue searches.

## Next safe actions
- Use coverage report lists to choose future deep competencies; do not auto-generate shallow Missions for every uncovered skill.
- Reduce the remaining skill-without-Mission list in later editorial batches, prioritizing motor and perceptual prerequisites over catalog volume.
