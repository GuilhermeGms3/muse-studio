# Target Architecture

A arquitetura alvo evolui o Muse Studio para um workspace modular baseado em macro contextos, menus contextuais, abas e módulos. A migração deve preservar rotas, contratos e componentes úteis.

## Princípio Central

Não reescrever. Evoluir.

Preferir:

- Refatorar.
- Mover.
- Renomear.
- Reorganizar.
- Reutilizar.
- Dividir componentes grandes.
- Criar adaptadores de compatibilidade.

Evitar:

- Excluir features.
- Reescrever rotas do zero.
- Duplicar lógica de domínio.
- Quebrar endpoints existentes.
- Trocar shell inteiro de uma vez.

## Pastas: Antes E Depois

Antes:

```text
src/
  components/
    music/
    ui/
    workspace/
  lib/
  routes/
```

Depois conceitual:

```text
src/
  app/
    providers/
    router/
  shared/
    api/
    ui/
    utils/
  workspace/
    shell/
    navigation/
    tabs/
    inspector/
    dock/
    command/
    store/
  features/
    home/
    practice/
    learning/
    library/
    repertoire/
    exercises/
    ear-training/
    metronome/
    compose/
    review/
    tools/
    diagnostics/
  routes/
    compatibility routes during migration
```

Observação: a pasta alvo é uma direção. A migração deve criar fachadas e aliases gradualmente antes de mover arquivos.

## Layouts: Antes E Depois

Antes:

```text
RootComponent
  QueryClientProvider
    WorkspaceProvider
      WorkspaceShell
        Outlet
```

Depois:

```text
RootComponent
  AppProviders
    WorkspaceProvider
      WorkspaceRuntimeProvider
        WorkspaceShell
          ContextNavigation
          WorkspaceTabs
          WorkspaceOutlet
          InspectorHost
          DockHost
          CommandPalette
```

Compatibilidade:

- `WorkspaceShell` permanece como host durante toda a migração.
- `Outlet` continua renderizando as rotas atuais.
- Providers novos podem ser adicionados em volta, não substituindo tudo.

## Rotas: Antes E Depois

Antes:

```text
routes/
  index.tsx
  sessao.tsx
  plano.tsx
  diario.tsx
  biblioteca.tsx
  biblioteca.$nodeId.tsx
  skills.tsx
  mapa.tsx
  repertorio.tsx
  repertorio.$songId.tsx
  exercicios.tsx
  ouvido.tsx
  metronomo.tsx
  projetos.tsx
  projetos.$projectId.tsx
  dados.tsx
```

Depois conceitual:

```text
routes permanecem estaveis
  /
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

features fornecem telas e módulos
  features/practice/SessionWorkspace
  features/library/LibraryWorkspace
  features/learning/SkillTreeWorkspace
```

Regra:

- URLs não devem mudar na migração inicial.
- Rotas passam a delegar para workspaces por feature.

## Componentes: Antes E Depois

Antes:

```text
components/workspace/
  WorkspaceShell
  WorkspaceSidebar
  TabBar
  Inspector
  CommandPalette
  Panel
```

Depois:

```text
workspace/
  shell/WorkspaceShell
  navigation/ContextNavigation
  navigation/navigation-model
  tabs/WorkspaceTabs
  tabs/tab-model
  inspector/InspectorHost
  inspector/inspector-model
  dock/DockHost
  command/CommandPalette
  panels/Panel
```

Compatibilidade:

- Exportar wrappers com nomes antigos enquanto rotas ainda importam caminhos antigos.
- Mover internamente apenas depois que imports estiverem centralizados.

## Contextos

Arquitetura alvo:

```text
MacroContext
  Home
  Praticar
  Aprender
  Biblioteca
  Compor
  Revisar
  Ferramentas

ContextMenu
  itens por macro contexto

WorkspaceTab
  path
  title
  context
  type
  objectId?
  dirty?
  pinned?
  temporary?

WorkspaceModule
  card
  panel
  inspector-section
  dock-tool
```

## Providers

Antes:

- `QueryClientProvider`
- `WorkspaceProvider`

Depois:

- `QueryClientProvider`
- `WorkspaceProvider`
- `NavigationProvider` ou modelo derivado puro.
- `TabProvider` ou slice dentro de workspace store.
- `InspectorProvider` ou slice.
- `DockProvider` ou slice.

Regra:

- Não criar vários providers antes de extrair contratos claros.
- Primeiro fatiar tipos e selectors, depois mover estado.

## API

Arquitetura alvo:

```text
shared/api/
  client.ts
  contracts.ts
  home.ts
  practice.ts
  learning.ts
  library.ts
  repertoire.ts
  exercises.ts
  ear-training.ts
  projects.ts
  data.ts
  recordings.ts
```

Compatibilidade:

- `src/lib/music-api.ts` continua exportando a API antiga durante a transição.
- Novos módulos podem ser criados e reexportados por `music-api.ts`.
- Query keys antigas devem continuar funcionando até migração completa.

## Backend

Arquitetura alvo backend é majoritariamente preservada.

Prioridade:

- Não alterar endpoints.
- Não alterar DTOs sem versão.
- Não alterar nomes de campos.
- Não alterar semântica de status.

Possível evolução futura:

- Separar `ApiModels` por domínio.
- Separar `CatalogController` em controllers por contexto.
- Manter `/api/v1` estável ou criar `/api/v2` se contratos mudarem.

