# Search Experience

Pesquisa global é o caminho rápido para usuários que sabem o que querem. Ela pode expor muitas capacidades porque o usuário expressou intenção.

## Objetivo

Permitir encontrar e abrir:

- Módulos.
- Aulas.
- Músicas.
- Exercícios.
- Habilidades.
- Projetos.
- Arquivos importados.
- Comandos.
- Abas abertas.

## Fluxo

```text
Ctrl/Cmd+K ou clique em Buscar
  -> Palette abre
  -> Usuario digita
  -> Resultados agrupados aparecem
  -> Usuario navega por teclado ou mouse
  -> Enter abre/ativa alvo
  -> Palette fecha
```

## Grupos De Resultado

Ordem recomendada:

1. Abas abertas.
2. Comandos.
3. Módulos.
4. Músicas.
5. Habilidades.
6. Aulas/Biblioteca.
7. Exercícios.
8. Projetos.
9. Arquivos importados.

## Comportamento De Abertura

- Se resultado já está aberto: ativar aba existente.
- Se resultado é objeto substancial: abrir nova aba.
- Se resultado é comando contextual: executar comando.
- Se resultado é ferramenta rápida: abrir dock ou aba conforme intensidade.
- Se resultado é dado sensível, como restore: abrir tela apropriada, não executar direto.

## Busca Dentro Do Contexto

Quando a palette abre, pode priorizar resultados do contexto atual:

- Em Praticar: sessão, plano, exercícios, repertório ativo, metronomo.
- Em Aprender: skills, mapa, aulas, revisões.
- Em Biblioteca: conteúdos, repertório, artistas, importação.
- Em Compor: projetos, riffs, ideias, referências.
- Em Revisar: diário, sessões, evidências, revisões.
- Em Ferramentas: dados, backup, importação, MIDI, preferências.

## Empty State

Mensagem:

- "Nada encontrado neste workspace."

Ações:

- Mostrar comandos do contexto atual.
- Sugerir remover filtros se existirem.

## Error State

Mensagem:

- "Alguns resultados não puderam ser carregados."

Comportamento:

- Mostrar resultados disponíveis.
- Indicar grupo com erro.
- Permitir tentar novamente.

## Loading State

- Campo permanece ativo.
- Grupos podem preencher progressivamente.
- Skeleton pequeno ou texto "Buscando..." por grupo.

## Regras De UX

- Pesquisa não substitui arquitetura de informação.
- Pesquisa deve respeitar permissões e disponibilidade local.
- Resultados precisam indicar tipo e destino.
- O resultado selecionado deve ser visualmente óbvio.
- O Enter nunca deve disparar ação destrutiva direta.

