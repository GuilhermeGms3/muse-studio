# Card System

Cards são protagonistas do Muse Studio. Eles representam objetos, módulos e decisões. Um card existe para o usuário reconhecer algo e agir.

## Anatomia De Card

Todo card deve possuir:

- Container.
- Título.
- Informação primária.
- Informação secundária.
- Estado.
- Ação principal ou área clicável.
- Ações secundárias quando necessário.
- Indicador contextual quando aplicável.

## Hierarquia

1. Título ou objeto.
2. Estado/progresso quando orienta decisão.
3. Metadados curtos.
4. Ação principal.
5. Relações secundárias.

## Tamanhos

Card pequeno:

- Uso: listas rápidas, plano, abas de relacionamento, itens de diário.
- Altura: 72px a 104px.
- Conteúdo: título, metadado, estado curto.

Card médio:

- Uso: música, exercício, aula, projeto.
- Altura: 120px a 180px.
- Conteúdo: título, descrição curta, tags, ação.

Card grande:

- Uso: módulo de trabalho, atividade atual, resumo de sessão, captura criativa.
- Altura: 220px ou mais.
- Conteúdo: controles, campos, módulos interativos.

## Espaçamento

- Padding pequeno: 8px a 12px.
- Padding médio: 16px.
- Gap interno: 6px a 12px.
- Gap entre cards: 12px a 16px.
- Bordas internas devem alinhar com grid de 4px.

## Border Radius

- Cards padrão: 6px.
- Cards compactos/listas: 4px.
- Cards grandes: 8px no máximo.

Cards não devem parecer pílulas grandes nem blocos decorativos.

## Estados

Padrão:

- Fundo branco.
- Borda cinza clara.
- Texto primário escuro.

Hover:

- Borda ligeiramente mais forte.
- Fundo muito levemente elevado.
- Ações secundárias podem aparecer.

Focus:

- Anel de foco visível.
- Não depender só de cor.

Pressed:

- Leve redução de elevação ou fundo.
- Sem deslocamento de layout.

Selecionado:

- Borda contextual.
- Fundo branco ou leve tint do contexto.
- Indicador lateral ou superior discreto.

Modificado:

- Ponto pequeno ou label "não salvo".

Carregando:

- Skeleton dentro do card.
- Dimensões preservadas.

Erro:

- Borda semântica de erro.
- Mensagem curta.
- Ação de retry quando aplicável.

Desativado:

- Opacidade reduzida.
- Motivo disponível por tooltip ou texto curto.

## Variações

Card informativo:

- Sem ação primária forte.
- Usado para contexto ou status.

Card interativo:

- Área principal clicável.
- Ações secundárias visíveis no hover/focus.

Card expansível:

- Mostra resumo fechado.
- Expande detalhes no mesmo card.
- Não deve mudar largura do grid.

Card inteligente:

- Mostra próximo passo ou recomendação.
- Deve explicar o motivo em uma linha curta.

Card de ferramenta:

- Mostra estado da ferramenta.
- Permite abrir em dock ou aba.

## Cards Por Objeto

Música:

- Título, artista, instrumento, status, BPM, progresso, técnicas.

Exercício:

- Nome, técnica, BPM atual/alvo, duração, dificuldade, critério.

Habilidade:

- Nome, domínio, estado, progresso, próximo requisito.

Aula:

- Título, categoria, nível, tempo estimado, habilidade relacionada.

Projeto:

- Nome, tom, BPM, status, ideias recentes.

Diário:

- Data, duração, instrumento, trabalhado, dificuldade, melhoria.

Plano:

- Duração, atividade, tipo, alvo, estado concluído.

## Regras De Consistência

- Todo card tem título no mesmo lugar.
- Todo card selecionável tem estado selecionado.
- Todo card interativo é navegável por teclado.
- Todo card com ação destrutiva separa essa ação visualmente.
- Todo card com truncamento deve oferecer acesso ao texto completo.
- Nenhum card deve usar cor apenas como decoração.

