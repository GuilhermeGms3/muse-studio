# Epics de Implementacao

## EPIC 01 - Baseline e Seguranca

**Objetivo:** criar uma linha de base antes de qualquer mudanca estrutural.

**Inclui:** rotas atuais, contratos de hooks, contratos de endpoints, fluxos criticos, smoke visual.

**Nao inclui:** migracao de componentes, mudanca visual, alteracao de estado.

**Stories:** IMP-001 a IMP-004.

**Conclusao:** a equipe consegue provar rapidamente que uma story posterior nao removeu funcionalidades.

## EPIC 02 - Infraestrutura de Migracao

**Objetivo:** adicionar estruturas novas em paralelo, sem substituir as antigas.

**Inclui:** tipos de macro contexto, registry de navegacao, adapter para `navTree`, metadados de tabs e esqueleto `features/*`.

**Nao inclui:** troca visual da Sidebar, movimentacao de rotas ou mudanca de comportamento.

**Stories:** IMP-005 a IMP-009.

**Conclusao:** arquitetura futura existe como camada compatibilidade-first.

## EPIC 03 - Navegacao Principal

**Objetivo:** fazer a Sidebar consumir a nova fonte de navegacao mantendo a experiencia atual.

**Inclui:** adapter equivalente, selecao ativa por path, estado recolhido e auditoria de cobertura.

**Nao inclui:** redesign visual amplo ou exclusao de `src/lib/nav.ts`.

**Stories:** IMP-010 a IMP-013.

**Conclusao:** a navegacao nova e a antiga apontam para o mesmo conjunto funcional.

## EPIC 04 - Menus Contextuais

**Objetivo:** introduzir menus secundarios por macro contexto em entregas pequenas.

**Inclui:** Home, Praticar, Aprender, Biblioteca, Compor, Revisar e Ferramentas.

**Nao inclui:** alteracao de URL, permissao de esconder funcionalidades ou logica de backend.

**Stories:** IMP-014 a IMP-019.

**Conclusao:** cada contexto tem entradas coerentes e reversiveis.

## EPIC 05 - Workspace Shell

**Objetivo:** reduzir o tamanho e o acoplamento de `WorkspaceShell` por extracoes pequenas.

**Inclui:** cabecalho, area central, status/indicadores e atalhos.

**Nao inclui:** troca do host principal nem mudanca do contrato de props.

**Stories:** IMP-020 a IMP-023.

**Conclusao:** `WorkspaceShell` continua sendo o host, mas sua composicao fica mais clara.

## EPIC 06 - Sistema de Tabs

**Objetivo:** evoluir o modelo de tabs para suportar estados ricos sem quebrar `path/title`.

**Inclui:** campos opcionais, deduplicacao, indicadores e storage versionado.

**Nao inclui:** remocao do formato atual nem mudanca obrigatoria nas rotas.

**Stories:** IMP-024 a IMP-027.

**Conclusao:** tabs antigas e novas coexistem com migracao gradual.

## EPIC 07 - Cards e Panels

**Objetivo:** introduzir primitivos canonicos e migrar usos por area.

**Inclui:** Card/Panel canonicos, plano, repertorio, exercicios, skills e biblioteca.

**Nao inclui:** redesenho de todas as telas ao mesmo tempo.

**Stories:** IMP-028 a IMP-032.

**Conclusao:** superficies repetidas seguem contrato visual comum.

## EPIC 08 - Modulos de Feature

**Objetivo:** mover responsabilidade de telas para `src/features/*` sem alterar URLs.

**Inclui:** metronomo, plano, diario, projetos, biblioteca, repertorio, exercicios, ouvido, sessao, dados e diagnostico.

**Nao inclui:** reescrita de telas, remocao de rotas ou mudanca de endpoints.

**Stories:** IMP-033 a IMP-040.

**Conclusao:** rotas viram delegadoras finas.

## EPIC 09 - API Frontend

**Objetivo:** quebrar `music-api.ts` em modulos por dominio preservando facade.

**Inclui:** cliente compartilhado, modulos de dominio e validacao de exports.

**Nao inclui:** mudanca de endpoint backend ou renomeacao publica de hooks.

**Stories:** IMP-041 a IMP-045.

**Conclusao:** importadores existentes continuam funcionando.

## EPIC 10 - Inspector, Dock e Ferramentas

**Objetivo:** preparar ferramentas persistentes sem deslocar funcionalidades atuais.

**Inclui:** secoes do Inspector, host de Dock e adaptacao inicial do metronomo.

**Nao inclui:** substituicao da rota `/metronomo` ou implementacao de ferramentas novas grandes.

**Stories:** IMP-046 a IMP-048.

**Conclusao:** ferramentas podem aparecer em Dock sem apagar telas dedicadas.

## EPIC 11 - Tokens Visuais

**Objetivo:** permitir evolucao visual com controle de risco.

**Inclui:** aliases semanticos, migracao de Workspace, migracao de Cards/Panels e preview de tema claro.

**Nao inclui:** ativar novo tema como default sem validacao de acessibilidade.

**Stories:** IMP-049 a IMP-052.

**Conclusao:** mudancas visuais passam por tokens, nao por CSS disperso.

## EPIC 12 - Limpeza e Release

**Objetivo:** fechar a migracao removendo apenas o que estiver comprovadamente obsoleto.

**Inclui:** remocao segura, atualizacao de docs e candidata de release.

**Nao inclui:** novas features ou refactors oportunistas.

**Stories:** IMP-053 a IMP-055.

**Conclusao:** arquitetura alvo vira arquitetura real sem camada antiga sem uso.
