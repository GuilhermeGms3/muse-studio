# Backlog de Implementacao - Muse Studio

## Objetivo

Transformar a estrategia de produto, UX, UI e arquitetura em um backlog tecnico executavel em pequenas entregas. Este plano assume evolucao incremental do Muse Studio, preservando codigo, rotas, APIs, providers, hooks e comportamento atual durante toda a migracao.

## Regras de execucao

- Nenhuma story deve alterar dezenas de arquivos.
- Nenhuma story deve remover funcionalidade existente.
- Nenhuma story deve misturar arquitetura, UI visual e regra de negocio.
- Toda story deve ter rollback simples: reverter um commit pequeno ou desativar um adapter/flag.
- As rotas atuais devem continuar funcionando: `/`, `/diagnostico`, `/sessao`, `/plano`, `/diario`, `/biblioteca`, `/biblioteca/$nodeId`, `/skills`, `/mapa`, `/repertorio`, `/repertorio/$songId`, `/exercicios`, `/ouvido`, `/metronomo`, `/projetos`, `/projetos/$projectId`, `/dados`.
- Os contratos de `src/lib/music-api.ts`, `src/lib/workspace-store.tsx`, `src/lib/nav.ts`, `src/components/workspace/WorkspaceShell.tsx`, `WorkspaceSidebar`, `TabBar`, `Inspector` e `CommandPalette` devem permanecer compativeis ate a fase final.

## Epics

| Epic | Nome | Resultado esperado |
| --- | --- | --- |
| EPIC 01 | Baseline e Seguranca | Contratos atuais documentados, smoke checks definidos e regressao controlada. |
| EPIC 02 | Infraestrutura de Migracao | Tipos, registries e adapters novos coexistindo com a arquitetura atual. |
| EPIC 03 | Navegacao Principal | Macro contextos introduzidos sem quebrar `navTree` nem rotas existentes. |
| EPIC 04 | Menus Contextuais | Menus por contexto adicionados de forma incremental e testavel. |
| EPIC 05 | Workspace Shell | Shell dividido em partes pequenas mantendo o mesmo comportamento externo. |
| EPIC 06 | Sistema de Tabs | Modelo de abas evoluido com compatibilidade para path/title atuais. |
| EPIC 07 | Cards e Panels | Primitivos canonicos introduzidos e migrados rota por rota. |
| EPIC 08 | Modulos de Feature | Rotas delegam gradualmente para `features/*` sem mudar URLs. |
| EPIC 09 | API Frontend | `music-api.ts` vira facade compativel sobre modulos menores. |
| EPIC 10 | Inspector, Dock e Ferramentas | Inspector e ferramentas sao modularizados sem substituir telas existentes. |
| EPIC 11 | Tokens Visuais | Tokens semanticos entram antes de qualquer mudanca visual ampla. |
| EPIC 12 | Limpeza e Release | Wrappers antigos sao removidos apenas depois de validacao completa. |

## Backlog resumido

| Ordem | Story | Titulo | Epic | Estimativa | Complexidade |
| --- | --- | --- | --- | --- | --- |
| 01 | IMP-001 | Registrar baseline de rotas e fluxos criticos | EPIC 01 | 2h | Baixa |
| 02 | IMP-002 | Registrar contratos publicos de frontend | EPIC 01 | 2h | Baixa |
| 03 | IMP-003 | Registrar contratos de API backend | EPIC 01 | 2h | Baixa |
| 04 | IMP-004 | Criar matriz de validacao visual por rota | EPIC 01 | 2h | Baixa |
| 05 | IMP-005 | Adicionar tipos de macro contexto em paralelo | EPIC 02 | 3h | Baixa |
| 06 | IMP-006 | Criar registry de navegacao sem consumo visual | EPIC 02 | 3h | Media |
| 07 | IMP-007 | Criar adapter entre registry e `navTree` | EPIC 02 | 3h | Media |
| 08 | IMP-008 | Adicionar metadados compativeis para tabs | EPIC 02 | 3h | Media |
| 09 | IMP-009 | Criar esqueleto de pastas de features sem mover telas | EPIC 02 | 2h | Baixa |
| 10 | IMP-010 | Conectar Sidebar ao adapter em modo equivalente | EPIC 03 | 3h | Media |
| 11 | IMP-011 | Preservar selecao ativa da Sidebar por path | EPIC 03 | 2h | Media |
| 12 | IMP-012 | Persistir estado recolhido da Sidebar sem mudar layout | EPIC 03 | 2h | Baixa |
| 13 | IMP-013 | Auditar cobertura de rotas na navegacao e palette | EPIC 03 | 2h | Baixa |
| 14 | IMP-014 | Adicionar menu contextual de Home | EPIC 04 | 2h | Baixa |
| 15 | IMP-015 | Adicionar menu contextual de Praticar | EPIC 04 | 3h | Media |
| 16 | IMP-016 | Adicionar menu contextual de Aprender | EPIC 04 | 3h | Media |
| 17 | IMP-017 | Adicionar menu contextual de Biblioteca | EPIC 04 | 3h | Media |
| 18 | IMP-018 | Adicionar menu contextual de Compor e Revisar | EPIC 04 | 3h | Media |
| 19 | IMP-019 | Adicionar menu contextual de Ferramentas | EPIC 04 | 2h | Baixa |
| 20 | IMP-020 | Isolar cabecalho do Workspace | EPIC 05 | 3h | Media |
| 21 | IMP-021 | Isolar area central do Workspace | EPIC 05 | 3h | Media |
| 22 | IMP-022 | Isolar status e indicadores do Workspace | EPIC 05 | 2h | Baixa |
| 23 | IMP-023 | Centralizar atalhos do Workspace | EPIC 05 | 3h | Media |
| 24 | IMP-024 | Adicionar campos opcionais ao modelo de tabs | EPIC 06 | 3h | Media |
| 25 | IMP-025 | Ajustar abertura de tabs com deduplicacao compativel | EPIC 06 | 3h | Media |
| 26 | IMP-026 | Adicionar indicadores visuais opcionais de tab | EPIC 06 | 2h | Baixa |
| 27 | IMP-027 | Persistir tabs com versao de storage | EPIC 06 | 3h | Alta |
| 28 | IMP-028 | Criar primitivos canonicos de Card e Panel | EPIC 07 | 3h | Media |
| 29 | IMP-029 | Migrar cards do plano diario | EPIC 07 | 3h | Media |
| 30 | IMP-030 | Migrar cards de repertorio | EPIC 07 | 3h | Media |
| 31 | IMP-031 | Migrar cards de exercicios | EPIC 07 | 3h | Media |
| 32 | IMP-032 | Migrar cards de skills e biblioteca | EPIC 07 | 3h | Media |
| 33 | IMP-033 | Extrair modulo de metronomo | EPIC 08 | 3h | Baixa |
| 34 | IMP-034 | Extrair modulo de plano diario | EPIC 08 | 3h | Media |
| 35 | IMP-035 | Extrair modulo de diario | EPIC 08 | 3h | Media |
| 36 | IMP-036 | Extrair modulos de projetos | EPIC 08 | 4h | Media |
| 37 | IMP-037 | Extrair modulos de biblioteca | EPIC 08 | 4h | Alta |
| 38 | IMP-038 | Extrair modulos de repertorio | EPIC 08 | 4h | Alta |
| 39 | IMP-039 | Extrair modulos de exercicios e ouvido | EPIC 08 | 4h | Alta |
| 40 | IMP-040 | Extrair modulos de sessao, dados e diagnostico | EPIC 08 | 4h | Alta |
| 41 | IMP-041 | Criar cliente API compartilhado | EPIC 09 | 3h | Media |
| 42 | IMP-042 | Separar modulo API de home e pratica | EPIC 09 | 3h | Media |
| 43 | IMP-043 | Separar modulo API de aprendizado e biblioteca | EPIC 09 | 3h | Media |
| 44 | IMP-044 | Separar modulo API de repertorio, projetos e dados | EPIC 09 | 4h | Alta |
| 45 | IMP-045 | Validar facade `music-api.ts` | EPIC 09 | 2h | Alta |
| 46 | IMP-046 | Dividir secoes do Inspector | EPIC 10 | 3h | Media |
| 47 | IMP-047 | Criar host de Dock vazio | EPIC 10 | 3h | Media |
| 48 | IMP-048 | Adaptar metronomo como ferramenta de Dock | EPIC 10 | 3h | Alta |
| 49 | IMP-049 | Adicionar aliases de tokens semanticos | EPIC 11 | 3h | Media |
| 50 | IMP-050 | Migrar Workspace para tokens semanticos | EPIC 11 | 3h | Media |
| 51 | IMP-051 | Migrar cards e panels para tokens semanticos | EPIC 11 | 3h | Media |
| 52 | IMP-052 | Ativar preview de tema claro sem substituir tema atual | EPIC 11 | 4h | Alta |
| 53 | IMP-053 | Remover wrappers obsoletos comprovadamente sem uso | EPIC 12 | 3h | Alta |
| 54 | IMP-054 | Atualizar documentacao arquitetural apos migracao | EPIC 12 | 2h | Baixa |
| 55 | IMP-055 | Executar candidata de release e congelamento | EPIC 12 | 4h | Alta |

## Definition of Ready

- A story tem escopo de ate 5 arquivos, salvo quando for documentacao ou validacao.
- As dependencias anteriores estao concluidas.
- O rollback esta definido antes do inicio.
- Os contratos afetados estao listados.
- Existe criterio objetivo de aceite.

## Definition of Done

- Funcionalidades atuais permanecem acessiveis.
- Rotas e endpoints mantem compatibilidade.
- Checks acordados para a story foram executados.
- Mudancas sao revisaveis isoladamente.
- Risco residual foi documentado na story ou PR.
