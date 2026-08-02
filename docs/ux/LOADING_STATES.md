# Loading States

Carregamento deve manter a sensação de workspace vivo. O usuário não deve sentir que o software inteiro parou.

## Princípios

- Carregar módulos de forma independente.
- Preservar chrome, abas, sidebar, busca e status bar.
- Usar skeletons compactos.
- Evitar telas inteiras bloqueadas.
- Indicar ações em andamento em botões e status bar.

## Home

Carregar:

- Plano de hoje.
- Continue point.
- Objetivo atual.
- Projetos recentes.

Padrão:

- Skeleton por card.
- Home permanece navegável.

## Diagnóstico

Carregar:

- Salvamento final.

Padrão:

- Botão "Salvando".
- Bloquear envio duplicado.
- Não bloquear voltar até envio começar.

## Sessão

Carregar:

- Criação da sessão.
- Salvamento de feedback.
- Finalização.
- Resumo.

Padrão:

- Durante criação: tela compacta "Preparando sessão".
- Durante feedback: botão em loading.
- Durante finalização: painel "Gerando resumo".

## Biblioteca

Carregar:

- Índice.
- Conteúdos.
- Aula selecionada.

Padrão:

- Índice skeleton.
- Cards skeleton.
- Aula skeleton por módulo.

## Skill Tree / Mapa

Carregar:

- Nós.
- Relações.
- Evidência.

Padrão:

- Placeholder de grafo.
- Inspector skeleton quando seleção muda.

## Repertório / Música

Carregar:

- Lista de músicas.
- Detalhe de música.
- Geração de plano.

Padrão:

- Cards skeleton.
- Painel de música skeleton.
- Ação "Gerando plano".

## Projetos

Carregar:

- Lista.
- Projeto.
- Salvamento.

Padrão:

- Cards skeleton.
- Módulos independentes.
- Indicador de salvamento discreto.

## Dados E Integrações

Carregar:

- Importação.
- Backup.
- Restore.
- Exportação CSV.
- Status local.

Padrão:

- Barra de progresso quando arquivo existe.
- Status bar com tarefa em andamento.
- Ações sensíveis bloqueadas enquanto executam.

## Busca Global

Carregar:

- Resultados de múltiplas fontes.

Padrão:

- Resultados aparecem por grupo conforme ficam disponíveis.
- Campo de busca permanece ativo.
- Enter só age sobre resultado selecionado.

