# Interaction Patterns

## Seleção

Selecionar um item atualiza:

- Estado visual do item.
- Conteúdo principal quando o item pertence à mesma aba.
- Inspector quando o detalhe é secundário.
- Status bar quando muda estado relevante.

Selecionar não deve sempre abrir aba. Abrir aba é reservado para objetos substanciais.

## Edição

Fluxo:

```text
Selecionar objeto
  -> Acao Editar
  -> Campos aparecem inline, em painel ou dialog
  -> Salvar
  -> Confirmar estado salvo
```

Regras:

- Edição inline para campos pequenos.
- Painel para objetos complexos.
- Dialog apenas para criação/edição focada.
- Não perder rascunho em falha de rede/API.
- Excluir sempre exige confirmação.

## Inspeção

Fluxo:

```text
Selecionar card, nó ou módulo
  -> Inspector abre ou atualiza
  -> Exibe contexto secundário
  -> Ações rápidas abrem abas ou executam comandos
```

Inspector deve mostrar relações, não duplicar a tela.

## Painéis Laterais

Sidebar:

- Navegação por contexto.
- Colapsável.
- Mostra somente menu do macro contexto ativo.

Inspector:

- Detalhes e relações.
- Colapsável.
- Atualiza com seleção.

Regras:

- Sidebar responde "onde estou?"
- Inspector responde "o que isso significa?"
- Workspace responde "o que faço agora?"

## Dock Inferior

Uso:

- Metronomo.
- Gravador.
- Player de áudio.
- Status de importação.
- Transporte futuro.

Fluxo:

```text
Acionar ferramenta contextual
  -> Se uso rapido: abre dock compacto
  -> Se uso intenso: abrir aba de ferramenta
  -> Dock pode recolher sem encerrar ferramenta
```

Regras:

- Dock não pode cobrir ação principal.
- Dock deve indicar estado ativo.
- Ferramenta em execução deve sobreviver à troca de abas quando fizer sentido.

## Ferramentas Contextuais

Ferramentas aparecem onde ajudam:

- Metrônomo na sessão, música, exercício e ferramentas.
- Gravador na sessão, música e projeto.
- Importação na Biblioteca e Ferramentas.
- Backup/exportação em Revisar e Ferramentas.

## Ações Rápidas

Devem ser curtas e previsíveis:

- Iniciar sessão.
- Continuar.
- Abrir.
- Gravar.
- Gerar plano.
- Registrar evidência.
- Marcar concluído.
- Importar.
- Exportar.

## Confirmações

Exigir confirmação para:

- Finalizar sessão ativa.
- Fechar aba com alteração não salva.
- Restaurar backup.
- Excluir conteúdo.
- Descartar gravação.

Não exigir confirmação para:

- Abrir aba.
- Trocar contexto.
- Expandir card.
- Alternar inspector.
- Ajustar BPM.

## Atalhos

Atalhos devem acelerar, não substituir a navegação visível. Todo atalho precisa ter equivalente na interface.

## Feedback Visual

Cada ação deve responder com:

- Mudança de estado.
- Mensagem discreta quando necessário.
- Persistência clara para dados importantes.
- Erro recuperável quando falhar.

