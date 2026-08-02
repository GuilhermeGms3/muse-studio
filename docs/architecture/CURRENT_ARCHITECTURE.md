# Current Architecture

Este documento descreve a arquitetura atual do Muse Studio a partir do código existente. Ele serve como ponto de partida para uma migração evolutiva, sem reescrita.

## Stack Atual

Frontend:

- React 19.
- TanStack Router/Start.
- TanStack Query.
- Tailwind CSS 4.
- Radix UI / shadcn-style primitives.
- Lucide React.
- react-resizable-panels.
- VexFlow e Web Audio em componentes musicais.

Backend:

- Java 21.
- Spring Boot.
- H2 persistente em arquivo.
- Controllers REST em `/api/v1`.
- Services transacionais.
- Repositories Spring Data.

## Pastas Atuais

```text
src/
  components/
    music/          componentes musicais específicos
    ui/             primitives reutilizáveis de UI
    workspace/      shell, sidebar, tabs, inspector, panels, palette
  hooks/            hooks genéricos
  integrations/     integrações futuras/externas
  lib/              api client, workspace store, nav, utils
  routes/           rotas TanStack por página/objeto
backend/
  src/main/java/com/musicos/
    api/            controllers e contratos
    config/         seed/catalog setup
    domain/         entidades
    repository/     repositories
    service/        regras de negócio
```

## Arquitetura De Rotas Atual

```text
/                         Home
/diagnostico              Diagnóstico inicial
/sessao                   Sessão em foco
/plano                    Plano de estudos
/diario                   Diário
/biblioteca               Biblioteca
/biblioteca/$nodeId       Aula/conteúdo
/skills                   Skill Tree
/mapa                     Redirect para /skills
/repertorio               Repertório
/repertorio/$songId       Música
/exercicios               Exercícios
/ouvido                   Treino de ouvido
/metronomo                Metrônomo
/projetos                 Projetos
/projetos/$projectId      Projeto
/dados                    Dados e integrações
```

## Workspace Atual

O workspace atual já contém boa parte da estrutura desejada:

- `WorkspaceShell`: title bar, seletor de instrumento, busca, metrônomo rápido, sidebar, tab bar, inspector, status bar.
- `WorkspaceSidebar`: menu agrupado por `navTree`.
- `TabBar`: abas abertas baseadas em path.
- `Inspector`: contexto do instrumento, sessão, habilidades ativas, música atual e notas rápidas.
- `CommandPalette`: busca global por módulos, biblioteca, repertório, habilidades e exercícios.
- `Panel`: primitive local de painel.
- `workspace-store`: estado de instrumento, abas, sidebar, inspector, palette, notas, sessão local e metrônomo.

## Navegação Atual

Hoje a navegação está próxima, mas ainda não corresponde completamente à arquitetura de quatro níveis.

Atual:

```text
navTree
  Estacao
    Inicio
    Sessao
  Aprender
    Biblioteca
    Skill Tree
  Praticar
    Repertorio
    Exercicios
    Ear Training
    Metronomo
  Criar
    Projetos
```

Documentação alvo:

```text
Home
Praticar
Aprender
Biblioteca
Compor
Revisar
Ferramentas
```

Gap principal:

- `Biblioteca`, `Revisar` e `Ferramentas` ainda não são macro contextos de primeira classe.
- `Diário`, `Plano` e `Dados` existem como rotas, mas não aparecem integralmente no menu.
- `/mapa` existe apenas como redirect para `/skills`.

## Estado Atual

`workspace-store.tsx` concentra:

- Instrumento atual.
- Abas abertas.
- Sidebar aberta/fechada.
- Inspector aberto/fechado.
- Palette aberta/fechada.
- Notas rápidas.
- Sessão local.
- Metrônomo.

Persistência atual:

- Instrumento.
- Notas rápidas.

Não persiste ainda:

- Abas abertas.
- Ordem de abas.
- Aba ativa.
- Sidebar/inspector/dock.
- Estado contextual selecionado.

## API Client Atual

`src/lib/music-api.ts` concentra:

- Tipos de contrato frontend.
- `apiRequest`.
- Hooks TanStack Query.
- Mutations/funções de escrita.
- Upload/download/importação.

Pontos fortes:

- Contratos tipados.
- Query keys consistentes por domínio.
- Centralização de endpoints.

Pontos de atenção:

- Arquivo concentra muitos domínios.
- Tipos, hooks e mutations convivem no mesmo módulo.
- Evolução para arquitetura por feature exigirá fatiamento gradual.

## Backend Atual

Controllers:

- `HomeController`
- `CatalogController`
- `LearningController`
- `PracticeSessionController`
- `RecordingController`
- `DataManagementController`

Services principais:

- `HomeService`
- `CatalogService`
- `LearningContentService`
- `PracticeSessionService`
- `StudyPlanService`
- `ProgressEngine`
- `RepertoirePlanningService`
- `RecordingService`
- `DataManagementService`
- `DiagnosticService`
- `SongRecommendationService`

Contratos públicos:

- Todos os endpoints sob `/api/v1`.
- Records em `ApiModels`.
- Tipos espelhados no frontend em `music-api.ts`.

## Riscos Da Arquitetura Atual

- Rotas carregam muita responsabilidade de layout e regra de tela.
- Componentes musicais têm lógica local forte e podem ser difíceis de desacoplar.
- `workspace-store` mistura chrome, sessão, tabs, notas e metrônomo.
- `navTree` ainda é uma árvore de navegação simples, não um modelo de contexto.
- `styles.css` está em design system escuro, enquanto `docs/ui` define tema claro como direção primária.
- `/mapa` ainda não é uma experiência independente.

