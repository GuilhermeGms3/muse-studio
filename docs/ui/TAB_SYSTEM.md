# Tab System

Abas são protagonistas do Muse Studio. Elas representam superfícies de trabalho abertas, não apenas navegação.

## Anatomia

Cada aba possui:

- Ícone opcional.
- Título humano.
- Estado.
- Indicador de contexto quando útil.
- Botão de fechar quando permitido.
- Tooltip com nome completo quando truncada.

## Tipos De Aba

Aba permanente:

- Home.
- Sessão ativa.
- Superfícies fixadas pelo usuário no futuro.

Aba comum:

- Música.
- Aula.
- Skill.
- Projeto.
- Diário.
- Exercício.

Aba temporária:

- Preview de busca.
- Objeto aberto rapidamente.
- Pode ser promovida a comum ao editar, fixar ou interagir.

Aba gerada:

- Plano de prática.
- Resumo de sessão.
- Resultado de importação.

## Estados

Ativa:

- Fundo conectado ao workspace.
- Borda superior ou inferior contextual.
- Texto primário.

Inativa:

- Fundo levemente diferenciado.
- Texto secundário.

Hover:

- Fundo mais claro.
- Fechar visível.

Focus:

- Anel claro.

Modificada:

- Ponto discreto antes do botão de fechar.

Salva:

- Sem indicador.

Carregando:

- Spinner pequeno ou shimmer no título.

Erro:

- Indicador pequeno de erro.
- Tooltip explica.

Sessão ativa:

- Indicador persistente de execução.
- Fechar exige confirmação.

## Drag And Drop

Regras:

- Arrastar aba deve mostrar ghost claro.
- Drop target deve aparecer entre abas.
- Reordenar não altera conteúdo.
- Usuário de teclado deve ter alternativa futura.

## Fechamento

- Botão aparece no hover/focus ou sempre quando há espaço.
- Aba com mudanças não salvas exige confirmação.
- Aba com gravação ativa exige confirmação.
- Fechar última aba abre Home.

## Reabertura E Histórico

Sistema visual deve prever:

- Lista de abas recentes.
- Reabrir última aba fechada.
- Histórico por objeto.
- Persistência de abas entre sessões futuras.

## Persistência

Persistir conceitualmente:

- Abas abertas.
- Aba ativa.
- Ordem das abas.
- Estado de sidebar/inspector/dock.
- Seleção do objeto quando seguro.

Não persistir:

- Dialog aberto.
- Menu temporário.
- Hover.
- Erro transitório já resolvido.

## Densidade

- Altura: 32px a 36px.
- Largura mínima: 120px.
- Largura máxima: 220px.
- Aba pinada: 40px a 48px.
- Gap entre abas: 0px ou 1px.

## Regras

- Títulos de abas nunca usam IDs ou rotas.
- Aba ativa deve ser inequívoca.
- Abas não devem parecer botões grandes.
- Muitas abas devem truncar com tooltip e overflow controlado.

