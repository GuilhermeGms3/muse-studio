# Dependency Map

Este documento mostra dependências atuais e dependências futuras. Ele orienta a ordem de migração.

## Árvore Atual De Frontend

```text
RootComponent
  QueryClientProvider
    WorkspaceProvider
      WorkspaceShell
        useWorkspace
        useInstruments
        useMetronomeEngine
        titleForPath
        WorkspaceSidebar
          navTree
          TanStack Link
        TabBar
          useWorkspace.tabs
          closeTab
          titleForPath via Breadcrumb
        Inspector
          useWorkspace
          useInstruments
          useSkills
          useSongs
          useTodayPlan
          Panel/Row/Meter/StateTag
        CommandPalette
          useWorkspace
          navFlat
          useLibrary
          useSongs
          useExercises
          useSkills
        Outlet
          route components
```

## Árvore Atual De Dados

```text
Route/Component
  music-api hook/function
    apiRequest
      /api/v1 endpoint
        Controller
          Service
            Repository
              Domain Entity
```

## Rotas E Dependências Principais

```text
/                         useHomeData, usePreferences, useWorkspace
/diagnostico              completeDiagnostic, useWorkspace
/sessao                   start/update/finish session, preferences, workspace session
/plano                    useTodayPlan, useSkills, updateActivity
/diario                   useJournal, useInstruments
/biblioteca               useLibrary, CatalogEditor
/biblioteca/$nodeId       useLibrary, useSkills, LessonRenderer, CatalogEditor
/skills                   useSkills, useLibrary, useExercises, useSongs, recordSkillEvidence
/mapa                     redirect -> /skills
/repertorio               useSongs, useInstruments, SongSuggestions, CatalogEditor
/repertorio/$songId       useSongs, usePreferences, createSongPracticePlan, InteractiveTab
/exercicios               useExercises, ExerciseRunner, CatalogEditor
/ouvido                   useEarStats, recordEarAttempt, PracticeRecorder
/metronomo                useWorkspace.metronome
/projetos                 useProjects, CatalogEditor
/projetos/$projectId      useProjects, CatalogEditor
/dados                    useDataStatus, import/backup/restore/export, workspace metronome
```

## Backend Dependency Tree

```text
Controllers
  HomeController
    HomeService
      Coach
      MissionExperienceRepository
      InstrumentProfileRepository

  CatalogController
    CatalogService
      InstrumentRepository
      LibraryContentRepository
      SongRepository
      ExerciseRepository
      MusicProjectRepository
      JournalEntryRepository
      ViewMapper

  LearningController
    LearningContentService
    DiagnosticService
    SongRecommendationService
    LearningWorkspaceService
    MissionExperienceService
    AssessmentService

  PracticeSessionController
    PracticeSessionService
      JournalEntryRepository
      PracticeSessionRepository

  StudioController
    StudioService
    ReaperBridge

  RecordingController
    RecordingService

  DataManagementController
    DataManagementService
      LearningContentService
      repositories
```

## Pode Ser Alterado Isoladamente

Baixo acoplamento:

- Documentação.
- Novos arquivos de tipos/modelos de navegação.
- Novos adapters que reexportam API antiga.
- Novos componentes canônicos não usados ainda.
- Novos testes de caracterização.
- Adicionar metadata em `nav.ts` mantendo export antigo.
- Criar `context registry` paralelo ao `navTree`.

## Exige Migração Conjunta

Médio/alto acoplamento:

- Alterar `workspace-store` sem adaptador: afeta Shell, Tabs, Inspector, Palette e rotas.
- Alterar `music-api.ts`: afeta quase todas as rotas.
- Alterar `Panel.tsx`: afeta rotas e Inspector.
- Alterar `WorkspaceShell`: afeta todo app.
- Alterar endpoints backend: afeta hooks e rotas.
- Alterar formato de `OpenTab`: afeta Shell, TabBar, store e navegação.

## Sequência De Dependências Recomendada

```text
1. Testes e documentação de contratos
2. Navigation metadata paralelo
3. Workspace/tab model com compatibilidade
4. Contextual sidebar consumindo modelo novo
5. Command palette consumindo registry
6. Extrair primitives de cards/panels sem trocar rotas
7. Migrar uma feature por vez para folders novos
8. Dividir workspace-store em slices
9. Evoluir Inspector e Dock
10. Migrar design tokens/tema
```

## Contratos Que Não Podem Quebrar

Frontend:

- Props públicas de componentes reutilizados durante a migração.
- Exports de `music-api.ts`.
- Shape inicial de `OpenTab` enquanto TabBar antigo existir.
- `WorkspaceProvider` e `useWorkspace`.
- Rotas TanStack existentes.

Backend:

- URLs `/api/v1`.
- Records de response/request em `ApiModels`.
- Semântica de status de sessão.
- Skill states.
- Instrument IDs.
- Campos de Song, Skill, Exercise, LibraryContent, Project, Journal, Recording.
