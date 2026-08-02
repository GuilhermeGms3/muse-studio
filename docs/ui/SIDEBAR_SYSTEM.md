# Sidebar System

A sidebar organiza contexto. Ela não deve mostrar todas as funcionalidades ao mesmo tempo.

## Estrutura

Sidebar principal:

- Macro contextos.
- Estado ativo.
- Ícones.
- Labels.

Sidebar contextual:

- Menu do macro contexto ativo.
- Subgrupos quando necessário.
- Itens relacionados.
- Estados de contagem/pendência discretos.

## Estados

Expandida:

- Ícone + label.
- Grupos visíveis.
- Hints curtos opcionais.

Recolhida:

- Ícones.
- Tooltip no hover/focus.
- Indicador de contexto ativo.

Oculta:

- Workspace ganha espaço.
- Botão de reabrir permanece na title bar.

## Indicadores

Ativo:

- Barra lateral ou fundo sutil.
- Cor contextual discreta.

Pendente:

- Badge pequeno.
- Nunca usar badge grande.

Sessão ativa:

- Indicador discreto em Praticar/Sessão.

Erro:

- Ícone ou ponto semântico.
- Tooltip ou texto no item.

## Ícones

Regras:

- Usar família única.
- Tamanho 16px a 18px.
- Stroke consistente.
- Ícone não substitui label em estado expandido.
- Não usar emoji.

## Comportamentos

- Clicar macro contexto troca menu contextual.
- Clicar item abre ou ativa aba.
- Expandir/recolher grupo preserva estado.
- Sidebar deve suportar teclado.
- Scroll independente se menu for longo.

## Hierarquia Visual

Macro contexto:

- Peso médio.
- Espaçamento maior.
- Ícone forte.

Item contextual:

- Texto compacto.
- Estado ativo claro.
- Hint apenas se útil.

Subitem:

- Recuo.
- Menor ênfase.

## Larguras

- Expandida: 240px a 288px.
- Recolhida: 56px a 72px.
- Mínima funcional: 220px.

## Anti-Padrões

- Mostrar todos os itens de todos os contextos.
- Sidebar virar mapa completo do produto.
- Badges competindo com títulos.
- Grupos abertos demais por padrão.
- Ícones coloridos demais.

