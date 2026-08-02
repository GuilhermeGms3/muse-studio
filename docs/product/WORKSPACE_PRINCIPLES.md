# Workspace Principles

Muse Studio should behave as a workspace made of tabs, modules, cards, panels, inspector, docked tools, and global commands.

## Tab Rules

Tabs are mandatory and central.

A new tab opens when the user selects:

- A major workspace: Home, Plano, Sessão, Diário, Skill Tree, Biblioteca, Repertório, Exercícios, Ear Training, Metrônomo, Projetos, Dados.
- A concrete musical object: lesson, skill, song, song section when substantial, exercise, project, diary entry, recording collection.
- A search result that has enough substance to remain open.
- A generated work surface, such as a song practice plan or session summary.

A new tab should not open for:

- Simple toggles.
- Inline card expansion.
- Inspector-only details.
- Temporary menus.
- Small confirmations.

If a tab for the same object already exists, activate it instead of duplicating it.

## Tab Naming

Use readable object names:

- Home
- Plano de Hoje
- Sessão
- Diário
- Skill Tree
- Bends
- Canon Rock
- Alternate Picking 1
- Projeto: Ideias Prog

Avoid route-like names in the user interface.

## Module Rules

Each tab is composed of modules. Modules must have a clear reason to exist.

Good modules:

- Today's Plan
- Current Activity
- Song Sections
- Related Skills
- Practice Recorder
- Common Mistakes
- Next Requirement
- Riffs
- Versions
- Quick Notes

Poor modules:

- Generic analytics.
- Unrelated summaries.
- Dense tables where cards would support scanning.
- Permanent advanced settings.
- Decorative cards without work value.

## Cards

Cards are the primary repeated unit. Use them for:

- Songs.
- Exercises.
- Plan activities.
- Projects.
- Lessons.
- Skills in lists.
- Diary entries.
- Imported files.

Cards should be compact, scannable, and action-oriented.

## Panels

Panels frame bounded work zones:

- Skill graph.
- Song detail.
- Exercise runner.
- Practice session activity.
- Import area.
- Recording analyzer.

Panels should not become full-page dashboards.

## Inspector

The inspector should follow selection and expose secondary context:

- Current instrument.
- Active session.
- Related skills.
- Current song.
- Quick notes.
- Prerequisites.
- Next requirements.
- Recordings.
- References.

The inspector is ideal for information that is useful but not primary.

## Docked Tools

Docked tools are available across contexts but should remain compact:

- Metronome.
- Recorder.
- Audio cue player.
- Transport controls.
- Import status.
- Future tuner.
- Future Reaper bridge.

## Workspace Chrome

Persistent chrome should include:

- Product identity.
- Current instrument.
- Global search.
- Metronome quick control.
- Sidebar toggle.
- Inspector toggle.
- Status bar.
- Active tabs.

This chrome should remain calm and compact.

## UX Principles

- Start from user intent, then reveal capabilities.
- Preserve continuity between tabs, inspector, and selected object.
- Make the next useful action visible in every major state.
- Keep object relationships discoverable through links, cards, and inspector context.
- Prefer recognition over memory: show current instrument, active objective, recent work, and continue points.
- Use friction for irreversible or data-sensitive actions such as restore, delete, and final session completion.
- Treat long practice sessions as the default use case; avoid visual noise that becomes tiring over time.
- Make exploration feel natural: a skill should lead to a lesson, exercise, song, review, or next skill.

## UI Principles

- Use compact, professional density.
- Use cards for repeated modules and panels for bounded work areas.
- Keep cards purposeful, not decorative.
- Keep hierarchy visible through spacing, alignment, type scale, and state indicators.
- Use restrained dark workspace styling with practical highlights.
- Avoid oversized page sections, hero layouts, marketing composition, and corporate analytics styling.
- Put global tools in persistent chrome, dock, inspector, or command palette according to frequency.
- Make status visible but quiet.
- Keep text direct and musical.
- Use tabs as active workspace surfaces, not as a cosmetic navigation detail.
