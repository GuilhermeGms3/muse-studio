# Navigation System

Muse Studio should use a four-level navigation model that preserves power while reducing visible complexity.

## Level 1: Macro Context

Macro contexts are the main mental modes:

- Home
- Praticar
- Aprender
- Biblioteca
- Compor
- Revisar
- Ferramentas

The user should never see every feature as equal siblings. Selecting a macro context changes the contextual menu.

## Level 2: Contextual Menus

### Home

- Estação de hoje
- Continuar
- Objetivo atual
- Notas rápidas
- Projetos recentes
- Diagnóstico inicial

### Praticar

- Nova sessão
- Continuar sessão
- Plano de hoje
- Exercícios
- Repertório em prática
- Treino de ouvido
- Metrônomo
- Gravações de prática
- Objetivos ativos

### Aprender

- Skill Tree
- Mapa do conhecimento
- Trilhas por instrumento
- Conceitos em progresso
- Revisões pendentes
- Pré-requisitos
- Próximos passos

### Biblioteca

- Conteúdos
- Teoria musical
- Ritmo
- Harmonia
- Campo harmônico
- Intervalos
- Escalas
- Modos gregos
- Formação de acordes
- Improvisação
- Técnicas
- Leitura
- Ear Training
- Repertório
- Playlists
- Favoritos
- Downloads
- Importação
- Artistas

### Compor

- Projetos
- Riffs
- Letras
- Ideias
- Referências
- Versões
- Gravações
- Preparação para Reaper

### Revisar

- Diário
- Histórico de sessões
- Evidências de habilidade
- Revisões espaçadas
- Progresso por instrumento
- Músicas em manutenção
- Exportações

### Ferramentas

- Metrônomo
- Gravador
- Afinador futuro
- Importar arquivos
- Backup
- Restaurar dados
- Exportar diário
- MIDI
- Integrações
- Preferências

## Level 3: Workspace Tabs

Tabs represent concrete surfaces:

- Home
- Plano de Hoje
- Sessão
- Diário
- Skill Tree
- Mapa do Conhecimento
- Biblioteca
- Lesson: Intervalos
- Repertório
- Song: Canon Rock
- Exercícios
- Exercise: Alternate Picking 1
- Ear Training
- Metrônomo
- Projetos
- Project: Ideias Prog
- Dados e Integrações

Tabs are not removed. They are a central identity of the product.

## Level 4: Modules

Tabs contain independent modules:

- Cards for repeated items and compact object summaries.
- Panels for bounded work areas.
- Inspector for selected object details and secondary actions.
- Docked tools for metronome, recorder, playback, imports, and contextual utilities.
- Contextual action bars for object-specific commands.

## Complete Navigation Map

```text
Home
  Estação de hoje -> tab: Home
  Continuar -> tab: last object
  Objetivo atual -> tab: Skill Tree or selected skill
  Diagnóstico inicial -> tab: Diagnóstico

Praticar
  Nova sessão -> tab: Sessão
  Continuar sessão -> tab: Sessão
  Plano de hoje -> tab: Plano de Hoje
  Exercícios -> tab: Exercícios
  Repertório em prática -> tab: Repertório filtered to active songs
  Treino de ouvido -> tab: Ear Training
  Metrônomo -> tab or dock: Metrônomo
  Gravações de prática -> inspector/dock or filtered review tab

Aprender
  Skill Tree -> tab: Skill Tree
  Mapa do conhecimento -> tab: Mapa do Conhecimento
  Trilhas por instrumento -> tab: Skill Tree filtered by instrument
  Conceitos em progresso -> tab: Biblioteca filtered by active skills
  Revisões pendentes -> tab: Revisar filtered by due reviews

Biblioteca
  Conteúdos -> tab: Biblioteca
  Category -> tab: Biblioteca filtered by category
  Repertório -> tab: Repertório
  Playlists/Favoritos/Downloads -> tab: Biblioteca filtered collection
  Importação -> tab: Dados e Integrações
  Artistas -> tab: Repertório grouped by artist

Compor
  Projetos -> tab: Projetos
  Project -> tab: Project detail
  Riffs/Letras/Ideias/Referências/Versões -> project modules
  Gravações -> project recording module
  Preparação para Reaper -> integration planning module

Revisar
  Diário -> tab: Diário
  Histórico de sessões -> tab: Diário filtered by sessions
  Evidências de habilidade -> tab: Skill Tree selected evidence
  Revisões espaçadas -> tab: Review queue
  Progresso por instrumento -> tab: Review filtered by instrument
  Exportações -> tab: Dados e Integrações

Ferramentas
  Metrônomo -> tab or dock: Metrônomo
  Gravador -> contextual module
  Importar arquivos -> tab: Dados e Integrações
  Backup/Restaurar/Exportar diário -> tab: Dados e Integrações
  MIDI/Integrações/Preferências -> tools modules
```

## Search And Command Palette

Global search remains a universal escape hatch. It should find:

- Modules
- Lessons
- Songs
- Exercises
- Skills
- Projects
- Imported files
- Commands
- Recent tabs

Search opens the most specific tab possible.

