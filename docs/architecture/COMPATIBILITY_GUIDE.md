# Compatibility Guide

Este guia define regras para manter o Muse Studio funcionando durante a migração.

## Regras De Compatibilidade

- Todas as rotas atuais continuam funcionando.
- Todos os endpoints atuais continuam funcionando.
- `music-api.ts` continua exportando os hooks e tipos atuais até a migração completa.
- `WorkspaceProvider` e `useWorkspace` continuam disponíveis.
- `WorkspaceShell` continua sendo o host principal até a última fase.
- `TabBar` continua aceitando tabs baseadas em path enquanto o modelo novo amadurece.
- `navTree` continua disponível enquanto `navigation registry` novo é introduzido.
- UI primitives em `components/ui` não devem ser renomeadas em massa.

## Rotas Que Devem Permanecer Estáveis

```text
/
/diagnostico
/sessao
/plano
/diario
/biblioteca
/biblioteca/$nodeId
/skills
/mapa
/repertorio
/repertorio/$songId
/exercicios
/ouvido
/metronomo
/projetos
/projetos/$projectId
/dados
```

## API Frontend Compatível

Não quebrar exports:

- `useHomeData`
- `useInstruments`
- `useTodayPlan`
- `useSkills`
- `useLibrary`
- `useSongs`
- `useExercises`
- `useProjects`
- `useJournal`
- `useEarStats`
- `usePreferences`
- `useDataStatus`
- `updateActivity`
- `updateSkillState`
- `recordSkillEvidence`
- `recordExerciseAttempt`
- `recordEarAttempt`
- `savePreferences`
- `completeDiagnostic`
- `getSongRecommendations`
- `saveLesson`
- `saveExercise`
- `saveSong`
- `createSongPracticePlan`
- `saveProject`
- `deleteResource`
- `startPracticeSession`
- `recordSessionActivity`
- `getSessionSummary`
- `getRecordings`
- `uploadRecording`
- `downloadDataFile`
- `restoreBackup`
- `importMusicFile`
- `updatePracticeSession`
- `finishPracticeSession`

## Backend Compatível

Não quebrar:

- `/api/v1/home`
- `/api/v1/instruments`
- `/api/v1/plans/today`
- `/api/v1/plans/today/regenerate`
- `/api/v1/plans/activities/{id}`
- `/api/v1/skills`
- `/api/v1/skills/{id}`
- `/api/v1/skills/{id}/state`
- `/api/v1/skills/{id}/evidence`
- `/api/v1/library`
- `/api/v1/library/{id}`
- `/api/v1/songs`
- `/api/v1/songs/{id}`
- `/api/v1/songs/{id}/practice-plan`
- `/api/v1/exercises`
- `/api/v1/exercises/{id}`
- `/api/v1/exercises/{id}/attempts`
- `/api/v1/ear-training/stats`
- `/api/v1/ear-training/attempts`
- `/api/v1/preferences`
- `/api/v1/diagnostic`
- `/api/v1/recommendations/songs`
- `/api/v1/projects`
- `/api/v1/projects/{id}`
- `/api/v1/journal`
- `/api/v1/sessions`
- `/api/v1/sessions/{id}`
- `/api/v1/sessions/{id}/finish`
- `/api/v1/sessions/{id}/activities/{activityId}/result`
- `/api/v1/sessions/{id}/summary`
- `/api/v1/recordings`
- `/api/v1/recordings/{id}/audio`
- `/api/v1/data/backup`
- `/api/v1/data/restore`
- `/api/v1/data/journal.csv`
- `/api/v1/data/imports`
- `/api/v1/data/status`

## Coexistência Antiga/Nova

Estratégia:

- Criar novos modelos ao lado dos antigos.
- Reexportar módulos novos por caminhos antigos.
- Migrar consumidores gradualmente.
- Apagar wrappers somente após todos os imports mudarem.

Exemplo conceitual:

```text
src/lib/music-api.ts
  reexporta shared/api/*

src/components/workspace/TabBar.tsx
  reexporta workspace/tabs/WorkspaceTabs ou mantém wrapper

src/lib/nav.ts
  exporta navTree antigo e navigationRegistry novo
```

## Feature Flags Conceituais

Usar flags/configuração interna para:

- Sidebar contextual nova.
- Tab metadata novo.
- Dock.
- Tema claro.
- Mapa real.

Flags devem permitir rollback sem remover código.

