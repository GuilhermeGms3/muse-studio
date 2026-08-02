# Tab System

As abas são permanentes e centrais na identidade do Muse Studio.

## Função Das Abas

Abas representam superfícies concretas de trabalho. Elas permitem alternar entre prática, estudo, repertório, composição e revisão sem perder contexto.

## Abertura De Abas

Abrir nova aba quando:

- O usuário abre macro workspace: Home, Plano, Sessão, Diário, Skill Tree, Mapa, Biblioteca, Repertório, Exercícios, Ear Training, Metrônomo, Projetos, Dados.
- O usuário abre objeto substancial: música, aula, habilidade, exercício, projeto, entrada de diário ou coleção de gravações.
- O usuário seleciona resultado específico na busca global.
- Uma ação gera superfície própria, como plano de prática de música ou resumo de sessão.

Não abrir nova aba quando:

- O usuário alterna instrumento.
- O usuário expande card.
- O usuário abre menu temporário.
- O usuário edita campo inline.
- O usuário visualiza detalhe que pertence ao Inspector.

## Fluxo De Abertura

```text
Acao do usuario
  -> Identificar alvo
  -> Existe aba com mesmo objeto?
    -> Sim: ativar aba existente
    -> Nao: criar aba nova
  -> Atualizar contexto selecionado
  -> Atualizar Inspector conforme selecao
```

## Fechamento De Abas

Fluxo:

```text
Clique em fechar aba
  -> Aba possui alteracoes nao salvas?
    -> Sim: confirmar salvar, descartar ou cancelar
    -> Nao: fechar
  -> Ativar aba vizinha mais recente
  -> Se nenhuma aba restar: abrir Home
```

Regras:

- Home pode ser fixa ou reaberta automaticamente.
- Sessão ativa deve pedir confirmação antes de fechar.
- Fechar uma aba não apaga dados.
- Fechar objeto com gravação em andamento deve pausar ou pedir decisão.

## Múltiplas Abas

Comportamento esperado:

- Usuário pode manter música, skill, exercício e diário abertos juntos.
- Abrir objeto relacionado não substitui a aba atual.
- Links internos devem ativar aba existente quando possível.
- Abas recentes ajudam Recognition over Recall.

Exemplo:

```text
Home | Plano de Hoje | Canon Rock | Bends | AP 01 | Diario
```

## Navegação Entre Abas

Ações:

- Clique na aba.
- Atalho para próxima aba.
- Atalho para aba anterior.
- Busca global por abas abertas.
- Voltar/avançar histórico interno quando aplicável.

## Estados Das Abas

- Ativa.
- Inativa.
- Fixada futura.
- Com alteração não salva.
- Com sessão ativa.
- Com erro de carregamento.
- Carregando.

## Nomes De Abas

Usar nomes humanos:

- Home
- Sessão
- Plano de Hoje
- Canon Rock
- Intervalos
- Bends
- Projeto: Ideias Prog
- Dados e Integrações

Evitar:

- Rotas.
- IDs.
- Termos técnicos de implementação.

## Regras De Atenção

- A aba ativa deve ser óbvia.
- Abas não devem competir com o conteúdo central.
- Badges devem ser discretos.
- Sessão ativa pode ter indicador persistente.

