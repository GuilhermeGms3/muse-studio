# Ordem de Implementacao

## Principio de ordem

A migracao deve sair do menor risco para o maior risco:

1. primeiro documentar baseline e contratos;
2. depois adicionar estruturas paralelas;
3. em seguida trocar consumidores com adapters equivalentes;
4. so entao mover responsabilidades de tela para features;
5. por ultimo dividir API, alterar tokens e limpar legado.

## Sequencia obrigatoria

| Ordem | Story | Por que vem aqui | Bloqueia |
| --- | --- | --- | --- |
| 01 | IMP-001 | Sem baseline nao ha como provar nao regressao. | IMP-002, IMP-003, IMP-004 |
| 02 | IMP-002 | Frontend precisa de contratos antes de tipos e tabs. | IMP-005, IMP-008 |
| 03 | IMP-003 | API precisa estar congelada antes de split frontend. | IMP-041 |
| 04 | IMP-004 | Visual baseline precisa existir antes de cards/tokens. | IMP-028, IMP-049 |
| 05 | IMP-005 | Macro contextos sao base do registry. | IMP-006, IMP-009 |
| 06 | IMP-006 | Registry novo precisa existir antes do adapter. | IMP-007 |
| 07 | IMP-007 | Adapter reduz risco da troca na Sidebar. | IMP-010 |
| 08 | IMP-008 | Modelo de tabs precisa aceitar futuro shape. | IMP-024 |
| 09 | IMP-009 | Features precisam de destino antes de extrair rotas. | IMP-033 a IMP-040 |
| 10 | IMP-010 | Primeira troca real de consumo, equivalente. | IMP-011, IMP-012, IMP-013 |
| 11 | IMP-011 | Matching ativo valida rotas detalhe. | IMP-014 a IMP-019 |
| 12 | IMP-012 | Preferencia local e isolada e nao bloqueia menus. | Nenhum critico |
| 13 | IMP-013 | Garante que menus novos nao escondem rotas. | IMP-014 a IMP-019 |
| 14 | IMP-014 | Home e o menu contextual de menor risco. | IMP-015 a IMP-018 |
| 15 | IMP-015 | Praticar conecta maior parte dos fluxos diarios. | IMP-019 |
| 16 | IMP-016 | Aprender prepara skills/biblioteca. | IMP-017 |
| 17 | IMP-017 | Biblioteca exige detalhe dinamico. | IMP-018 |
| 18 | IMP-018 | Compor/Revisar dependem de padrao contextual estabilizado. | Nenhum critico |
| 19 | IMP-019 | Ferramentas prepara Dock futuro. | IMP-048 |
| 20 | IMP-020 | Workspace deve ser dividido antes de body/dock. | IMP-021, IMP-022, IMP-023 |
| 21 | IMP-021 | Body separado e pre-requisito para Dock. | IMP-047 |
| 22 | IMP-022 | Indicadores isolados reduzem risco de tabs/dock. | Nenhum critico |
| 23 | IMP-023 | Atalhos centralizados evitam conflito com ferramentas. | IMP-047 |
| 24 | IMP-024 | Campos opcionais antes de dedupe. | IMP-025, IMP-026 |
| 25 | IMP-025 | Dedupe estabilizado antes de persistir. | IMP-027 |
| 26 | IMP-026 | Indicadores podem entrar antes de storage. | Nenhum critico |
| 27 | IMP-027 | Storage versionado so apos modelo e dedupe. | IMP-055 |
| 28 | IMP-028 | Primitives antes de migrar cards. | IMP-029 a IMP-032 |
| 29 | IMP-029 | Plano e primeira validacao de cards. | IMP-034 |
| 30 | IMP-030 | Repertorio lista antes de feature repertorio. | IMP-038 |
| 31 | IMP-031 | Exercicios antes de feature exercises. | IMP-039 |
| 32 | IMP-032 | Skills/biblioteca antes de feature biblioteca. | IMP-037 |
| 33 | IMP-033 | Metronomo e feature de menor risco. | IMP-048 |
| 34 | IMP-034 | Plano depende de cards estabilizados. | IMP-040 |
| 35 | IMP-035 | Diario prepara Revisar. | IMP-040 |
| 36 | IMP-036 | Projetos isolam Compor. | IMP-044 |
| 37 | IMP-037 | Biblioteca exige cards e route detail testados. | IMP-043 |
| 38 | IMP-038 | Repertorio exige cards e componentes musicais estaveis. | IMP-044 |
| 39 | IMP-039 | Exercicios/ouvido exigem audio e runner. | IMP-040 |
| 40 | IMP-040 | Fecha migracao de rotas complexas. | IMP-044 |
| 41 | IMP-041 | Cliente compartilhado vem antes dos dominios API. | IMP-042 a IMP-044 |
| 42 | IMP-042 | Home/pratica e primeiro split API. | IMP-045 |
| 43 | IMP-043 | Aprendizado/biblioteca depois da feature biblioteca. | IMP-045 |
| 44 | IMP-044 | Repertorio/projetos/dados dependem de rotas migradas. | IMP-045 |
| 45 | IMP-045 | Facade validada antes de limpeza. | IMP-053 |
| 46 | IMP-046 | Inspector dividido antes de Dock. | IMP-047 |
| 47 | IMP-047 | Dock vazio antes de ferramenta real. | IMP-048 |
| 48 | IMP-048 | Metronomo no Dock depois da feature e host. | IMP-055 |
| 49 | IMP-049 | Tokens aliases antes de qualquer migracao visual. | IMP-050, IMP-051 |
| 50 | IMP-050 | Workspace usa aliases antes do tema claro. | IMP-052 |
| 51 | IMP-051 | Cards/panels usam aliases antes do tema claro. | IMP-052 |
| 52 | IMP-052 | Tema claro so como preview apos aliases. | IMP-053 |
| 53 | IMP-053 | Limpeza so apos facade e tokens validados. | IMP-054 |
| 54 | IMP-054 | Docs finais apos codigo estabilizado. | IMP-055 |
| 55 | IMP-055 | Release candidate e ultimo passo. | Release |

## Bloqueios principais

- IMP-010 nao deve iniciar sem IMP-007 aceito.
- IMP-027 nao deve iniciar sem IMP-025 aceito.
- IMP-037, IMP-038 e IMP-039 nao devem iniciar sem seus cards/fluxos validados.
- IMP-044 nao deve iniciar antes das rotas de repertorio, projetos e dados estarem delegadas.
- IMP-053 nao deve iniciar enquanto `rg` encontrar consumidores da camada antiga.
