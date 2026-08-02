# User Stories Tecnicas

## Convencoes

- **Estimativa:** tempo de implementacao por pessoa em poucas horas.
- **Complexidade:** Baixa, Media ou Alta.
- **Checklist:** usar o checklist padrao de `docs/implementation/CHECKLISTS.md` mais os itens especificos da story.
- **Rollback:** preferir reverter o commit da story; quando houver flag/adapter, desativar a integracao antes de reverter.

## EPIC 01 - Baseline e Seguranca

### IMP-001 - Registrar baseline de rotas e fluxos criticos

**Objetivo:** estabelecer uma referencia de funcionamento antes da migracao.

**Descricao:** catalogar as rotas atuais e fluxos minimos de navegacao, abertura de tabs, busca, sessao, pratica, biblioteca, repertorio e projetos.

**Arquivos envolvidos:** `src/routes/*`, `src/components/workspace/*`, `docs/architecture/COMPATIBILITY_GUIDE.md`.

**Dependencias:** nenhuma.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** baseline incompleto permitir regressao invisivel.

**O que implementar:** checklist executavel de smoke manual ou automatizado para cada rota atual.

**O que nao implementar:** alteracao de rotas, componentes ou design.

**O que testar:** acesso a todas as rotas, navegacao pela Sidebar, abertura de tabs e Command Palette.

**Como validar:** comparar resultado com `CURRENT_ARCHITECTURE.md` e `COMPATIBILITY_GUIDE.md`.

**Como desfazer:** remover o documento/checklist se ele estiver incorreto e recriar a partir das rotas reais.

**Criterios de aceite:** todas as rotas atuais aparecem no baseline; os fluxos criticos possuem passos de validacao; riscos nao cobertos estao marcados.

**Checklist:** confirmar lista de rotas; confirmar fluxos; registrar gaps; revisar com tech lead.

### IMP-002 - Registrar contratos publicos de frontend

**Objetivo:** proteger exports e APIs internas consumidas por rotas e componentes.

**Descricao:** listar hooks, providers, tipos e componentes que nao podem quebrar durante a migracao.

**Arquivos envolvidos:** `src/lib/music-api.ts`, `src/lib/workspace-store.tsx`, `src/lib/nav.ts`, `src/components/workspace/*`.

**Dependencias:** IMP-001.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** esquecer exports usados por rotas.

**O que implementar:** inventario de contratos publicos e consumidores.

**O que nao implementar:** renomear hooks, mover arquivos ou alterar imports.

**O que testar:** busca por imports existentes e uso de hooks principais.

**Como validar:** `rg` por cada export listado e revisao contra `COMPONENT_AUDIT.md`.

**Como desfazer:** corrigir o inventario sem tocar no codigo.

**Criterios de aceite:** contratos de `music-api`, `workspace-store`, `navTree`, `WorkspaceShell`, `TabBar`, `Inspector` e `CommandPalette` documentados.

**Checklist:** listar exports; listar consumidores; marcar estabilidade; apontar riscos.

### IMP-003 - Registrar contratos de API backend

**Objetivo:** impedir mudancas involuntarias nos endpoints usados pelo frontend.

**Descricao:** mapear endpoints REST `/api/v1`, controllers, DTOs e hooks consumidores.

**Arquivos envolvidos:** `backend/src/main/java/com/musicos/api/*`, `src/lib/music-api.ts`.

**Dependencias:** IMP-001.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** contratos backend mudarem durante refactor frontend.

**O que implementar:** tabela endpoint -> controller -> hook -> tela.

**O que nao implementar:** alterar controllers, services ou DTOs.

**O que testar:** chamadas existentes de home, plano, skills, biblioteca, repertorio, exercicios, projetos, diario, ouvido e dados.

**Como validar:** comparar hooks de `music-api.ts` com controllers.

**Como desfazer:** corrigir mapeamento.

**Criterios de aceite:** todos os endpoints consumidos pelo frontend estao mapeados e marcados como contratos estaveis.

**Checklist:** listar controllers; listar hooks; cruzar consumidores; registrar lacunas.

### IMP-004 - Criar matriz de validacao visual por rota

**Objetivo:** permitir validacao visual incremental sem esperar o fim da migracao.

**Descricao:** criar matriz de captura/check por viewport para rotas e estados essenciais.

**Arquivos envolvidos:** `src/routes/*`, `docs/ui/*`, `docs/ux/*`.

**Dependencias:** IMP-001.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** validacao visual subjetiva.

**O que implementar:** matriz com rota, viewport, estado esperado e criterio de aceitacao.

**O que nao implementar:** mudanca visual.

**O que testar:** desktop, tablet e mobile quando aplicavel.

**Como validar:** conferir com `SCREEN_SPECS.md`, `WORKSPACE_LAYOUTS.md` e `ACCESSIBILITY.md`.

**Como desfazer:** revisar matriz.

**Criterios de aceite:** cada rota critica possui pelo menos um criterio visual objetivo.

**Checklist:** cobrir viewports; cobrir loading/empty/error; registrar acessibilidade.

## EPIC 02 - Infraestrutura de Migracao

### IMP-005 - Adicionar tipos de macro contexto em paralelo

**Objetivo:** criar a linguagem tecnica dos contextos sem trocar UI.

**Descricao:** definir tipos para Home, Praticar, Aprender, Biblioteca, Compor, Revisar e Ferramentas, preservando tipos atuais.

**Arquivos envolvidos:** novo arquivo de tipos em `src/workspace` ou `src/shared`, sem alterar consumidores existentes.

**Dependencias:** IMP-002.

**Estimativa:** 3h. **Complexidade:** Baixa. **Riscos:** criar nomes divergentes da documentacao.

**O que implementar:** tipos e constantes de macro contexto.

**O que nao implementar:** conectar Sidebar, mudar `navTree` ou alterar rotas.

**O que testar:** typecheck e import isolado.

**Como validar:** nomes batem com `NAVIGATION_SYSTEM.md` e `TARGET_ARCHITECTURE.md`.

**Como desfazer:** remover arquivo novo.

**Criterios de aceite:** tipos existem, nao afetam bundle visual e nao geram mudanca funcional.

**Checklist:** revisar nomes; manter paralelo; rodar typecheck.

### IMP-006 - Criar registry de navegacao sem consumo visual

**Objetivo:** preparar fonte futura de navegacao sem trocar renderizacao.

**Descricao:** criar estrutura de registry com contexto, entradas, labels, paths e metadados.

**Arquivos envolvidos:** novo modulo de navegacao, `src/lib/nav.ts` apenas se for necessario reexport seguro.

**Dependencias:** IMP-005.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** registry divergir do menu atual.

**O que implementar:** registry completo cobrindo rotas atuais.

**O que nao implementar:** substituir `WorkspaceSidebar`.

**O que testar:** snapshot/assercoes de cobertura de paths.

**Como validar:** comparar registry com `navTree` e baseline de rotas.

**Como desfazer:** remover modulo novo.

**Criterios de aceite:** nenhuma rota atual fica sem representacao; registry nao e consumido em producao ainda.

**Checklist:** cobrir paths; marcar redirects; revisar labels; typecheck.

### IMP-007 - Criar adapter entre registry e `navTree`

**Objetivo:** permitir migracao da Sidebar sem alterar seu contrato de imediato.

**Descricao:** converter registry novo para o formato esperado por consumidores atuais.

**Arquivos envolvidos:** modulo de adapter, `src/lib/nav.ts`.

**Dependencias:** IMP-006.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** mudanca sutil de ordem ou label.

**O que implementar:** funcao adapter com saida equivalente ao `navTree` atual.

**O que nao implementar:** alterar visual da Sidebar.

**O que testar:** igualdade de paths, grupos e labels essenciais.

**Como validar:** comparacao automatizada ou checklist linha a linha.

**Como desfazer:** manter `navTree` manual e remover adapter.

**Criterios de aceite:** adapter reproduz navegacao atual sem perda de entrada.

**Checklist:** comparar ordem; comparar paths; comparar icones; typecheck.

### IMP-008 - Adicionar metadados compativeis para tabs

**Objetivo:** preparar o modelo futuro de abas sem quebrar abas atuais.

**Descricao:** definir campos opcionais como `context`, `kind`, `dirty`, `source`, mantendo `path` e `title`.

**Arquivos envolvidos:** `src/lib/workspace-store.tsx` ou modulo de tipos adjacente.

**Dependencias:** IMP-002.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** storage antigo rejeitar shape novo.

**O que implementar:** tipos opcionais e compatibilidade de leitura.

**O que nao implementar:** persistencia nova ou comportamento visual.

**O que testar:** tabs atuais abrem, ativam e fecham.

**Como validar:** typecheck e smoke de abertura de tabs.

**Como desfazer:** remover campos opcionais.

**Criterios de aceite:** modelo aceita dados antigos e novos sem migracao obrigatoria.

**Checklist:** manter `path/title`; evitar required fields; smoke tabs.

### IMP-009 - Criar esqueleto de pastas de features sem mover telas

**Objetivo:** criar destino arquitetural de forma neutra.

**Descricao:** adicionar pastas vazias ou placeholders de indice para features planejadas.

**Arquivos envolvidos:** `src/features/*`.

**Dependencias:** IMP-005.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** gerar estrutura que pareca implementada sem estar.

**O que implementar:** estrutura minima documentada.

**O que nao implementar:** mover rotas ou copiar componentes.

**O que testar:** typecheck se houver arquivos index.

**Como validar:** estrutura bate com `TARGET_ARCHITECTURE.md`.

**Como desfazer:** remover pastas/indices.

**Criterios de aceite:** estrutura existe sem alterar bundle ou comportamento.

**Checklist:** nomes consistentes; nenhum codigo duplicado; nenhum import alterado.

## EPIC 03 - Navegacao Principal

### IMP-010 - Conectar Sidebar ao adapter em modo equivalente

**Objetivo:** trocar a fonte de dados da Sidebar mantendo saida identica.

**Descricao:** fazer `WorkspaceSidebar` consumir o adapter validado sem mudar layout, classes ou interacoes.

**Arquivos envolvidos:** `src/components/workspace/WorkspaceSidebar.tsx`, adapter de navegacao.

**Dependencias:** IMP-007.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** perda de item ou ordem.

**O que implementar:** consumo do adapter equivalente.

**O que nao implementar:** novo design da Sidebar.

**O que testar:** navegacao por todos os itens e Command Palette se compartilhar fonte.

**Como validar:** smoke visual e comparacao com baseline.

**Como desfazer:** voltar Sidebar para `navTree` estatico.

**Criterios de aceite:** todos os itens atuais aparecem e navegam igual.

**Checklist:** conferir grupos; conferir paths; testar rota ativa; rollback simples.

### IMP-011 - Preservar selecao ativa da Sidebar por path

**Objetivo:** garantir que a nova fonte nao quebre estado ativo.

**Descricao:** ajustar resolucao de item ativo considerando rotas detalhe como biblioteca e repertorio.

**Arquivos envolvidos:** `WorkspaceSidebar.tsx`, util de navegacao se existir.

**Dependencias:** IMP-010.

**Estimativa:** 2h. **Complexidade:** Media. **Riscos:** detalhe nao marcar item pai.

**O que implementar:** regra pequena de matching por path.

**O que nao implementar:** mudanca visual de destaque.

**O que testar:** `/biblioteca/$nodeId`, `/repertorio/$songId`, `/projetos/$projectId`.

**Como validar:** navegar para detalhe e conferir item pai ativo.

**Como desfazer:** restaurar matching anterior.

**Criterios de aceite:** rotas detalhe mantem contexto ativo correto.

**Checklist:** testar rotas detalhe; testar root; testar redirect `/mapa`.

### IMP-012 - Persistir estado recolhido da Sidebar sem mudar layout

**Objetivo:** preservar preferencia local do usuario.

**Descricao:** adicionar persistencia leve para estado aberto/recolhido sem mexer em dimensoes.

**Arquivos envolvidos:** `WorkspaceSidebar.tsx` ou `workspace-store.tsx`.

**Dependencias:** IMP-010.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** storage interferir em estado do workspace.

**O que implementar:** leitura/escrita com fallback seguro.

**O que nao implementar:** redesign ou novas animacoes.

**O que testar:** reload com Sidebar aberta e recolhida.

**Como validar:** preferencia persiste e falha de storage nao quebra app.

**Como desfazer:** remover persistencia e limpar chave local.

**Criterios de aceite:** estado persiste apenas localmente e nao altera rotas.

**Checklist:** testar reload; testar fallback; documentar chave.

### IMP-013 - Auditar cobertura de rotas na navegacao e palette

**Objetivo:** garantir que nenhuma feature desapareca na navegacao.

**Descricao:** cruzar rotas existentes com Sidebar, menus e Command Palette.

**Arquivos envolvidos:** `src/routes/*`, `src/lib/nav.ts`, `CommandPalette.tsx`.

**Dependencias:** IMP-010.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** rotas secundarias ficarem inacessiveis.

**O que implementar:** auditoria ou teste de cobertura.

**O que nao implementar:** mudanca de IA de navegacao.

**O que testar:** cada rota possui pelo menos um caminho de acesso intencional.

**Como validar:** matriz rota -> entrada de navegacao.

**Como desfazer:** corrigir auditoria; se teste impedir release por falso positivo, ajustar criterio.

**Criterios de aceite:** nenhum item atual fica sem acesso.

**Checklist:** cruzar rotas; cruzar palette; registrar excecoes.

## EPIC 04 - Menus Contextuais

### IMP-014 - Adicionar menu contextual de Home

**Objetivo:** introduzir primeiro menu secundario de baixo risco.

**Descricao:** exibir entradas relacionadas ao estado inicial sem ocultar navegacao principal.

**Arquivos envolvidos:** modulo de menu contextual, `WorkspaceSidebar.tsx` ou novo componente adjacente.

**Dependencias:** IMP-013.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** duplicar links de forma confusa.

**O que implementar:** menu Home com entradas atuais relevantes.

**O que nao implementar:** menus dos demais contextos.

**O que testar:** Home e navegacao de volta.

**Como validar:** Home nao perde acesso a plano, sessao e diagnostico.

**Como desfazer:** remover registro Home do menu contextual.

**Criterios de aceite:** menu aparece apenas quando contexto Home esta ativo e nao altera URLs.

**Checklist:** verificar contexto; testar links; revisar labels.

### IMP-015 - Adicionar menu contextual de Praticar

**Objetivo:** agrupar fluxos de pratica mantendo entradas atuais.

**Descricao:** adicionar itens de plano, sessao, repertorio, exercicios, ouvido e metronomo.

**Arquivos envolvidos:** registry/menu contextual.

**Dependencias:** IMP-014.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** confundir Praticar com Ferramentas.

**O que implementar:** menu Praticar com links existentes.

**O que nao implementar:** mudar telas de pratica.

**O que testar:** acesso a `/plano`, `/sessao`, `/repertorio`, `/exercicios`, `/ouvido`, `/metronomo`.

**Como validar:** cada link carrega rota atual.

**Como desfazer:** remover grupo Praticar do registry contextual.

**Criterios de aceite:** praticante acessa todos os fluxos atuais pelo contexto.

**Checklist:** testar cada link; conferir item ativo; revisar duplicidade.

### IMP-016 - Adicionar menu contextual de Aprender

**Objetivo:** organizar aprendizado sem alterar `skills` e biblioteca.

**Descricao:** adicionar entradas de skills, mapa redirect e conteudos de aprendizado.

**Arquivos envolvidos:** registry/menu contextual.

**Dependencias:** IMP-014.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** `/mapa` deve continuar redirectando conforme contrato atual.

**O que implementar:** menu Aprender com paths existentes.

**O que nao implementar:** recriar skill tree.

**O que testar:** `/skills`, `/mapa`, links para biblioteca quando aplicavel.

**Como validar:** redirect `/mapa` continua compativel.

**Como desfazer:** remover grupo Aprender do registry contextual.

**Criterios de aceite:** aprendizado fica acessivel sem mudar URLs.

**Checklist:** testar `/skills`; testar `/mapa`; conferir labels.

### IMP-017 - Adicionar menu contextual de Biblioteca

**Objetivo:** dar acesso direto a biblioteca e detalhe preservando rotas.

**Descricao:** estruturar entradas de listagem e detalhe por item selecionado quando existir.

**Arquivos envolvidos:** registry/menu contextual, possivel util de route matching.

**Dependencias:** IMP-016.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** detalhes dinamicos sem item selecionado.

**O que implementar:** menu Biblioteca com fallback para listagem.

**O que nao implementar:** alterar `biblioteca.$nodeId.tsx`.

**O que testar:** `/biblioteca` e `/biblioteca/$nodeId`.

**Como validar:** detalhe mantem contexto Biblioteca ativo.

**Como desfazer:** remover entradas dinamicas do menu.

**Criterios de aceite:** listagem e detalhe continuam acessiveis.

**Checklist:** testar listagem; testar detalhe; testar fallback.

### IMP-018 - Adicionar menu contextual de Compor e Revisar

**Objetivo:** cobrir projetos e diario em contextos separados.

**Descricao:** adicionar Compor para projetos e Revisar para diario/dados quando coerente com docs.

**Arquivos envolvidos:** registry/menu contextual.

**Dependencias:** IMP-017.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** agrupar dados administrativos como revisao indevidamente.

**O que implementar:** menus Compor e Revisar com links existentes.

**O que nao implementar:** novas telas de composicao.

**O que testar:** `/projetos`, `/projetos/$projectId`, `/diario`, `/dados`.

**Como validar:** nenhuma rota muda de path.

**Como desfazer:** remover grupos do registry contextual.

**Criterios de aceite:** projetos e revisao ficam acessiveis por contexto sem perder menu antigo.

**Checklist:** testar projetos; testar detalhe; testar diario; testar dados.

### IMP-019 - Adicionar menu contextual de Ferramentas

**Objetivo:** preparar area de ferramentas sem mover ferramentas existentes.

**Descricao:** adicionar entradas de metronomo, ouvido e futuras ferramentas como placeholders nao navegaveis apenas se documentado.

**Arquivos envolvidos:** registry/menu contextual.

**Dependencias:** IMP-015.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** placeholders parecerem features prontas.

**O que implementar:** menu Ferramentas com links reais existentes.

**O que nao implementar:** ferramentas novas nem links quebrados.

**O que testar:** `/metronomo` e `/ouvido`.

**Como validar:** menu nao mostra item sem destino funcional.

**Como desfazer:** remover grupo Ferramentas.

**Criterios de aceite:** ferramentas atuais continuam acessiveis por rota propria.

**Checklist:** testar links; evitar placeholder falso; revisar labels.

## EPIC 05 - Workspace Shell

### IMP-020 - Isolar cabecalho do Workspace

**Objetivo:** reduzir responsabilidade do `WorkspaceShell` sem mudar layout.

**Descricao:** extrair cabecalho/titulo/acoes para subcomponente local mantendo props equivalentes.

**Arquivos envolvidos:** `WorkspaceShell.tsx`, novo componente em `components/workspace` ou `workspace/shell`.

**Dependencias:** IMP-013.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** quebrar acoes de palette ou tabs.

**O que implementar:** subcomponente de cabecalho com contrato local.

**O que nao implementar:** alterar visual, textos ou ordem de controles.

**O que testar:** render do shell, comandos do cabecalho, navegacao.

**Como validar:** screenshot antes/depois equivalente.

**Como desfazer:** inline do subcomponente no shell.

**Criterios de aceite:** diff visual intencionalmente nulo e shell mais simples.

**Checklist:** limitar arquivos; manter props; smoke workspace.

### IMP-021 - Isolar area central do Workspace

**Objetivo:** separar composicao de panels e conteudo.

**Descricao:** extrair corpo central responsavel por conteudo principal, painel lateral e layout resizable.

**Arquivos envolvidos:** `WorkspaceShell.tsx`, `Panel.tsx`, novo subcomponente.

**Dependencias:** IMP-020.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** alterar comportamento de resize.

**O que implementar:** subcomponente de body mantendo children e panels.

**O que nao implementar:** novo grid, novo layout ou dock.

**O que testar:** resize, inspector, conteudo de rotas.

**Como validar:** interacao de panels igual ao baseline.

**Como desfazer:** devolver markup para `WorkspaceShell`.

**Criterios de aceite:** rotas renderizam igual e resize segue funcional.

**Checklist:** testar resize; testar mobile; testar inspector.

### IMP-022 - Isolar status e indicadores do Workspace

**Objetivo:** criar ponto unico para indicadores sem mudar estado.

**Descricao:** extrair status bar, badges ou indicadores de sessao/instrumento se estiverem no shell.

**Arquivos envolvidos:** `WorkspaceShell.tsx`, `workspace-store.tsx`, novo subcomponente.

**Dependencias:** IMP-020.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** leitura errada de estado.

**O que implementar:** componente de status read-only.

**O que nao implementar:** novas preferencias ou mutacoes.

**O que testar:** troca de instrumento, sessao ativa, estados sem dados.

**Como validar:** indicadores exibem mesmos valores.

**Como desfazer:** reverter extracao.

**Criterios de aceite:** nenhum estado novo e nenhum indicador perdido.

**Checklist:** comparar textos; testar store; screenshot.

### IMP-023 - Centralizar atalhos do Workspace

**Objetivo:** evitar atalhos duplicados antes de migrar ferramentas.

**Descricao:** mover registro de keyboard shortcuts para modulo pequeno preservando combinacoes atuais.

**Arquivos envolvidos:** `WorkspaceShell.tsx`, `CommandPalette.tsx`, possivel hook novo.

**Dependencias:** IMP-020.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** atalhos pararem de responder.

**O que implementar:** hook ou util central de atalhos.

**O que nao implementar:** atalhos novos ou mudanca de combinacao.

**O que testar:** abrir palette, alternar paines, acoes existentes.

**Como validar:** checklist de `SHORTCUTS.md`.

**Como desfazer:** restaurar handlers nos componentes originais.

**Criterios de aceite:** atalhos atuais funcionam igual e ficam registrados em um ponto.

**Checklist:** testar teclado; testar foco em inputs; documentar conflitos.

## EPIC 06 - Sistema de Tabs

### IMP-024 - Adicionar campos opcionais ao modelo de tabs

**Objetivo:** permitir tabs ricas sem migracao quebradora.

**Descricao:** introduzir campos opcionais no tipo de tab com leitura tolerante.

**Arquivos envolvidos:** `workspace-store.tsx`, `TabBar.tsx` se necessario.

**Dependencias:** IMP-008.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** type narrowing incorreto.

**O que implementar:** campos opcionais e defaults.

**O que nao implementar:** persistencia v2.

**O que testar:** tabs antigas e tabs com metadados opcionais.

**Como validar:** typecheck e smoke tabs.

**Como desfazer:** remover campos e defaults.

**Criterios de aceite:** consumidor antigo continua compilando.

**Checklist:** preservar `path/title`; testar close/open; typecheck.

### IMP-025 - Ajustar abertura de tabs com deduplicacao compativel

**Objetivo:** impedir duplicatas sem quebrar fluxo atual.

**Descricao:** usar `id` quando existir e `path` como fallback.

**Arquivos envolvidos:** `workspace-store.tsx`.

**Dependencias:** IMP-024.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** tab errada ganhar foco.

**O que implementar:** deduplicacao por chave compativel.

**O que nao implementar:** alterar UI de tabs.

**O que testar:** abrir mesma rota, abrir detalhes diferentes, fechar ativa.

**Como validar:** comportamento atual preservado para `path`.

**Como desfazer:** voltar dedupe por `path`.

**Criterios de aceite:** nao surgem duplicatas indevidas e detalhes seguem independentes.

**Checklist:** testar lista; testar detalhe; testar fallback.

### IMP-026 - Adicionar indicadores visuais opcionais de tab

**Objetivo:** preparar estado `dirty`, contexto e tipo sem obrigar uso.

**Descricao:** renderizar indicadores apenas quando campos opcionais existirem.

**Arquivos envolvidos:** `TabBar.tsx`.

**Dependencias:** IMP-024.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** poluir tabs atuais.

**O que implementar:** indicadores discretos condicionais.

**O que nao implementar:** logica de dirty tracking.

**O que testar:** tab sem metadados, tab com metadados.

**Como validar:** tab atual permanece visualmente equivalente.

**Como desfazer:** remover render condicional.

**Criterios de aceite:** zero mudanca em tabs sem metadados.

**Checklist:** screenshot sem metadados; testar metadados fake; acessibilidade.

### IMP-027 - Persistir tabs com versao de storage

**Objetivo:** proteger usuarios de storage antigo durante evolucao do modelo.

**Descricao:** adicionar versao e migracao tolerante para dados persistidos de tabs quando persistencia for ativada.

**Arquivos envolvidos:** `workspace-store.tsx`.

**Dependencias:** IMP-025.

**Estimativa:** 3h. **Complexidade:** Alta. **Riscos:** corromper estado local do workspace.

**O que implementar:** storage versionado com fallback e limpeza segura.

**O que nao implementar:** sincronizacao remota.

**O que testar:** storage vazio, storage antigo, storage novo, storage invalido.

**Como validar:** reload preserva tabs sem erro.

**Como desfazer:** desativar leitura da chave v2 e voltar estado em memoria.

**Criterios de aceite:** storage invalido nao quebra a aplicacao.

**Checklist:** testar fallback; testar reload; documentar chave; rollback.

## EPIC 07 - Cards e Panels

### IMP-028 - Criar primitivos canonicos de Card e Panel

**Objetivo:** definir superficie comum sem migrar telas ainda.

**Descricao:** criar wrappers pequenos baseados nos componentes UI existentes e tokens atuais.

**Arquivos envolvidos:** `src/components/ui/card.tsx`, `src/components/workspace/Panel.tsx`, novo modulo shared.

**Dependencias:** IMP-004.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** duplicar sistema visual.

**O que implementar:** primitives canonicos compatibilizando shadcn/Radix existentes.

**O que nao implementar:** migracao em massa.

**O que testar:** render isolado e estados loading/empty/error.

**Como validar:** compara com `docs/ui/CARD_SYSTEM.md`.

**Como desfazer:** remover primitives novos.

**Criterios de aceite:** primitives existem sem alterar telas existentes.

**Checklist:** manter tokens atuais; documentar slots; typecheck.

### IMP-029 - Migrar cards do plano diario

**Objetivo:** validar primitives em area de baixo escopo.

**Descricao:** trocar apenas cards da rota de plano para o contrato canonico.

**Arquivos envolvidos:** `src/routes/plano.tsx`, primitives de Card/Panel.

**Dependencias:** IMP-028.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** alterar hierarquia de informacao.

**O que implementar:** uso dos primitives em cards do plano.

**O que nao implementar:** mudar dados, CTA ou API.

**O que testar:** plano carregado, empty, loading, erro.

**Como validar:** criterio visual da matriz da rota `/plano`.

**Como desfazer:** voltar markup local anterior.

**Criterios de aceite:** conteudo e acoes do plano permanecem iguais.

**Checklist:** testar estados; screenshot; manter textos.

### IMP-030 - Migrar cards de repertorio

**Objetivo:** aplicar contrato canonico ao repertorio em escopo isolado.

**Descricao:** migrar cards/lista da rota `/repertorio`, sem mexer no detalhe.

**Arquivos envolvidos:** `src/routes/repertorio.tsx`, primitives.

**Dependencias:** IMP-029.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** quebrar filtros ou links para detalhe.

**O que implementar:** cards da listagem.

**O que nao implementar:** alterar `repertorio.$songId.tsx`.

**O que testar:** listagem, filtros, abrir musica.

**Como validar:** todos os links de musica continuam funcionando.

**Como desfazer:** restaurar cards anteriores da listagem.

**Criterios de aceite:** repertorio lista igual em comportamento.

**Checklist:** testar link; testar filtro; screenshot.

### IMP-031 - Migrar cards de exercicios

**Objetivo:** padronizar cards da area de exercicios.

**Descricao:** migrar somente cards/lista de `/exercicios`.

**Arquivos envolvidos:** `src/routes/exercicios.tsx`, `ExerciseRunner.tsx` apenas se estritamente necessario.

**Dependencias:** IMP-029.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** acoplar card com runner.

**O que implementar:** superficie de card para exercicios.

**O que nao implementar:** alterar runner ou metronomo interno.

**O que testar:** selecionar exercicio e iniciar pratica.

**Como validar:** fluxo de exercicio continua igual.

**Como desfazer:** voltar card local.

**Criterios de aceite:** exercicio selecionavel e executavel.

**Checklist:** testar selecao; testar loading; testar erro.

### IMP-032 - Migrar cards de skills e biblioteca

**Objetivo:** consolidar areas de aprendizado com o mesmo contrato.

**Descricao:** migrar cards pequenos em `/skills` e `/biblioteca`, sem alterar detalhe de licao.

**Arquivos envolvidos:** `src/routes/skills.tsx`, `src/routes/biblioteca.tsx`, primitives.

**Dependencias:** IMP-030.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** mudar densidade de informacao.

**O que implementar:** cards/listas das duas rotas.

**O que nao implementar:** alterar `biblioteca.$nodeId.tsx`.

**O que testar:** abrir skill, abrir conteudo de biblioteca.

**Como validar:** links e estados visuais continuam coerentes.

**Como desfazer:** restaurar markup local.

**Criterios de aceite:** aprendizado e biblioteca seguem acessiveis.

**Checklist:** testar skills; testar biblioteca; screenshot.

## EPIC 08 - Modulos de Feature

### IMP-033 - Extrair modulo de metronomo

**Objetivo:** iniciar migracao de features por rota de menor acoplamento.

**Descricao:** mover conteudo da rota para componente em `features/tools` ou `features/metronome`, mantendo rota delegadora.

**Arquivos envolvidos:** `src/routes/metronomo.tsx`, `src/lib/use-metronome.ts`, novo modulo feature.

**Dependencias:** IMP-009.

**Estimativa:** 3h. **Complexidade:** Baixa. **Riscos:** hook de audio perder ciclo de vida.

**O que implementar:** componente de feature reutilizando hook atual.

**O que nao implementar:** nova UI do metronomo.

**O que testar:** start/stop, BPM, navegacao para fora.

**Como validar:** comportamento sonoro e visual igual.

**Como desfazer:** devolver componente para rota.

**Criterios de aceite:** `/metronomo` renderiza igual por delegacao.

**Checklist:** testar audio; testar cleanup; typecheck.

### IMP-034 - Extrair modulo de plano diario

**Objetivo:** mover plano para feature preservando hook e rota.

**Descricao:** criar componente de feature para `/plano` com imports atuais.

**Arquivos envolvidos:** `src/routes/plano.tsx`, `src/features/practice/*`, `music-api.ts`.

**Dependencias:** IMP-009, IMP-029.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** quebrar loading/empty/error.

**O que implementar:** rota delegadora e componente feature.

**O que nao implementar:** mudar cards ou API alem do ja feito.

**O que testar:** todos os estados do plano.

**Como validar:** route smoke `/plano`.

**Como desfazer:** inline do componente na rota.

**Criterios de aceite:** comportamento equivalente e rota fina.

**Checklist:** testar states; conferir imports; rollback.

### IMP-035 - Extrair modulo de diario

**Objetivo:** mover revisao/diario para feature isolada.

**Descricao:** delegar `/diario` para componente em `features/review`.

**Arquivos envolvidos:** `src/routes/diario.tsx`, `src/features/review/*`, `music-api.ts`.

**Dependencias:** IMP-009.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** mutacoes de diario mudarem.

**O que implementar:** extracao sem alterar formulario ou hooks.

**O que nao implementar:** mudar schema de entrada.

**O que testar:** listar, criar ou atualizar entrada conforme existente.

**Como validar:** fluxo atual de diario funciona.

**Como desfazer:** devolver JSX para rota.

**Criterios de aceite:** `/diario` mantem dados e acoes.

**Checklist:** testar mutacao; testar erro; typecheck.

### IMP-036 - Extrair modulos de projetos

**Objetivo:** separar lista e detalhe de projetos em feature.

**Descricao:** mover conteudo de `/projetos` e `/projetos/$projectId` em duas unidades internas, mas uma story por dominio.

**Arquivos envolvidos:** `src/routes/projetos.tsx`, `src/routes/projetos.$projectId.tsx`, `src/features/compose/*`.

**Dependencias:** IMP-009.

**Estimativa:** 4h. **Complexidade:** Media. **Riscos:** detalhe perder parametros de rota.

**O que implementar:** componentes `ProjectList` e `ProjectDetail` com rotas delegadoras.

**O que nao implementar:** mudar modelo de projeto.

**O que testar:** listagem, abrir detalhe, salvar/editar se existir.

**Como validar:** parametros continuam chegando ao detalhe.

**Como desfazer:** restaurar conteudo nas rotas.

**Criterios de aceite:** lista e detalhe funcionam com URLs atuais.

**Checklist:** testar parametro; testar volta; testar loading.

### IMP-037 - Extrair modulos de biblioteca

**Objetivo:** mover biblioteca e detalhe de licao para feature.

**Descricao:** delegar `/biblioteca` e `/biblioteca/$nodeId` para `features/library`.

**Arquivos envolvidos:** `src/routes/biblioteca.tsx`, `src/routes/biblioteca.$nodeId.tsx`, `LessonRenderer.tsx`, `features/library/*`.

**Dependencias:** IMP-009, IMP-032.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** lesson renderer e parametros de no.

**O que implementar:** componentes de listagem e detalhe reutilizando renderer atual.

**O que nao implementar:** reescrever renderizador de licoes.

**O que testar:** abrir biblioteca, abrir node, renderizar licao.

**Como validar:** conteudo da licao permanece identico.

**Como desfazer:** devolver JSX para rotas.

**Criterios de aceite:** biblioteca e detalhe preservam comportamento.

**Checklist:** testar node valido; testar node invalido; testar loading.

### IMP-038 - Extrair modulos de repertorio

**Objetivo:** mover repertorio para feature mantendo detalhe musical.

**Descricao:** delegar lista e detalhe para `features/repertoire`.

**Arquivos envolvidos:** `src/routes/repertorio.tsx`, `src/routes/repertorio.$songId.tsx`, `InteractiveTab.tsx`, `InteractiveScore.tsx`, `features/repertoire/*`.

**Dependencias:** IMP-009, IMP-030.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** componentes musicais com estado local.

**O que implementar:** componentes de feature reutilizando componentes musicais.

**O que nao implementar:** alterar VexFlow, tab interativa ou API.

**O que testar:** lista, detalhe, tab/score interativos.

**Como validar:** score e tab renderizam igual.

**Como desfazer:** restaurar rotas.

**Criterios de aceite:** repertorio completo preservado.

**Checklist:** testar musica; testar audio/interacao; testar detalhe invalido.

### IMP-039 - Extrair modulos de exercicios e ouvido

**Objetivo:** mover praticas estruturadas para features.

**Descricao:** delegar `/exercicios` para `features/exercises` e `/ouvido` para `features/ear-training`.

**Arquivos envolvidos:** `src/routes/exercicios.tsx`, `src/routes/ouvido.tsx`, `ExerciseRunner.tsx`, `AudioCuePlayer.tsx`, `features/*`.

**Dependencias:** IMP-009, IMP-031.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** audio, runner e tentativas.

**O que implementar:** componentes de feature reutilizando runner/player.

**O que nao implementar:** mudar avaliacao ou audio.

**O que testar:** iniciar exercicio, responder ouvido, registrar tentativa se existir.

**Como validar:** fluxos iguais ao baseline.

**Como desfazer:** restaurar conteudo nas rotas.

**Criterios de aceite:** praticas funcionam por URLs atuais.

**Checklist:** testar runner; testar audio; testar mutacao.

### IMP-040 - Extrair modulos de sessao, dados e diagnostico

**Objetivo:** concluir delegacao de rotas de maior risco operacional.

**Descricao:** mover sessao, dados e diagnostico para features correspondentes preservando contratos.

**Arquivos envolvidos:** `src/routes/sessao.tsx`, `src/routes/dados.tsx`, `src/routes/diagnostico.tsx`, `features/*`, `music-api.ts`.

**Dependencias:** IMP-034, IMP-035, IMP-039.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** diagnostico e dados podem afetar estado global.

**O que implementar:** componentes de feature com rotas delegadoras.

**O que nao implementar:** mudar formularios, endpoints ou inicializacao de dados.

**O que testar:** fluxo de sessao, diagnostico, status/importacao/exportacao de dados.

**Como validar:** baseline dos fluxos criticos passa.

**Como desfazer:** restaurar rotas.

**Criterios de aceite:** todas as rotas principais ja delegam para features.

**Checklist:** testar fluxo completo; testar erro; revisar imports.

## EPIC 09 - API Frontend

### IMP-041 - Criar cliente API compartilhado

**Objetivo:** reduzir duplicacao em `music-api.ts` sem mudar hooks.

**Descricao:** extrair fetch/client base para modulo compartilhado usado pela facade.

**Arquivos envolvidos:** `src/lib/music-api.ts`, novo `src/shared/api/*`.

**Dependencias:** IMP-003.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** tratamento de erro divergir.

**O que implementar:** cliente base com comportamento identico.

**O que nao implementar:** renomear hooks.

**O que testar:** chamadas de home e erro de API.

**Como validar:** respostas e erros continuam no mesmo shape.

**Como desfazer:** devolver fetch local para `music-api.ts`.

**Criterios de aceite:** facade publica nao muda.

**Checklist:** preservar base URL; preservar erro; typecheck.

### IMP-042 - Separar modulo API de home e pratica

**Objetivo:** extrair primeiro grupo de hooks de baixo risco.

**Descricao:** mover implementacao interna de home, plano e sessao mantendo exports pela facade.

**Arquivos envolvidos:** `music-api.ts`, `shared/api`, modulo `features/practice/api` ou dominio equivalente.

**Dependencias:** IMP-041.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** query keys mudarem.

**O que implementar:** modulo interno e reexports.

**O que nao implementar:** mudar nomes de hooks ou query keys sem justificativa.

**O que testar:** home, plano, sessao.

**Como validar:** cache e loading seguem iguais.

**Como desfazer:** devolver hooks para facade.

**Criterios de aceite:** importadores existentes nao mudam.

**Checklist:** preservar query keys; smoke rotas; typecheck.

### IMP-043 - Separar modulo API de aprendizado e biblioteca

**Objetivo:** modularizar hooks de skills e biblioteca.

**Descricao:** mover implementacao interna mantendo facade.

**Arquivos envolvidos:** `music-api.ts`, `features/learning/api`, `features/library/api`.

**Dependencias:** IMP-041.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** detalhe de biblioteca quebrar.

**O que implementar:** modulos internos e reexports.

**O que nao implementar:** alterar DTOs.

**O que testar:** `/skills`, `/biblioteca`, `/biblioteca/$nodeId`.

**Como validar:** dados carregam igual.

**Como desfazer:** devolver hooks para facade.

**Criterios de aceite:** facade mantem exports.

**Checklist:** preservar hooks; testar detalhes; typecheck.

### IMP-044 - Separar modulo API de repertorio, projetos e dados

**Objetivo:** modularizar dominios com maior impacto.

**Descricao:** mover hooks e mutations internas mantendo facade e endpoints.

**Arquivos envolvidos:** `music-api.ts`, `features/repertoire/api`, `features/compose/api`, `features/tools/api` ou `shared/api`.

**Dependencias:** IMP-041, IMP-038, IMP-040.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** mutations e invalidações de cache.

**O que implementar:** modulos internos com reexports.

**O que nao implementar:** mudar endpoints `/api/v1`.

**O que testar:** repertorio, projetos, diario/dados quando envolvidos.

**Como validar:** mutacoes invalidam mesmos dados.

**Como desfazer:** devolver codigo para facade.

**Criterios de aceite:** nenhum import externo precisa mudar.

**Checklist:** preservar mutation keys; testar cache; typecheck.

### IMP-045 - Validar facade `music-api.ts`

**Objetivo:** provar compatibilidade apos split.

**Descricao:** adicionar verificacao de exports e consumidores, garantindo que `music-api.ts` continua sendo contrato publico.

**Arquivos envolvidos:** `src/lib/music-api.ts`, testes ou script de verificacao.

**Dependencias:** IMP-042, IMP-043, IMP-044.

**Estimativa:** 2h. **Complexidade:** Alta. **Riscos:** falso senso de compatibilidade.

**O que implementar:** teste ou checklist automatizado de exports.

**O que nao implementar:** remover facade.

**O que testar:** todos os hooks documentados em `COMPATIBILITY_GUIDE.md`.

**Como validar:** `rg` por imports antigos e suite passando.

**Como desfazer:** remover teste apenas se substituido por validacao melhor.

**Criterios de aceite:** facade preserva todos os exports usados.

**Checklist:** listar exports; testar import; smoke rotas.

## EPIC 10 - Inspector, Dock e Ferramentas

### IMP-046 - Dividir secoes do Inspector

**Objetivo:** modularizar Inspector sem mudar conteudo.

**Descricao:** extrair secoes internas em componentes pequenos.

**Arquivos envolvidos:** `src/components/workspace/Inspector.tsx`, novos componentes adjacentes.

**Dependencias:** IMP-021.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** perder informacao contextual.

**O que implementar:** subcomponentes read-only ou com mesmas callbacks.

**O que nao implementar:** novo modelo de inspector.

**O que testar:** conteudo do inspector em rotas principais.

**Como validar:** screenshot equivalente.

**Como desfazer:** inline das secoes.

**Criterios de aceite:** Inspector visual e funcionalmente equivalente.

**Checklist:** comparar secoes; testar store; typecheck.

### IMP-047 - Criar host de Dock vazio

**Objetivo:** preparar area de ferramentas persistentes sem exibir feature incompleta.

**Descricao:** adicionar host controlado e vazio, desativado ou sem ferramentas por default.

**Arquivos envolvidos:** `WorkspaceShell.tsx` ou body extraido, novo `DockHost`.

**Dependencias:** IMP-021, IMP-046.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** alterar layout central.

**O que implementar:** host sem impacto visual quando vazio.

**O que nao implementar:** ferramentas reais.

**O que testar:** layout com dock vazio e desativado.

**Como validar:** screenshot igual quando sem ferramentas.

**Como desfazer:** remover host.

**Criterios de aceite:** dock vazio nao muda experiencia.

**Checklist:** testar desktop; testar mobile; rollback.

### IMP-048 - Adaptar metronomo como ferramenta de Dock

**Objetivo:** validar Dock com ferramenta existente sem remover rota.

**Descricao:** criar adapter do metronomo para o Dock reutilizando hook atual.

**Arquivos envolvidos:** `DockHost`, feature metronomo, `use-metronome.ts`, rota `/metronomo`.

**Dependencias:** IMP-033, IMP-047.

**Estimativa:** 3h. **Complexidade:** Alta. **Riscos:** audio duplicado entre rota e dock.

**O que implementar:** instancia controlada no Dock, se habilitada.

**O que nao implementar:** remover ou substituir `/metronomo`.

**O que testar:** rota e dock nao conflitam; start/stop.

**Como validar:** apenas uma fonte sonora ativa por vez.

**Como desfazer:** desregistrar ferramenta do Dock.

**Criterios de aceite:** metronomo continua funcionando na rota e pode funcionar no Dock.

**Checklist:** testar audio; testar navegacao; testar cleanup.

## EPIC 11 - Tokens Visuais

### IMP-049 - Adicionar aliases de tokens semanticos

**Objetivo:** criar ponte para o design system futuro.

**Descricao:** adicionar tokens semanticos mapeados para valores atuais.

**Arquivos envolvidos:** `src/styles.css`, docs de tokens se mantidos no repo.

**Dependencias:** IMP-004.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** mudar visual sem perceber.

**O que implementar:** aliases sem mudanca de valor final.

**O que nao implementar:** ativar tema claro ou alterar paleta.

**O que testar:** regressao visual basica.

**Como validar:** screenshots equivalentes.

**Como desfazer:** remover aliases.

**Criterios de aceite:** CSS computado visualmente equivalente.

**Checklist:** comparar valores; testar contraste; screenshot.

### IMP-050 - Migrar Workspace para tokens semanticos

**Objetivo:** reduzir dependencia de cores literais no Workspace.

**Descricao:** trocar referencias locais por aliases semanticos equivalentes.

**Arquivos envolvidos:** `WorkspaceShell.tsx`, `WorkspaceSidebar.tsx`, `TabBar.tsx`, `Inspector.tsx`, CSS.

**Dependencias:** IMP-049.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** pequenas divergencias visuais.

**O que implementar:** substituicao local e equivalente.

**O que nao implementar:** novo tema.

**O que testar:** workspace completo em rotas principais.

**Como validar:** matriz visual.

**Como desfazer:** voltar classes anteriores.

**Criterios de aceite:** sem mudanca visual intencional.

**Checklist:** comparar workspace; testar estados; acessibilidade.

### IMP-051 - Migrar cards e panels para tokens semanticos

**Objetivo:** completar base visual comum das superficies.

**Descricao:** aplicar aliases aos primitives canonicos.

**Arquivos envolvidos:** primitives de Card/Panel, `Panel.tsx`, CSS.

**Dependencias:** IMP-049, IMP-028.

**Estimativa:** 3h. **Complexidade:** Media. **Riscos:** contraste em estados especiais.

**O que implementar:** tokens equivalentes em cards/panels.

**O que nao implementar:** redesenho de cards.

**O que testar:** loading, empty, error, hover, focus.

**Como validar:** contraste e screenshot.

**Como desfazer:** voltar classes anteriores.

**Criterios de aceite:** superficies preservam hierarquia visual.

**Checklist:** testar estados; testar focus; screenshot.

### IMP-052 - Ativar preview de tema claro sem substituir tema atual

**Objetivo:** validar direcao visual futura com baixo risco.

**Descricao:** criar preview opt-in de tema claro seguindo docs UI.

**Arquivos envolvidos:** `src/styles.css`, provider/setting se ja existir.

**Dependencias:** IMP-050, IMP-051.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** contraste e legibilidade.

**O que implementar:** mecanismo opt-in ou flag local.

**O que nao implementar:** tornar tema claro default.

**O que testar:** contraste, foco, rotas principais.

**Como validar:** WCAG AA nos textos essenciais.

**Como desfazer:** desativar flag/preview.

**Criterios de aceite:** preview existe sem afetar usuarios atuais.

**Checklist:** testar contraste; testar tema atual; testar persistencia se houver.

## EPIC 12 - Limpeza e Release

### IMP-053 - Remover wrappers obsoletos comprovadamente sem uso

**Objetivo:** eliminar camada antiga apenas quando nao houver import.

**Descricao:** remover arquivos/adapters antigos apos busca e validacao completa.

**Arquivos envolvidos:** wrappers antigos identificados por `rg`, docs de arquitetura.

**Dependencias:** IMP-045, IMP-052.

**Estimativa:** 3h. **Complexidade:** Alta. **Riscos:** import dinamico ou uso indireto.

**O que implementar:** remocao minima e comprovada.

**O que nao implementar:** limpeza oportunista fora do escopo.

**O que testar:** typecheck, build, smoke completo.

**Como validar:** `rg` sem consumidores e suite passando.

**Como desfazer:** restaurar arquivo removido.

**Criterios de aceite:** nenhum contrato publico e removido sem substituto.

**Checklist:** `rg`; build; smoke; rollback.

### IMP-054 - Atualizar documentacao arquitetural apos migracao

**Objetivo:** manter docs alinhadas ao codigo real.

**Descricao:** atualizar docs de arquitetura com estado final implementado.

**Arquivos envolvidos:** `docs/architecture/*`, `docs/implementation/*`.

**Dependencias:** IMP-053.

**Estimativa:** 2h. **Complexidade:** Baixa. **Riscos:** docs divergirem da implementacao.

**O que implementar:** atualizacao documental factual.

**O que nao implementar:** novas decisoes de arquitetura.

**O que testar:** revisao cruzada com estrutura de arquivos.

**Como validar:** docs refletem `rg --files`.

**Como desfazer:** reverter edicoes documentais incorretas.

**Criterios de aceite:** docs indicam arquitetura real, nao aspiracional.

**Checklist:** conferir pastas; conferir rotas; conferir contratos.

### IMP-055 - Executar candidata de release e congelamento

**Objetivo:** validar a migracao como release completo.

**Descricao:** rodar regressao final, congelar escopo e preparar release.

**Arquivos envolvidos:** nenhum codigo novo; pipelines, docs e checklist de release.

**Dependencias:** IMP-054.

**Estimativa:** 4h. **Complexidade:** Alta. **Riscos:** regressao tardia em fluxo pouco usado.

**O que implementar:** checklist final e correcao apenas via stories novas se necessario.

**O que nao implementar:** novas features no release candidate.

**O que testar:** suite completa, smoke rotas, fluxo musical, API backend.

**Como validar:** todos os criterios de release passam.

**Como desfazer:** adiar release e abrir stories corretivas pequenas.

**Criterios de aceite:** candidata aprovada sem regressao bloqueante.

**Checklist:** congelar escopo; rodar checks; aprovar release; registrar decisoes.
