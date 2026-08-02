# Layout Guidelines

## Grid

O layout usa grid modular baseado em 4px e 8px.

Grid recomendado:

- Base: 4px.
- Espaçamento principal: 8px.
- Gaps de módulos: 12px ou 16px.
- Margem de workspace: 16px.
- Gutter largo: 24px quando houver leitura.

## Larguras

- Sidebar expandida: 240px a 288px.
- Sidebar compacta: 56px a 72px.
- Inspector: 280px a 360px.
- Dock inferior: 160px a 280px de altura, conforme ferramenta.
- Card pequeno: 180px a 240px.
- Card médio: 280px a 360px.
- Card grande: 420px a 640px.
- Linha de lista: 40px a 56px.

## Alturas

- Title bar: 40px.
- Tab bar: 36px.
- Status bar: 24px.
- Toolbar compacta: 32px.
- Botão compacto: 28px a 32px visual, com área clicável mínima adequada.
- Input padrão: 32px a 36px.
- Card pequeno: 72px a 104px.
- Card médio: 120px a 180px.
- Card grande: 220px ou mais apenas quando contém trabalho real.

## Zonas De Layout

Chrome:

- Denso.
- Fixo.
- Sem cards.
- Sem conteúdo longo.

Sidebar:

- Hierárquica.
- Escaneável.
- Scroll independente quando necessário.

Workspace:

- Área principal.
- Conteúdo em cards e painéis.
- Scroll principal da aba.

Inspector:

- Coluna de apoio.
- Painéis empilhados.
- Conteúdo curto.

Dock:

- Ferramenta ativa.
- Estado persistente.
- Recolhível.

## Responsividade

Desktop amplo:

- Sidebar + workspace + inspector podem coexistir.
- Grids podem usar 3 ou 4 colunas.

Desktop médio:

- Inspector pode recolher.
- Cards usam 2 ou 3 colunas.

Tela estreita:

- Sidebar vira rail ou overlay.
- Inspector vira painel recolhível.
- Dock ocupa menos altura.
- Cards empilham.

## Regras De Leitura

- Textos longos devem limitar medida de linha.
- Painéis de leitura não devem ocupar largura total.
- Tabelas só quando comparação for essencial.
- Listas grandes devem manter altura de linha estável.

## Z-Index Conceitual

- Base: workspace.
- 10: cards e estados hover.
- 20: sticky toolbars.
- 30: sidebar/inspector/dock.
- 40: popovers/context menus.
- 50: command palette.
- 60: dialogs.
- 70: critical confirmations.

