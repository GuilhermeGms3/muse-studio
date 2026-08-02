# Release Plan

## Estrategia

A migracao deve ser entregue em releases intermediarias, cada uma com rollback claro. Nenhuma release deve depender de uma grande reescrita para funcionar.

## R0 - Baseline congelado

**Inclui:** IMP-001 a IMP-004.

**Valor:** equipe sabe o que nao pode quebrar.

**Rollback:** nao aplicavel a codigo; corrigir docs/checklists.

**Gate:** baseline revisado e aceito.

## R1 - Navegacao compativel

**Inclui:** IMP-005 a IMP-013.

**Valor:** registry futuro e Sidebar equivalente coexistem.

**Rollback:** voltar Sidebar para `navTree` direto e manter registry sem consumo.

**Gate:** todas as rotas acessiveis por navegacao e palette.

## R2 - Contextos e Workspace modular

**Inclui:** IMP-014 a IMP-027.

**Valor:** menus contextuais, shell menor e tabs futuras.

**Rollback:** remover grupos contextuais, restaurar shell inline, desativar storage v2.

**Gate:** workspace smoke completo, tabs antigas compativeis.

## R3 - Features delegadas

**Inclui:** IMP-028 a IMP-040.

**Valor:** primitives visuais e rotas delegadoras para features.

**Rollback:** restaurar JSX nas rotas afetadas uma a uma.

**Gate:** todas as URLs atuais renderizam e fluxos musicais passam.

## R4 - API modular e ferramentas

**Inclui:** IMP-041 a IMP-048.

**Valor:** API frontend modular com facade, Inspector dividido, Dock preparado.

**Rollback:** devolver hooks para `music-api.ts` e desregistrar Dock/metronomo.

**Gate:** todos os imports antigos funcionam e nao ha conflito de audio.

## R5 - Visual foundation e release candidate

**Inclui:** IMP-049 a IMP-055.

**Valor:** tokens semanticos, preview visual controlado e limpeza final.

**Rollback:** desativar preview claro, restaurar wrappers removidos, abrir hotfix pequeno.

**Gate:** release candidate sem regressao bloqueante.

## Criterios globais de release

- Build e typecheck aprovados.
- Testes backend aprovados quando backend for tocado.
- Smoke de todas as rotas atuais aprovado.
- Sidebar, tabs, Command Palette e Workspace aprovados.
- `music-api.ts` preserva exports usados.
- Nenhum endpoint `/api/v1` foi quebrado.
- Tema atual continua disponivel.
