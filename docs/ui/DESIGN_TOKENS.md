# Design Tokens

Tokens são a fonte visual do Muse Studio. Implementações futuras devem usar tokens sem valores arbitrários por componente.

## Espaçamento

Escala base:

- 0: 0px
- 1: 4px
- 2: 8px
- 3: 12px
- 4: 16px
- 5: 20px
- 6: 24px
- 8: 32px
- 10: 40px
- 12: 48px
- 16: 64px

Uso:

- 4px para micro alinhamentos.
- 8px para controles compactos.
- 12px para gaps internos.
- 16px para cards e módulos.
- 24px para separação de seções.

## Radius

- none: 0px.
- xs: 2px.
- sm: 4px.
- md: 6px.
- lg: 8px.
- xl: 12px apenas para dialogs ou superfícies especiais.

Cards devem usar 4px a 8px.

## Elevação E Sombras

Muse Studio prefere borda a sombra.

Escala:

- level-0: sem sombra.
- level-1: sombra quase imperceptível para cards hover.
- level-2: popover e dropdown.
- level-3: dialog.

Sombras nunca devem ser decorativas.

## Bordas

- border-subtle: divisores internos.
- border-default: cards e inputs.
- border-strong: hover, painéis ativos.
- border-focus: foco de teclado.
- border-context: seleção contextual.
- border-danger: erro ou destrutivo.

## Cores Base

Tokens conceituais:

- background-app.
- background-rail.
- background-workspace.
- surface-card.
- surface-panel.
- surface-elevated.
- surface-hover.
- surface-selected.
- border-subtle.
- border-default.
- text-primary.
- text-secondary.
- text-muted.
- text-inverse.

## Cores Semânticas

- action-primary.
- action-primary-hover.
- action-secondary.
- success.
- warning.
- danger.
- info.
- focus.

Toda cor semântica precisa ter par de foreground com contraste adequado.

## Cores De Contexto

- context-home.
- context-practice.
- context-learn.
- context-library.
- context-compose.
- context-review.
- context-tools.

Uso permitido:

- Indicador de aba.
- Borda de seleção.
- Ícone ativo.
- Badge discreto.
- Pequeno destaque.

Uso proibido:

- Fundo de página inteiro.
- Gradiente decorativo.
- Grandes cards coloridos sem função.

## Tipografia

Famílias conceituais:

- UI Sans: interface, texto, labels.
- UI Mono: números, timer, BPM, valores técnicos.

Escala:

- caption: 11px.
- label: 12px.
- body-sm: 13px.
- body: 14px.
- body-lg: 16px.
- title-sm: 18px.
- title-md: 20px.
- title-lg: 24px.

Line-height:

- Compacto: 1.2.
- Interface: 1.35.
- Leitura: 1.5 a 1.65.

Pesos:

- Regular: 400.
- Medium: 500.
- Semibold: 600.
- Bold raramente.

## Ícones

Tamanhos:

- xs: 12px.
- sm: 14px.
- md: 16px.
- lg: 20px.
- xl: 24px.

Stroke:

- 1.5px ou 2px, consistente por camada.

## Movimento

Durações:

- instant: 80ms a 120ms.
- fast: 150ms.
- normal: 200ms.
- slow: 300ms.
- complex: até 400ms.

Easing:

- Entrada: ease-out.
- Saída: ease-in.
- Troca de estado: ease-in-out.

Respeitar reduced motion.

## Opacidade

- disabled: 40% a 50%.
- muted icon: 60%.
- scrim dialog: 40% a 60%.
- hover tint: 4% a 8%.
- selected tint: 6% a 10%.

