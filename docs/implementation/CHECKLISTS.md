# Checklists de Implementacao

## Checklist padrao por story

### Antes

- Confirmar que a story esta na ordem prevista em `IMPLEMENTATION_ORDER.md`.
- Ler os docs relevantes em `docs/product`, `docs/ux`, `docs/ui` e `docs/architecture`.
- Conferir dependencias em `DEPENDENCIES.md`.
- Rodar `git status --short` e identificar mudancas nao relacionadas.
- Confirmar rollback antes do primeiro commit.
- Listar arquivos que serao tocados e manter escopo pequeno.

### Durante

- Preservar rotas atuais e endpoints `/api/v1`.
- Preservar exports publicos de `music-api.ts` e contratos de `workspace-store`.
- Evitar mover e reescrever no mesmo commit.
- Evitar mudanca visual fora de stories de UI/tokens.
- Manter adapters enquanto consumidores antigos existirem.
- Registrar qualquer comportamento alterado intencionalmente.

### Depois

- Rodar typecheck/build conforme pacote disponivel.
- Rodar testes frontend ou backend quando a story tocar essas areas.
- Executar smoke da rota afetada.
- Executar smoke de Sidebar, tabs e Command Palette quando a story tocar workspace/navegacao.
- Conferir que nenhuma feature sumiu da navegacao.
- Atualizar documentacao apenas quando a story pedir.

### Aceite

- Criterios de aceite da story cumpridos.
- Rollback comprovadamente simples.
- Diff pequeno e revisavel.
- Sem regressao conhecida nao documentada.

## Checklist por tipo de story

| Tipo | Antes | Durante | Depois | Aceite |
| --- | --- | --- | --- | --- |
| Baseline | Conferir rotas reais | Nao alterar codigo | Revisar cobertura | Rotas e fluxos mapeados |
| Navegacao | Comparar com `navTree` | Nao esconder feature | Testar todos os links | Menu equivalente ou documentado |
| Workspace | Capturar visual atual | Extrair uma parte por vez | Testar resize, mobile e palette | Layout equivalente |
| Tabs | Salvar exemplo de estado antigo | Manter `path/title` | Testar storage vazio/antigo | Compatibilidade mantida |
| Cards | Conferir specs UI | Migrar uma rota por story | Testar estados | Conteudo e acoes iguais |
| Feature | Identificar rota delegadora | Mover sem reescrever | Testar URL atual | Rota fina e funcional |
| API | Mapear hooks/endpoints | Manter facade | Testar cache/mutations | Imports antigos funcionam |
| Dock | Confirmar host vazio | Evitar impacto visual | Testar desktop/mobile | Dock vazio nao altera layout |
| Tokens | Comparar valores atuais | Usar aliases equivalentes | Testar contraste | Sem mudanca visual acidental |
| Limpeza | Provar ausencia de imports | Remover minimo | Build e smoke completo | Nenhum contrato removido |

## Checklist por story

| Story | Checklist especifico |
| --- | --- |
| IMP-001 | Rotas atuais; fluxos criticos; tabs; palette; workspace. |
| IMP-002 | Exports de `music-api`; provider/store; nav; workspace components. |
| IMP-003 | Controllers; hooks consumidores; DTOs; endpoints `/api/v1`. |
| IMP-004 | Viewports; loading; empty; error; acessibilidade. |
| IMP-005 | Nomes dos macro contextos; paralelo; typecheck. |
| IMP-006 | Cobertura de paths; labels; icones; sem consumo visual. |
| IMP-007 | Equivalencia com `navTree`; ordem; grupos; fallback. |
| IMP-008 | Campos opcionais; `path/title`; storage antigo. |
| IMP-009 | Pastas sem codigo duplicado; nenhum import alterado. |
| IMP-010 | Sidebar com todos os itens; links; rota ativa. |
| IMP-011 | Rotas detalhe; item pai ativo; redirect `/mapa`. |
| IMP-012 | Reload; fallback storage; chave documentada. |
| IMP-013 | Matriz rota -> entrada; palette; excecoes. |
| IMP-014 | Contexto Home; links reais; labels. |
| IMP-015 | Plano; sessao; repertorio; exercicios; ouvido; metronomo. |
| IMP-016 | Skills; mapa redirect; biblioteca quando aplicavel. |
| IMP-017 | Listagem; detalhe; fallback sem node. |
| IMP-018 | Projetos; detalhe; diario; dados. |
| IMP-019 | Links reais; sem placeholder falso; ferramentas atuais. |
| IMP-020 | Cabecalho equivalente; comandos; screenshot. |
| IMP-021 | Resize; inspector; mobile; children. |
| IMP-022 | Indicadores; instrumento; sessao; estado vazio. |
| IMP-023 | Atalhos; foco em inputs; palette. |
| IMP-024 | Tabs antigas; tabs novas; defaults. |
| IMP-025 | Dedupe por id/path; detalhe; close ativa. |
| IMP-026 | Tabs sem metadados; indicadores opcionais; acessibilidade. |
| IMP-027 | Storage vazio; antigo; novo; invalido; reload. |
| IMP-028 | Slots; tokens atuais; estados. |
| IMP-029 | Plano; loading; empty; error; CTAs. |
| IMP-030 | Repertorio; filtros; links de musica. |
| IMP-031 | Exercicios; selecao; runner preservado. |
| IMP-032 | Skills; biblioteca; links; densidade. |
| IMP-033 | Audio; start/stop; cleanup. |
| IMP-034 | Plano; estados; imports. |
| IMP-035 | Diario; mutacoes; erro. |
| IMP-036 | Projetos; parametro; detalhe. |
| IMP-037 | Biblioteca; node valido/invalido; renderer. |
| IMP-038 | Repertorio; score; tab; interacao. |
| IMP-039 | Exercicios; ouvido; audio; tentativas. |
| IMP-040 | Sessao; dados; diagnostico; fluxo completo. |
| IMP-041 | Base URL; erro; query client. |
| IMP-042 | Query keys; home; plano; sessao. |
| IMP-043 | Skills; biblioteca; detalhe. |
| IMP-044 | Mutations; invalidacoes; repertorio; projetos; dados. |
| IMP-045 | Exports; imports antigos; smoke completo. |
| IMP-046 | Secoes; store; screenshot. |
| IMP-047 | Dock vazio; desktop; mobile; layout. |
| IMP-048 | Audio unico; rota mantida; cleanup. |
| IMP-049 | Aliases; valores atuais; contraste. |
| IMP-050 | Workspace; states; visual equivalente. |
| IMP-051 | Cards; panels; focus; hover. |
| IMP-052 | Tema atual; preview; contraste AA. |
| IMP-053 | `rg` sem imports; build; smoke. |
| IMP-054 | Docs vs arquivos reais; rotas; contratos. |
| IMP-055 | Escopo congelado; checks completos; release aprovada. |
