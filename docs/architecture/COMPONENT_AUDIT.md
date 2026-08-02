# Component Audit

Este documento classifica componentes, estados, rotas, providers, hooks e layouts existentes para orientar uma migração segura.

## Componentes Reutilizáveis

Workspace:

- `WorkspaceShell`: reutilizar como shell base. Precisa ser dividido depois, mas não reescrito.
- `WorkspaceSidebar`: reutilizar inicialmente. Evoluir para consumir modelo de macro contexto.
- `TabBar`: reutilizar inicialmente. Evoluir para suportar metadata de abas.
- `Inspector`: reutilizar como primeiro `InspectorHost`. Depois dividir seções.
- `CommandPalette`: reutilizar e evoluir para buscar em registry.
- `Panel`: reutilizar como primitive de painel.
- `QueryState`: reutilizar para estados de query se estiver consolidado nas telas.

Music:

- `LessonRenderer`: reutilizar como módulo de aula.
- `PracticeRecorder`: reutilizar como ferramenta contextual/dock futuro.
- `ExerciseRunner`: reutilizar como módulo de exercício.
- `InteractiveTab`: reutilizar em música e aula.
- `InteractiveScore`: reutilizar em aula.
- `AudioCuePlayer`: reutilizar como ferramenta/player.
- `Diagrams`: reutilizar em aulas/mapa.
- `CatalogEditor`: reutilizar temporariamente para edição/criação.
- `SongSuggestions`: reutilizar no repertório.

UI:

- Primitives em `components/ui/*`: preservar. Elas são base de compatibilidade.

## Componentes Que Precisam Ser Divididos

`WorkspaceShell`:

- Title bar.
- Instrument selector.
- Search trigger.
- Metronome quick control.
- Layout resizable.
- Status bar.
- Focus session shell.

`Inspector`:

- Instrument context section.
- Active skills section.
- Current song section.
- Quick notes section.

`CommandPalette`:

- Data gathering.
- Result model.
- Result groups.
- Navigation action.
- UI rendering.

`CatalogEditor`:

- Entity form model.
- Dialog shell.
- Save/delete mutations.
- Bulk/list helper.

`PracticeRecorder`:

- Media permission/recording.
- Audio analysis.
- Save/upload.
- Playback/history UI.

Large route files:

- `skills.tsx`
- `sessao.tsx`
- `diagnostico.tsx`
- `dados.tsx`
- `repertorio.tsx`
- `repertorio.$songId.tsx`

## Componentes Que Precisam Ser Unificados

Card patterns:

- Cards de plano em Home/Plano.
- Cards de música em Repertório/Home/Inspector.
- Cards de exercício em Exercícios/Skills.
- Cards de skill em Skills/Inspector.

Panel patterns:

- `Panel` já existe, mas headers/actions/empty/loading/error precisam virar convenção.

Breadcrumb:

- Está em `TabBar.tsx`; deve migrar para `workspace/navigation` ou `shared/ui/navigation`.

Status displays:

- `Meter`, `StateTag`, `StateDot` vivem em `Panel.tsx`; devem virar shared status components.

## Componentes Que Precisam Apenas Mudar De Lugar

- `Panel`, `Row`, `Meter`, `StateTag`, `StateDot`: mover de `components/workspace/Panel.tsx` para primitives compartilhadas ou `workspace/panels`.
- `Breadcrumb`: mover para `workspace/navigation` ou `shared/ui`.
- `use-metronome`: mover para `features/metronome` ou `workspace/dock/tools/metronome`.
- `nav.ts`: evoluir para `workspace/navigation/navigation-model`.
- `workspace-store.tsx`: mover para `workspace/store` depois de fatiar.

## Componentes Potencialmente Descartáveis

Nenhum componente deve ser descartado na migração inicial.

Possíveis candidatos futuros, apenas depois de substituição validada:

- Wrappers antigos de export após migração de imports.
- Estruturas duplicadas de card quando houver componente canônico.
- Redirect simples de `/mapa` se o mapa virar rota real.

## Componentes Duplicados Ou Quase Duplicados

Não há duplicação literal forte, mas há duplicação de padrões:

- List/detail com `Panel` em Biblioteca, Repertório e Projetos.
- Cards/list rows reimplementados dentro de rotas.
- Estados loading/empty/error tratados localmente.
- Ações de edição via `CatalogEditor` espalhadas por domínios.
- Mutations com `queryClient.invalidateQueries` específicas em vários componentes.

## Rotas Reutilizáveis

Preservar todas:

- `/`
- `/diagnostico`
- `/sessao`
- `/plano`
- `/diario`
- `/biblioteca`
- `/biblioteca/$nodeId`
- `/skills`
- `/mapa`
- `/repertorio`
- `/repertorio/$songId`
- `/exercicios`
- `/ouvido`
- `/metronomo`
- `/projetos`
- `/projetos/$projectId`
- `/dados`

## Estados Reutilizáveis

`workspace-store`:

- Instrumento.
- Abas.
- Sidebar/inspector visibility.
- Palette.
- Notas.
- Sessão local.
- Metrônomo.

Estados locais que devem virar feature state ou componentes:

- Seleção em `skills.tsx`.
- Seleção em `exercicios.tsx`.
- Seleção em `diario.tsx`.
- Filtros em repertório/biblioteca/projetos.
- Estado de importação em `dados.tsx`.
- Estado de sessão remota em `sessao.tsx`.

## Providers Reutilizáveis

- `QueryClientProvider`: manter.
- `WorkspaceProvider`: manter, depois dividir internamente.

## Hooks Reutilizáveis

- Todos os hooks de `music-api.ts` devem ser preservados.
- `useMetronomeEngine` deve ser preservado.
- `useMobile` existe, mas não parece central no desktop atual.

## Layouts Reutilizáveis

- `WorkspaceShell`: layout principal.
- Focus mode dentro de `WorkspaceShell`: preservar para `/sessao`.
- `ResizablePanelGroup`: preservar para sidebar/workspace/inspector.

