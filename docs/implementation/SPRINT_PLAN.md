# Sprint Plan

## Cadencia

- Sprint de 1 semana.
- PRs pequenos, idealmente 1 story por PR.
- Stories de alto risco devem ser abertas com plano de rollback explicito.
- Ao fim de cada sprint, executar smoke de rotas, tabs, Sidebar e API.

## Sprint 1 - Baseline e registry

**Stories:** IMP-001 a IMP-006.

**Objetivo:** criar a base de seguranca e a primeira infraestrutura paralela.

**Criterio de encerramento:** baseline, contratos e registry inicial revisados.

## Sprint 2 - Adapter e Sidebar

**Stories:** IMP-007 a IMP-013.

**Objetivo:** migrar a fonte de navegacao mantendo equivalencia.

**Criterio de encerramento:** Sidebar, path ativo, persistencia local e coverage audit aprovados.

## Sprint 3 - Menus contextuais

**Stories:** IMP-014 a IMP-019.

**Objetivo:** introduzir menus secundarios por contexto.

**Criterio de encerramento:** todos os contextos tem menu funcional com links reais.

## Sprint 4 - Workspace e tabs

**Stories:** IMP-020 a IMP-027.

**Objetivo:** dividir shell e evoluir tabs com storage seguro.

**Criterio de encerramento:** Workspace equivalente e tabs antigas/novas compativeis.

## Sprint 5 - Cards e primeiras features

**Stories:** IMP-028 a IMP-035.

**Objetivo:** criar primitives e extrair rotas de baixo/medio risco.

**Criterio de encerramento:** plano, diario e metronomo delegados; cards iniciais migrados.

## Sprint 6 - Features complexas

**Stories:** IMP-036 a IMP-040.

**Objetivo:** migrar rotas com detalhe, audio e fluxos operacionais.

**Criterio de encerramento:** todas as rotas principais delegam para features.

## Sprint 7 - API, Inspector e Dock

**Stories:** IMP-041 a IMP-048.

**Objetivo:** dividir API mantendo facade e preparar ferramentas persistentes.

**Criterio de encerramento:** `music-api.ts` compativel, Dock vazio seguro, metronomo adaptado.

## Sprint 8 - Tokens, limpeza e release

**Stories:** IMP-049 a IMP-055.

**Objetivo:** preparar evolucao visual, remover legado comprovado e aprovar release.

**Criterio de encerramento:** release candidate aprovada.
