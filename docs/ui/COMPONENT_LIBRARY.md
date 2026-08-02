# Component Library

Este documento define padrões visuais para componentes. Não é especificação de implementação.

## Botões

Tipos:

- Primário.
- Secundário.
- Ghost.
- Icon-only.
- Destrutivo.
- Toggle.

Regras:

- Uma ação primária por tela ou módulo.
- Botões compactos no chrome.
- Icon-only sempre com tooltip/label acessível.
- Destrutivo separado visualmente.
- Loading preserva largura.

## Inputs

Regras:

- Label visível.
- Altura 32px a 36px.
- Erro abaixo do campo.
- Helper text para campos complexos.
- Focus ring visível.

## Dropdown / Combobox

Uso:

- Seleção de instrumento.
- Filtros.
- Estado de música.
- Técnica.
- Categoria.

Regras:

- Mostrar valor atual.
- Suportar busca quando lista for longa.
- Não usar para ações destrutivas.

## Search

Tipos:

- Busca global.
- Busca dentro da aba.
- Filtro de lista.

Regras:

- Busca global é proeminente no title bar.
- Busca contextual fica dentro da aba.
- Resultados mostram tipo e destino.

## Dialogs

Uso:

- Confirmação.
- Criação/edição focada.
- Restauração de backup.
- Descartar gravação.

Regras:

- Título claro.
- Uma ação principal.
- Cancelar sempre visível.
- Foco inicial correto.

## Context Menu

Uso:

- Ações secundárias de card.
- Ações de aba.
- Ações de objeto.

Regras:

- Itens curtos.
- Ícone opcional.
- Destrutivo separado.
- Não esconder ação principal apenas no menu.

## Command Palette

Regras:

- Campo de busca no topo.
- Resultados por grupo.
- Ícones discretos.
- Atalho visível.
- Enter não executa destrutivo direto.

## Breadcrumb

Uso:

- Objetos profundos.
- Biblioteca.
- Mapa do conhecimento.
- Repertório por artista/playlist.

Regras:

- Compacto.
- Não substituir tabs.
- Último item não clicável.

## Tabs

Seguir `TAB_SYSTEM.md`.

## Accordions

Uso:

- Detalhes avançados.
- Erros comuns.
- Evidências.
- Histórico.

Regras:

- Estado expandido claro.
- Não esconder conteúdo essencial.

## Sidebar / Navbar / Toolbar

Sidebar:

- Contextual, colapsável, hierárquica.

Navbar/title bar:

- Persistente, técnica e compacta.

Toolbar:

- Ações da aba ativa.
- Não conter navegação global.

## Badges

Uso:

- Estado.
- Tipo.
- Dificuldade.
- Pendente.

Regras:

- Pequenos.
- Sem saturação excessiva.
- Texto curto.
- Cor acompanhada de texto/ícone.

## Avatares

Uso limitado:

- Não há foco em usuários/equipes.
- Podem representar instrumentos ou artistas futuramente, mas não como padrão central.

## Listas E Árvores

Listas:

- Altura de linha estável.
- Seleção clara.
- Virtualização futura para listas longas.

Árvores:

- Biblioteca.
- Skill dependencies.
- Mapa lateral.

Regras:

- Recuos consistentes.
- Estado expandido visível.

## Tabelas

Uso:

- Exportações.
- Histórico detalhado.
- Dados locais.

Regras:

- Não usar tabela para tudo.
- Cabeçalho fixo quando longa.
- Números alinhados.

## Widgets

Tipos:

- Timer.
- BPM.
- Metrônomo.
- Gravador.
- Progress meter.
- State tag.

Regras:

- Compactos.
- Próximos do contexto.
- Sem estilo de KPI corporativo.

