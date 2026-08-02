# Context Flows

Cada macro contexto responde uma pergunta principal. O menu contextual muda conforme o contexto, e o workspace em abas é sempre o centro da experiência.

## Fluxo Inicial

```text
Abrir Muse Studio
  -> Home
    -> Se sem diagnóstico: Diagnóstico Inicial
    -> Se com diagnóstico: Estação de Hoje
    -> Ações: Iniciar sessão, Continuar, Abrir objetivo, Explorar biblioteca
```

## Home

Pergunta principal: "O que merece minha atenção agora?"

Fluxo:

```text
Home
  -> Estação de hoje
  -> Continuar
  -> Objetivo atual
  -> Notas rápidas
  -> Projetos recentes
  -> Diagnóstico inicial
```

Workspace:

- Aba padrão: Home.
- Cards: Plano de hoje, Continuar, Objetivo atual, Música atual, Projetos recentes, Notas rápidas.
- Inspector: contexto do instrumento, sessão ativa, habilidades em desenvolvimento.

## Praticar

Pergunta principal: "Como começo ou continuo uma prática?"

Fluxo:

```text
Praticar
  -> Nova sessão
    -> Aba Sessão
      -> Atividade atual
      -> Timer/BPM/Notas
      -> Feedback
      -> Resumo
  -> Continuar sessão
  -> Plano de hoje
  -> Exercícios
  -> Repertório em prática
  -> Treino de ouvido
  -> Metrônomo
  -> Gravações de prática
  -> Objetivos ativos
```

Regras:

- Nova sessão deve ser a ação mais direta.
- Metrônomo e gravador devem aparecer como ferramenta contextual, dock ou aba, conforme intensidade de uso.
- Histórico pertence a Revisar, mas pode ser acessado por link contextual após finalizar.

## Biblioteca

Pergunta principal: "Que material musical eu quero consultar ou organizar?"

Fluxo:

```text
Biblioteca
  -> Conteúdos
    -> Categoria
      -> Aula/Conceito
        -> Conteúdo, exemplos, diagramas, relações
  -> Repertório
  -> Playlists
  -> Favoritos
  -> Downloads
  -> Importação
  -> Artistas
```

Regras:

- Biblioteca é tanto acervo quanto porta de entrada para repertório.
- Importação pode abrir Dados e Integrações sem perder o contexto de Biblioteca.
- Aulas abrem em abas próprias quando são objetos substanciais.

## Aprender

Pergunta principal: "Que habilidade ou conceito devo entender agora?"

Fluxo:

```text
Aprender
  -> Skill Tree
    -> Habilidade
      -> Critério atual
      -> Pré-requisitos
      -> Conteúdos
      -> Exercícios
      -> Músicas
  -> Mapa do conhecimento
  -> Trilhas por instrumento
  -> Conceitos em progresso
  -> Revisões pendentes
  -> Próximos passos
```

Regras:

- Aprender prioriza relações entre conhecimentos.
- Skill Tree e Mapa podem compartilhar objetos, mas Mapa deve enfatizar exploração.
- Revisões pendentes podem enviar o usuário para Revisar.

## Compor

Pergunta principal: "Como desenvolvo uma ideia musical?"

Fluxo:

```text
Compor
  -> Projetos
    -> Projeto
      -> Riffs
      -> Letras
      -> Ideias
      -> Referências
      -> Versões
      -> Gravações
      -> Preparação Reaper
```

Regras:

- Captura rápida deve ficar visível.
- Detalhes avançados aparecem por seleção.
- Reaper é preparação contextual, não centro da tela.

## Revisar

Pergunta principal: "O que precisa ser lembrado, mantido ou ajustado?"

Fluxo:

```text
Revisar
  -> Diário
  -> Histórico de sessões
  -> Evidências de habilidade
  -> Revisões espaçadas
  -> Progresso por instrumento
  -> Músicas em manutenção
  -> Exportações
```

Regras:

- Revisão deve sempre oferecer próximo passo.
- Histórico pode abrir objetos relacionados em abas.
- Exportações levam para Dados e Integrações.

## Ferramentas

Pergunta principal: "Que utilitário apoia meu trabalho agora?"

Fluxo:

```text
Ferramentas
  -> Metrônomo
  -> Gravador
  -> Afinador futuro
  -> Importar arquivos
  -> Backup
  -> Restaurar dados
  -> Exportar diário
  -> MIDI
  -> Integrações
  -> Preferências
```

Regras:

- Ferramentas frequentes podem ser dock.
- Ferramentas perigosas exigem confirmação.
- Preferências não devem parecer painel administrativo.

## Configurações / Preferências

Pergunta principal: "Como ajusto meu estúdio pessoal sem sair do fluxo musical?"

Fluxo:

```text
Ferramentas
  -> Preferências
    -> Instrumento principal
    -> Tempo padrão de sessão
    -> Nível
    -> Gêneros, artistas e músicas favoritas
    -> Diretório local de dados
    -> Integrações
    -> Atalhos futuros
```

Regras:

- Preferências pertencem a Ferramentas, mas devem abrir uma aba própria quando exigirem edição.
- Preferências não devem usar linguagem de SaaS, equipe, billing ou organização.
- Alterações de instrumento e tempo devem explicar impacto no plano e nas recomendações.
- Configuração sensível de dados locais deve usar confirmação.

## Pesquisa Global

```text
Ctrl/Cmd+K
  -> Campo de busca
    -> Módulos
    -> Aulas
    -> Músicas
    -> Exercícios
    -> Habilidades
    -> Projetos
    -> Arquivos importados
    -> Comandos
  -> Enter
    -> Ativa aba existente ou abre nova aba específica
```
