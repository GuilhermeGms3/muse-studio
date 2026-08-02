# Accessibility

Acessibilidade é requisito central do Design System. Muse Studio é um software de uso prolongado; legibilidade, foco e navegação por teclado são parte da qualidade visual.

## Contraste

Regras:

- Texto normal: mínimo 4.5:1.
- Texto grande: mínimo 3:1.
- Ícones informativos: mínimo 3:1.
- Estados de erro/sucesso precisam de texto ou ícone além da cor.

## Tamanho Mínimo

Área interativa:

- 44px por 44px como alvo mínimo recomendado.
- Controles compactos podem parecer menores visualmente, mas precisam de hit area adequada.

Texto:

- Evitar texto abaixo de 11px.
- Corpo de leitura deve ficar em 14px a 16px.
- Labels compactos podem usar 12px com contraste forte.

## Foco

Todo elemento interativo deve ter:

- Estado focus visível.
- Ordem lógica.
- Label acessível.
- Estado selecionado/expandido/desativado anunciado quando aplicável.

## Navegação Por Teclado

Deve ser possível:

- Abrir busca global.
- Navegar resultados.
- Trocar abas.
- Fechar aba.
- Alternar sidebar.
- Alternar inspector.
- Navegar cards.
- Ativar ações primárias.
- Cancelar dialogs.

## Leitura

Regras:

- Hierarquia de títulos sequencial.
- Não usar caixa alta para blocos longos.
- Linhas de texto longas devem ter largura limitada.
- Tooltips não podem ser única fonte de informação essencial.

## Movimento Reduzido

Quando reduced motion estiver ativo:

- Remover transições espaciais complexas.
- Manter fades simples.
- Não animar grandes áreas.
- Preservar feedback de estado.

## Cor Não É Único Indicador

Exemplos:

- Erro: cor + ícone + texto.
- Seleção: cor + borda/posição.
- Sessão ativa: cor + ícone/texto.
- Concluído: cor + check/texto.

## Drag And Drop

Toda ação de drag futura deve ter alternativa:

- Menu de contexto.
- Comando de teclado.
- Botões mover antes/depois.

## Dialogs

Regras:

- Foco vai para dialog.
- Esc cancela quando seguro.
- Foco retorna ao trigger.
- Ação destrutiva exige confirmação clara.

## Command Palette

Regras:

- Campo recebe foco ao abrir.
- Resultados são navegáveis por setas.
- Grupo e tipo devem ser compreensíveis por leitor de tela.
- Enter não executa destrutivo direto.

