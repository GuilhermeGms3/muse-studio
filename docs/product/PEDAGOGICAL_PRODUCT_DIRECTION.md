# Direção Pedagógica do Produto

## Tese central

O Muse Studio não deve ser apenas um lugar para organizar estudos. Ele deve se
comportar como um professor que conhece a jornada musical do usuário.

O produto atual já modela instrumentos, habilidades, exercícios, repertório,
sessões e progresso. Essa base é útil para armazenar e relacionar conhecimento,
mas não basta para ensinar. O próximo salto do produto é conduzir o aprendizado:

```text
diagnosticar
  -> ensinar
  -> demonstrar
  -> praticar
  -> ouvir
  -> corrigir
  -> tentar novamente
  -> comprovar
  -> revisar
```

O principal critério para uma nova funcionalidade passa a ser:

> Isso ajuda o Muse Studio a ensinar melhor ou apenas adiciona mais uma tela?

## Mudança de paradigma

O modelo atual é centrado principalmente em objetos:

```text
Instrumento -> Skill -> Exercise -> Progress
```

O modelo pedagógico desejado é centrado na experiência do aluno:

```text
Quem é este aluno?
  -> O que ele consegue fazer?
  -> O que o impede de avançar?
  -> Qual é o próximo passo adequado?
  -> Como ensinar esse passo?
  -> Que evidência demonstraria aprendizado?
  -> Quando e como revisar?
```

Skills continuam sendo importantes, mas funcionam principalmente como estrutura
interna. A unidade percebida pelo usuário deve ser uma missão musical concreta.

Modelo conceitual desejado:

```text
Skill
  -> Learning Mission
      -> Lesson
      -> Exercise
      -> Assessment
      -> Evidence
  -> Mastery
```

Esse modelo é conceitual. Ele não determina ainda tabelas, endpoints ou classes.
Os contratos serão definidos apenas depois da validação da experiência.

## Promessa do produto

Ao abrir o Muse Studio, o usuário deve conseguir responder imediatamente:

- Qual é meu estágio atual neste instrumento?
- O que está impedindo minha evolução?
- O que vale a pena fazer hoje?
- Por que o sistema está recomendando isso?
- O que preciso demonstrar para avançar?
- Que evidências já sustentam meu domínio?

O Muse Studio não deve apenas dizer "pratique escalas maiores". Ele deve oferecer
uma experiência executável: referência, explicação, demonstração, material
musical, tentativa, escuta, feedback, nova tentativa e revisão futura.

## A sessão como coração do sistema

A sessão deixa de ser principalmente cronômetro e bloco de notas. Ela passa a
orquestrar a relação entre professor, material, execução e evidência:

```text
Professor contextualiza
  -> demonstra
  -> usuário escuta e observa
  -> usuário tenta
  -> sistema coleta sinais
  -> sistema oferece feedback compatível com sua confiança
  -> usuário tenta novamente
  -> avaliação registra evidências
  -> coach decide o próximo passo
```

Uma sessão pode conter missão, aula, exercício, trecho de música, revisão ou
prática livre. Ela precisa abrir os materiais adequados em abas completas, sem
reduzir objetos complexos a uma sidebar.

## Conteúdo profundo antes de catálogo amplo

Quantidade de registros não representa qualidade pedagógica. O produto deve
preferir poucas experiências completas, específicas e testadas a milhares de
variações produzidas por templates genéricos.

Cada conteúdo canônico deve responder:

- O que o aluno aprenderá?
- Por que isso importa musicalmente?
- O que deve observar ou escutar?
- Como executar?
- Quais são os erros comuns?
- Como reduzir a dificuldade?
- Como aumentar a dificuldade?
- Em qual música ou contexto aplicar?
- Qual avaliação produz evidência útil?
- Quando revisar?

## Perfis por instrumento

O usuário possui um perfil musical, com um perfil de aprendizado independente
para cada instrumento. Trocar o instrumento deve trocar todo o contexto do
workspace: estágio, missões, repertório, prioridades, histórico e evidências.

Conhecimentos realmente compartilháveis, como parte da teoria ou percepção
musical, podem contribuir para mais de um instrumento. Habilidades motoras e
evidências de execução não devem ser transferidas automaticamente.

## O que o Muse Studio não deve se tornar

- Um LMS genérico com vídeos e checkboxes.
- Um catálogo massivo de conteúdo repetido.
- Um sistema que chama tempo acumulado de domínio.
- Um jogo de XP, moedas e níveis sem significado musical.
- Uma DAW completa ou substituto do Reaper.
- Um avaliador que afirma certeza onde a captura não é confiável.

Elementos de missão e progressão podem tornar a jornada envolvente, mas devem
servir à prática musical real, não a uma gamificação superficial.

## Princípios de decisão

1. Condução antes de navegação.
2. Experiência musical antes de objeto administrativo.
3. Evidência antes de porcentagem decorativa.
4. Feedback honesto antes de aprovação automática.
5. Conteúdo profundo antes de escala de catálogo.
6. Uma trilha excelente antes de muitos instrumentos incompletos.
7. Controle do usuário sem devolver toda a decisão a ele.
8. Reaper e ferramentas externas como integrações, não como centro do produto.
9. Toda recomendação importante deve ser explicável.
10. Toda afirmação de domínio deve admitir incerteza e ser auditável.

## Resultado esperado

O Muse Studio deixa de ser um excelente organizador de prática e passa a ser um
treinador musical: identifica necessidades, propõe a próxima experiência,
acompanha a execução, reúne evidências ao longo do tempo e adapta a jornada.

