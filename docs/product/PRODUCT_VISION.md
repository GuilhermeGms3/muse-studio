# Product Vision

> [!IMPORTANT]
> A direção pedagógica detalhada que complementa esta visão está registrada em
> [Direção Pedagógica do Produto](./PEDAGOGICAL_PRODUCT_DIRECTION.md),
> [Learning Missions](./LEARNING_MISSIONS.md),
> [Coach e Experiência Diária](./COACH_AND_DAILY_EXPERIENCE.md) e
> [Piloto Pedagógico — Violão, Primeiros 30 Dias](./FIRST_30_DAYS_PILOT.md).
> Os requisitos de confiança e áudio estão em
> [Motor de Domínio Baseado em Evidências](../architecture/EVIDENCE_MASTERY_ENGINE.md)
> e [Evidência de Áudio e Integração com Reaper](../architecture/AUDIO_EVIDENCE_AND_REAPER.md).

Muse Studio is a modular workspace for musicians: a personal Music OS where practice, learning, repertoire, progress, creation, recordings, and musical tools live in one connected environment.

The product must feel closer to a DAW, IDE, editor, or personal studio than to a dashboard. Its purpose is not to make musical study smaller. Its purpose is to make a powerful study system easier to inhabit for long sessions.

## Product Definition

Muse Studio is:

- A personal workspace for music learning and practice.
- A structured environment for multiple instruments.
- A place to connect concepts, exercises, songs, projects, recordings, and journal entries.
- A system for real mastery, based on evidence such as practice time, BPM, accuracy, consistency, repertoire, reviews, and self-assessment.
- A modular interface built around context, tabs, cards, panels, inspector, docked tools, and global search.

Muse Studio is not:

- A SaaS dashboard.
- A corporate admin system.
- A CRM, ERP, analytics product, or team workspace.
- A marketing website or landing page.
- A gamified course with superficial XP, coins, or player levels.

## Long-Term Vision

Muse Studio should become the musician's operating environment:

- Learn theory and technique through connected lessons.
- Practice with focused sessions, metronome, audio capture, and adaptive activities.
- Organize songs by section, technique, scale, tuning, BPM, and status.
- Track real progress through a large skill graph.
- Review history through a practice journal.
- Compose with projects, riffs, lyrics, ideas, references, and versions.
- Import and organize audio, MIDI, MusicXML, and Guitar Pro material.
- Prepare for future integration with Reaper without making that integration mandatory today.

## Product Promise

Muse Studio helps a musician answer four recurring questions:

- What should I practice now?
- What am I learning and why?
- How does this song, technique, exercise, and concept connect?
- How am I evolving over time?

The experience should feel exploratory, professional, compact, and personal.

## Product Objectives

- Help the user begin the right practice without decision fatigue.
- Preserve a complete personal record of practice, repertoire, skills, projects, recordings, and imported material.
- Connect learning content to real musical execution through exercises, songs, sessions, and reviews.
- Support multiple instruments with independent progress and contextual material.
- Make mastery measurable through musical evidence rather than superficial gamification.
- Keep creative work close to study work, so riffs and project ideas can become practice material.
- Provide a professional workspace that remains comfortable for long sessions.
- Prepare the architecture for future integrations, especially Reaper, without making them visually dominant now.

## Current Product Problems

The current product already contains many of the right pieces, but the user's mental model is not fully protected yet.

- Too many capabilities can appear at the same hierarchy level.
- Navigation is organized more by implemented feature than by musical intent.
- The user can be exposed to too much of the product before choosing a context.
- The distinction between context, tool, and workspace is not always clear.
- Progressive disclosure exists in places, but it is not yet the primary navigation philosophy.
- Some powerful areas risk feeling like separate pages instead of parts of one studio environment.
- Tools such as metronome, data import, recordings, and backup need clearer placement as contextual or utility surfaces.
- The knowledge map idea currently resolves into Skill Tree behavior and should become a more explicit exploration model over time.

## Success Criteria

The redesign succeeds when:

- The user sees a workspace, not a dashboard.
- The first screen gives orientation without exposing every feature.
- The sidebar communicates context before functionality.
- Tabs make it natural to keep multiple musical objects open.
- Tools appear when they are useful, not all at once.
- Cards and panels support scanning without turning pages into dense reports.
- No current capability is removed.
