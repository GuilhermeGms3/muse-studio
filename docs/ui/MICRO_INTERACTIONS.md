# Micro Interactions

Microinterações tornam o workspace vivo sem chamar atenção para si mesmas.

## Hover

Aplicar em:

- Cards.
- Botões.
- Itens de sidebar.
- Abas.
- Linhas de lista.

Comportamento:

- Alterar borda ou fundo.
- Revelar ações secundárias quando seguro.
- Não deslocar layout.

## Focus

Regras:

- Todo interativo tem foco visível.
- Foco não depende só de cor.
- Ordem segue leitura visual.
- Focus ring não deve ser removido.

## Click / Pressed

Comportamento:

- Feedback em até 100ms.
- Fundo ou opacidade muda brevemente.
- Sem animação que atrase ação.

## Drag

Aplicável a:

- Abas.
- Cards futuros.
- Painéis redimensionáveis.

Regras:

- Mostrar ghost.
- Mostrar drop target.
- Usar threshold para evitar arraste acidental.
- Oferecer alternativa de teclado futura.

## Drop

Comportamento:

- Destino destacado.
- Confirmação visual curta.
- Se falhar, item retorna e erro explica.

## Loading

Regras:

- Skeleton para cards e módulos.
- Spinner pequeno para botões.
- Progress para importação/exportação.
- Chrome permanece utilizável.

## Success

Uso:

- Salvamento.
- Importação.
- Feedback registrado.
- Backup gerado.

Comportamento:

- Mensagem breve.
- Não roubar foco.
- Indicar estado salvo quando importante.

## Erro

Comportamento:

- Mensagem próxima ao problema.
- Borda semântica.
- Ação de recuperação.
- Dados do usuário preservados.

## Empty State

Comportamento:

- Mensagem curta.
- Uma ação principal.
- Manter contexto.
- Sem ilustração decorativa grande.

## Expansão E Contração

Uso:

- Cards expansíveis.
- Accordions.
- Sidebar groups.
- Inspector panels.
- Dock.

Regras:

- 150ms a 250ms.
- Preservar posição visual.
- Reduced motion remove transição complexa.

## Troca De Contexto

Comportamento:

- Sidebar muda menu.
- Workspace preserva abas.
- Aba ativa não deve desaparecer sem ação explícita.
- Indicador de contexto muda discretamente.

## Abertura De Aba

Comportamento:

- Nova aba aparece próxima da aba ativa ou no final conforme regra futura.
- Aba recebe foco.
- Conteúdo carrega com skeleton se necessário.

## Fechamento De Aba

Comportamento:

- Aba sai rapidamente.
- Aba vizinha/recentemente usada recebe foco.
- Confirmação quando há risco.

