# Roadmap de Migracao

## Premissas

- Semanas consideram capacidade de uma pequena equipe frontend/fullstack.
- Cada story deve caber em poucas horas e virar PR pequeno.
- Stories de alto risco devem entrar no inicio do dia/sprint para haver tempo de rollback.
- Backend so deve ser tocado quando necessario para validacao de contratos; a migracao proposta e majoritariamente frontend.

## Semana 1 - Baseline e infraestrutura inicial

**Foco:** provar estado atual e preparar estruturas paralelas.

**Stories:** IMP-001, IMP-002, IMP-003, IMP-004, IMP-005, IMP-006.

**Marco:** baseline confiavel e macro contextos/registry inicial criados sem impacto visual.

## Semana 2 - Adapter e Sidebar equivalente

**Foco:** trocar fonte de navegacao com comportamento equivalente.

**Stories:** IMP-007, IMP-008, IMP-009, IMP-010, IMP-011, IMP-012, IMP-013.

**Marco:** Sidebar usa caminho de migracao seguro e todas as rotas seguem acessiveis.

## Semana 3 - Menus contextuais

**Foco:** adicionar menus por contexto, um grupo por vez.

**Stories:** IMP-014, IMP-015, IMP-016, IMP-017, IMP-018, IMP-019.

**Marco:** macro navegacao e contexto secundario coexistem sem esconder features.

## Semana 4 - Workspace Shell e Tabs

**Foco:** reduzir acoplamento do shell e preparar tabs ricas.

**Stories:** IMP-020, IMP-021, IMP-022, IMP-023, IMP-024, IMP-025.

**Marco:** shell dividido em partes pequenas e tabs compativeis com modelo futuro.

## Semana 5 - Persistencia de tabs e Cards

**Foco:** finalizar base de tabs e introduzir primitives visuais.

**Stories:** IMP-026, IMP-027, IMP-028, IMP-029, IMP-030.

**Marco:** storage de tabs versionado e primeiros cards migrados sem regressao.

## Semana 6 - Cards restantes e features simples

**Foco:** completar migracao inicial de cards e extrair rotas de menor risco.

**Stories:** IMP-031, IMP-032, IMP-033, IMP-034, IMP-035.

**Marco:** Plano, diario e metronomo ja estao em modulos de feature.

## Semana 7 - Features medias e complexas

**Foco:** projetos, biblioteca e repertorio.

**Stories:** IMP-036, IMP-037, IMP-038.

**Marco:** rotas com detalhe dinamico delegam para features sem mudar URLs.

## Semana 8 - Features finais e API base

**Foco:** exercicios, ouvido, sessao, dados, diagnostico e cliente API.

**Stories:** IMP-039, IMP-040, IMP-041.

**Marco:** todas as rotas principais estao delegadas e API base esta pronta.

## Semana 9 - Split de API e Inspector/Dock

**Foco:** modularizar hooks e preparar ferramentas persistentes.

**Stories:** IMP-042, IMP-043, IMP-044, IMP-045, IMP-046, IMP-047.

**Marco:** `music-api.ts` preserva facade e Dock vazio nao impacta layout.

## Semana 10 - Ferramentas, tokens e preview visual

**Foco:** metronomo no Dock, tokens semanticos e preview claro.

**Stories:** IMP-048, IMP-049, IMP-050, IMP-051, IMP-052.

**Marco:** base visual futura pronta sem substituir tema atual.

## Semana 11 - Limpeza e candidata de release

**Foco:** remover legado comprovado, atualizar docs e validar release.

**Stories:** IMP-053, IMP-054, IMP-055.

**Marco:** migracao concluida e candidata de release aprovada.
