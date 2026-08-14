# Learning Missions

## Objetivo

Learning Mission é a unidade pedagógica apresentada ao usuário. Ela transforma
uma habilidade abstrata em uma experiência musical concreta, com propósito,
material, prática, avaliação e consequência visível.

O usuário não deve receber apenas "Escalas maiores — executar lentamente". Deve
receber uma missão que explique o que fará, por que fará e o que poderá tocar ou
desbloquear ao demonstrar a competência.

## Anatomia de uma missão

Uma missão deve possuir, quando aplicável:

- título orientado à ação musical;
- objetivo observável;
- contexto e motivação;
- duração prevista;
- habilidades trabalhadas;
- pré-requisitos;
- aula ou explicação;
- vídeo ou demonstração visual;
- áudio de referência;
- tablatura, partitura, cifra ou diagrama;
- exercícios graduados;
- opção de reduzir a dificuldade;
- aplicação em trecho musical;
- avaliação;
- evidências esperadas;
- critérios de conclusão;
- revisão futura;
- consequência pedagógica, como nova missão ou música disponível.

## Exemplo conceitual

```text
Missão 17 — Primeiros acordes maiores

Hoje você aprenderá:
- G
- D
- Em

Objetivo:
Trocar entre os acordes em até um segundo, mantendo o pulso.

Duração prevista:
18 minutos

Etapas:
1. Assistir à demonstração.
2. Ouvir e reconhecer cada acorde.
3. Montar os acordes sem tempo.
4. Praticar a troca lentamente.
5. Tocar com metrônomo.
6. Aplicar em um trecho musical.
7. Gravar uma tentativa.
8. Fazer a avaliação.

Resultado:
Cria evidências para troca de acordes e pode liberar uma missão de repertório.
```

Valores e limiares do exemplo não são requisitos universais. Cada competência
precisa de critérios próprios e calibrados.

## Missão, aula e exercício são objetos diferentes

- **Skill:** competência que o sistema acompanha internamente.
- **Mission:** jornada curta com objetivo musical e começo, meio e fim.
- **Lesson:** ensina conceito, percepção ou movimento.
- **Exercise:** cria uma condição controlada de prática.
- **Assessment:** coleta evidências sob condições conhecidas.
- **Mastery:** conclusão probabilística baseada em evidências acumuladas.

Uma missão pode trabalhar mais de uma skill. Uma skill pode exigir várias
missões, aplicações e revisões antes de ser considerada estável.

## Comportamento em abas

Objetos pedagógicos importantes devem abrir como superfícies completas:

```text
Home | Missão 17 | Exercício 2 | Música: seção do refrão
```

Ao abrir uma skill disponível, o usuário vê uma aba com sua explicação, missões,
aulas, exercícios, evidências, aplicações e próximos caminhos. Uma skill
trancada também pode ser inspecionada, mas deve explicar claramente:

- por que está trancada;
- quais competências faltam;
- quais evidências são exigidas;
- qual caminho recomendado leva até ela.

Ao abrir um exercício, outra aba apresenta apenas o ambiente necessário para
executá-lo: demonstração, material musical, playback, metrônomo, gravação,
feedback, histórico comparável e nova tentativa.

## Escolha guiada

A Home deve perguntar "O que você quer fazer hoje?" e oferecer intenções claras:

- aprender algo novo;
- evoluir uma habilidade atual;
- tocar músicas;
- revisar;
- fazer treino livre.

O coach usa a intenção como restrição, não como abandono da adaptação. Dentro da
categoria escolhida, o sistema recomenda opções compatíveis com estágio,
evidências, revisões pendentes, tempo disponível e preferências.

O usuário pode:

- aceitar a missão recomendada;
- adiar;
- trocar por outra equivalente;
- pedir uma opção mais fácil;
- pedir uma opção mais difícil quando houver evidência suficiente;
- escolher prática livre sem falsificar progresso curricular.

## Estados conceituais

Uma missão pode estar:

- indisponível;
- disponível;
- recomendada;
- planejada;
- em andamento;
- pausada;
- aguardando nova tentativa;
- aguardando revisão;
- concluída com evidência insuficiente;
- concluída com evidência válida.

Concluir todas as etapas visuais não significa necessariamente dominar a skill.

## Regras de qualidade

- Missões não podem ser apenas wrappers de exercícios genéricos.
- O objetivo deve descrever execução musical observável.
- Toda etapa deve ter função pedagógica explícita.
- Deve existir recuperação quando o aluno não consegue concluir.
- Feedback deve apontar uma próxima ação pequena e executável.
- A missão precisa produzir evidências ou declarar que foi apenas exploratória.
- Recompensas devem ser musicais: repertório, autonomia, novo contexto ou nova
  capacidade, não apenas XP.

## Jornada editorial

O catálogo mantém ao menos 25 Missions por instrumento suportado (guitarra,
violão, teclado e bateria), distribuídas pelos sete estágios e pelas nove trilhas
curriculares reais. Cada instrumento possui experiências próprias de percepção,
técnica, ritmo, leitura, harmonia, repertório, improvisação, criação e
performance; a cobertura não é obtida trocando apenas o nome do instrumento.

As definições extensas ficam separadas por instrumento e são compostas pelo
`TeachingContentCatalog`, sem criar outro motor pedagógico. Cada Mission
editorial declara resultado observável, função no desenvolvimento, condição de
prática, aplicação, critérios, variações e protocolo de Assessment. O catálogo
não cria domínio: tentativas de exercício e autoavaliações geram apenas
evidência provisória; observações admissíveis são interpretadas pelo Evidence
Engine.

As relações entre Mission, Lesson, Exercise, Assessment e repertório são
persistidas em `learning_content_relations`. O workspace de uma Mission recebe
o campo `repertoire` com as músicas editoriais vinculadas, para que a aplicação
não dependa de busca textual ou de uma associação implícita.

Competências editoriais usam janela de retenção de 30 dias. Quando evidências
que sustentavam a hipótese vencem, Curriculum Engine e Coach priorizam a
Mission de revisão correspondente, distinguindo `REVIEW` de `REVALIDATION`.

`EditorialCatalogIntegrityIntegrationTest` valida IDs, referências, instrumento,
pré-requisitos acíclicos, stages, tracks, repertório e conteúdo executável. Seu
relatório lista também skills ainda sem Mission e skills cobertas sem aplicação
de repertório; essas listas orientam expansão futura sem converter quantidade
arbitrária em regra pedagógica.
