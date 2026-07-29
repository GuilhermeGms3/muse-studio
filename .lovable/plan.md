## Music OS — Estação de Trabalho Musical Pessoal

Aplicativo desktop single-user, estética DAW/IDE: denso, escuro, compacto, sem nada de SaaS.

### Shell da aplicação (sempre visível)

```text
┌──────────────────────────────────────────────────────────────┐
│ Title bar: instrumento ativo · busca global (⌘K) · metrônomo │
├────────────┬──────────────────────────────┬──────────────────┤
│ Sidebar    │ Área central com ABAS        │ Painel direito   │
│ em árvore  │ (múltiplos docs abertos)     │ (inspetor)       │
│            │ breadcrumb + conteúdo        │ contextual       │
├────────────┴──────────────────────────────┴──────────────────┤
│ Status bar: sessão ativa · cronômetro · BPM · atalhos        │
└──────────────────────────────────────────────────────────────┘
```

- Sidebar em árvore colapsável (Biblioteca › Teoria › Harmonia › ...), com Favoritos e Histórico no rodapé.
- Painéis redimensionáveis (drag nas divisórias), abas fecháveis/reordenáveis, breadcrumb no topo de cada aba.
- Command palette (⌘K) para busca global de conteúdos, músicas, exercícios, skills.

### Módulos / telas

1. **Home (estação de trabalho)** — grade densa: Sessão de hoje, Plano do dia (blocos de minutos), Última prática, Música atual, Próximo objetivo, mini Skill Tree, Notas rápidas, Projetos recentes. Zero KPIs/gráficos corporativos.
2. **Biblioteca** — árvore de categorias (Teoria, Ritmo, Harmonia, Campo Harmônico, Intervalos, Escalas, Modos Gregos, Formação de Acordes, Improvisação, Técnicas, Leitura, Ear Training). Cada artigo: texto, diagramas, braço de guitarra, teclado, exemplos, links internos, notas.
3. **Plano de Estudos** — timeline diária de blocos com duração, instrumento e alvo; reorganização automática por desempenho (regras de prioridade: skill fraca / revisão atrasada / meta de BPM).
4. **Sessão de Prática** — modo focado: cronômetro, instrumento, lista de exercícios, BPM, anotações ao vivo; tela de encerramento com dificuldades, conquistas e próximos passos.
5. **Diário** — histórico cronológico de sessões com o que foi treinado, dificuldades e melhorias (ex.: 90→110 BPM).
6. **Repertório** — músicas com afinação, tom, BPM, instrumento, dificuldade, técnicas/escalas usadas, status, arquivos; divisão em seções (Intro, Verso, Refrão, Solo, Bridge, Final) com progresso por seção.
7. **Exercícios** — banco filtrável por técnica (alternate, economy, hybrid, sweep, tapping, legato, bends, etc.), com BPM alvo/atingido.
8. **Ear Training** — treinos de intervalos, acordes, escalas, ritmos, progressões, melodias.
9. **Metrônomo** — BPM, compasso, subdivisão, acentuação, presets, favoritos, histórico. Acoplável no painel inferior durante sessões.
10. **Projetos Musicais** — riffs, letras, ideias, acordes, versões, notas, referências, arquivos.
11. **Skill Tree** — mapa vertical grande com dependências e estados: Bloqueada → Disponível → Aprendendo → Praticando → Consistente → Dominada → Natural → Especialista. Sem XP, moedas ou níveis; evolução por horas, BPM, precisão, constância, músicas, exercícios, revisões, autoavaliação.
12. **Mapa do Conhecimento** — grafo interativo navegável (pan/zoom); clicar num nó abre inspetor com pré-requisitos, relacionados, conteúdos, músicas, exercícios, projetos e próximos passos.

### Multi-instrumento

Guitarra, Violão e Teclado como contextos independentes: cada um tem sua própria árvore de skills, repertório, exercícios e progresso. Troca de instrumento na title bar recontextualiza a interface.

### Dados

Primeira versão com camada de dados local (store tipado + persistência no navegador) atrás de uma interface de repositório, para depois trocar por banco sem reescrever telas. Seeds ricos de conteúdo, skills, exercícios e repertório para o app já abrir cheio.

### Integração Reaper (só arquitetura)

Camada `integrations/reaper` com tipos e interface (projetos, takes, BPM, marcadores) e implementação stub — nada funcional agora.

### Visual

Tema escuro único, cinzas/preto, azul e verde discretos, laranja só para destaque. Tipografia técnica (sans compacta + mono para números/BPM). Raio pequeno, bordas 1px, sem sombras/gradientes, densidade alta, tudo em tokens semânticos.

### Detalhes técnicos

- TanStack Start com rotas por módulo; abas mantidas em store de workspace.
- Componentes reutilizáveis: `TreeView`, `TabBar`, `SplitPane`, `Inspector`, `Fretboard`, `Keyboard`, `SkillNode`, `GraphCanvas`, `Metronome`, `SessionTimer`.
- Pastas: `src/features/<modulo>`, `src/components/workspace`, `src/data`, `src/integrations/reaper`.
- Atalhos: ⌘K busca, ⌘B sidebar, ⌘\ painel direito, ⌘P sessão, espaço metrônomo.
