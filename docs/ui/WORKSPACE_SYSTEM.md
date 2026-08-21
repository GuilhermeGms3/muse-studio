# Workspace System

O Workspace é o centro do Muse Studio. Ele contém abas, módulos, cards, painéis, inspector e dock. Sidebar e menus existem para alimentar o workspace, não para competir com ele.

## Estrutura Fixa

Elementos fixos:

- Title bar.
- Seletor de instrumento.
- Busca global.
- Controle rápido de metrônomo/BPM.
- Botões de sidebar e inspector.
- Tab bar.
- Status bar.

Esses elementos permanecem estáveis para reduzir carga cognitiva.

## Estrutura Contextual

Elementos contextuais:

- Menu da sidebar.
- Conteúdo da aba ativa.
- Cards exibidos.
- Inspector.
- Dock.
- Toolbar da aba.
- Ações rápidas.

Eles mudam conforme macro contexto, seleção e tarefa.

## O Que Pode Ser Fechado

- Sidebar.
- Inspector.
- Dock.
- Abas não fixas.
- Popovers.
- Command palette.
- Painéis temporários.

## O Que Não Deve Ser Fechado Permanentemente

- Title bar.
- Tab bar.
- Workspace.
- Status bar.
- Acesso à busca global.

## O Que Pode Ser Destacado

- Aba ativa.
- Card selecionado.
- Atividade atual.
- Sessão ativa.
- Ferramenta em execução.
- Drop target.
- Item com erro ou pendência.

## O Que Pode Ser Desacoplado

Conceitualmente, os seguintes elementos podem evoluir para modo destacado/desacoplado:

- Metrônomo.
- Gravador.
- Player de áudio.
- Inspector de objeto.
- Mapa do conhecimento.
- Prática e Studio dentro do shell único.

Mesmo desacoplados, devem preservar vínculo com o objeto/contexto.

## Múltiplas Abas

Regras visuais:

- Abas coexistem em linha horizontal.
- Aba ativa tem borda superior ou inferior clara.
- Abas modificadas têm indicador discreto.
- Abas com sessão ativa têm indicador persistente.
- Abas temporárias têm título em estilo sutil ou itálico futuro.
- Abas pinadas usam largura menor e ícone.

## Limpeza Com Muitas Funcionalidades

O workspace permanece limpo porque:

- Sidebar mostra apenas menu contextual.
- Cards mostram apenas o necessário para decidir.
- Inspector guarda relações secundárias.
- Dock guarda ferramentas temporárias.
- Command palette absorve atalhos e busca ampla.
- Detalhes avançados ficam em expansão.

## Layout Base

```text
+--------------------------------------------------------------------------------+
| Title bar: produto | instrumento | busca | BPM | toggles                       |
+--------------------------------------------------------------------------------+
| Sidebar contextual | Tabs                                                       |
|                    +------------------------------------------------------------+
|                    | Workspace ativo                                            |
|                    | Cards, paineis, modulos                                    |
|                    |                                                            |
|                    +---------------------------------------+--------------------+
|                    | Dock contextual opcional              | Inspector opcional |
+--------------------------------------------------------------------------------+
| Status bar: sessao, tempo, BPM, dados, atalhos                                  |
+--------------------------------------------------------------------------------+
```

## Regras De Composição

- Workspace nunca deve virar página densa.
- Cada aba deve ter uma pergunta principal.
- Cada módulo deve ter propósito claro.
- Inspector não deve duplicar o workspace.
- Dock não deve cobrir ação principal.
- Toolbars devem ser compactas e contextuais.

## Painéis

Painéis são superfícies delimitadas para trabalho ou apoio. Eles não são páginas dentro de páginas.

### Inspector

Função:

- Mostrar contexto secundário do item selecionado.
- Expor relações, pré-requisitos, próximas ações e notas.
- Apoiar decisão sem roubar foco do workspace.

Regras:

- Pode ser fechado.
- Pode ser redimensionado.
- Atualiza com a seleção.
- Deve ter painéis internos curtos.
- Não deve repetir todo o conteúdo da aba.

### Dock

Função:

- Hospedar ferramentas temporárias ou persistentes.
- Manter metrônomo, gravador, player ou import status próximos do trabalho.

Regras:

- Pode ser recolhido.
- Pode permanecer ativo entre abas quando a ferramenta fizer sentido.
- Deve indicar claramente ferramenta em execução.
- Não deve cobrir conteúdo sem permitir ajuste.

### Painéis Laterais

Tipos:

- Sidebar de navegação.
- Inspector.
- Painel contextual futuro.

Regras:

- Largura redimensionável dentro de limites.
- Estado recolhido persistente.
- Conteúdo com scroll próprio.
- Cabeçalho compacto.

### Painéis Inferiores

Uso:

- Dock.
- Console/status futuro.
- Transporte musical futuro.

Regras:

- Altura controlada.
- Pode recolher para barra compacta.
- Não deve virar área principal por acidente.

### Painéis Contextuais

Uso:

- Edição rápida.
- Configuração de ferramenta.
- Detalhes de seleção.

Regras:

- Surgem perto do contexto.
- Fecham com Esc ou clique externo quando seguro.
- Preservam rascunho se houver edição.

### Painéis Recolhíveis

Regras:

- Estado expandido/recolhido visível.
- Animação curta.
- Conteúdo essencial não deve ficar escondido por padrão.
- Recolher libera espaço sem encerrar tarefa.
