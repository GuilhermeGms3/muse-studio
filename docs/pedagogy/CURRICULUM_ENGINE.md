# Curriculum Engine

## Escopo

Este documento define o modelo conceitual do currículo do Muse Studio. Não
define banco, endpoints, classes, algoritmos ou interface.

O Curriculum Engine representa o território de aprendizagem e as regras pelas
quais diferentes alunos podem percorrê-lo. O Coach usa esse território para
selecionar experiências; ele não inventa uma sequência desconectada do currículo.

## Curriculum

Curriculum é uma organização intencional de competências e experiências que
leva um perfil de aluno de um estado inicial a um conjunto de capacidades
musicais coerentes.

Um currículo contém:

- identidade e propósito;
- instrumento ou domínio de aplicação;
- público e estados iniciais previstos;
- resultados musicais esperados;
- módulos e unidades;
- competências;
- relações e pré-requisitos;
- caminhos essenciais e opcionais;
- pontos de diagnóstico, avaliação, aplicação e revisão;
- regras de adaptação a objetivos;
- critérios de conclusão e transição.

Currículo não é uma lista de conteúdo, calendário fixo nem sequência universal.

## Estrutura conceitual

```text
Curriculum
  -> Modules
      -> Units
          -> Competencies
              -> Learning Missions
                  -> Lessons
                  -> Exercises
                  -> Assessments
                  -> Evidence requirements
```

Essa hierarquia organiza apresentação e autoria. As dependências reais entre
competências formam um grafo descrito em [Knowledge Graph](./KNOWLEDGE_GRAPH.md).

## Module

Module agrupa resultados musicais amplos e relacionados. Deve representar uma
transformação compreensível, como sustentar acompanhamentos básicos, desenvolver
leitura rítmica inicial ou construir independência no teclado.

Um módulo:

- possui resultado de saída explícito;
- combina competências complementares;
- pode compartilhar competências com outros módulos;
- contém pontos de aplicação musical;
- não precisa ser percorrido integralmente antes de outro começar.

## Unit

Unit é um arco pedagógico menor dentro de um módulo. Organiza competências e
missões que fazem sentido em proximidade, culminando em uma aplicação ou
avaliação integradora.

Uma unidade deve responder:

- o que o aluno será capaz de fazer ao final;
- quais competências sustentam esse resultado;
- quais experiências introduzem, praticam e integram essas competências;
- quais evidências justificam avanço;
- como recuperar lacunas.

## Competency

Competency é uma capacidade musical observável em condições declaradas. Deve ser
específica o suficiente para avaliação e significativa o suficiente para ter
valor musical.

Toda competência precisa declarar:

- ação ou percepção esperada;
- contexto e condições;
- dimensões relevantes de qualidade;
- pré-requisitos essenciais e auxiliares;
- aplicações;
- política de evidências;
- condições de retenção e revalidação;
- relações com objetivos e instrumentos.

Termos amplos como "ritmo" ou "harmonia" são domínios, não competências finais.

## Learning Path

Learning Path é um percurso possível pelo currículo para um aluno em determinado
momento. É derivado do grafo, do perfil, dos objetivos e das evidências.

Um caminho pode conter:

- núcleo essencial;
- especialização por objetivo ou estilo;
- reforço para lacunas;
- atalhos justificados por diagnóstico;
- aplicações em repertório escolhido;
- ramificações opcionais;
- revisões e revalidações.

Caminhos mudam sem reescrever o currículo. Dois alunos podem chegar ao mesmo
resultado por repertórios, exemplos e missões diferentes.

## Tipos de pré-requisito

- **Estrito:** sem ele a nova tarefa é insegura, incompreensível ou inviável.
- **Pedagógico:** reduz carga e aumenta chance de sucesso, mas admite exceções.
- **Recomendado:** melhora a experiência sem bloquear acesso.
- **Contextual:** necessário apenas para uma variante, repertório ou objetivo.
- **Co-requisito:** pode ser desenvolvido em paralelo.

Pré-requisitos podem ser motores, perceptivos, conceituais, rítmicos, de leitura,
de repertório ou de autonomia. Uma dependência não deve existir apenas porque
um curso tradicional ensinou os conteúdos nessa ordem.

## Regras de desbloqueio

Desbloquear significa permitir prática guiada como próximo passo adequado. Não
significa declarar domínio completo dos pré-requisitos.

Uma competência pode ser desbloqueada quando:

- pré-requisitos estritos possuem evidência mínima válida;
- critérios de segurança e carga estão atendidos;
- existe missão compatível com a dificuldade relativa do aluno;
- a novidade serve ao caminho ou a uma exploração consciente.

Regras importantes:

- tempo ou conclusão visual não desbloqueiam sozinhos;
- lacuna em critério obrigatório não pode ser escondida por média;
- o aluno pode inspecionar nós bloqueados e entender o caminho;
- exploração pode ser permitida sem registrar domínio ou avanço;
- exceções diagnósticas devem ser explicáveis e reversíveis;
- desbloqueio e recomendação são decisões diferentes.

## Caminhos opcionais

Caminhos opcionais oferecem repertório, estilos, técnicas e formas de criação que
não são universais. Eles não devem comprometer fundações necessárias ao objetivo
do aluno nem ser tratados como inferiores.

O currículo deve distinguir:

- fundações amplamente transferíveis;
- requisitos do objetivo escolhido;
- especializações;
- exploração livre;
- conteúdo de enriquecimento.

## Currículos por instrumento

Cada instrumento possui demandas motoras, sonoras, espaciais e de repertório
próprias. Por isso, cada perfil percorre um currículo instrumental específico.

Conhecimentos compartilhados podem referenciar a mesma competência conceitual,
mas aplicação e evidência permanecem contextualizadas. Compreender subdivisão
pode transferir; coordenar mãos e pés na bateria não comprova coordenação no
violão.

## Currículos orientados por objetivo

Objetivos não criam currículos isolados do zero. Eles aplicam prioridades,
conteúdos, aplicações e requisitos adicionais sobre a fundação instrumental.
Ver [Learning Goals](./LEARNING_GOALS.md).

Exemplos:

- tocar na igreja prioriza acompanhamento, repertório, transposição, dinâmica e
  escuta em conjunto;
- fingerstyle prioriza independência, vozes, dinâmica e arranjo;
- metal prioriza precisão rítmica, muting, resistência e repertório apropriado;
- composição prioriza percepção, harmonia aplicada, forma e produção de ideias.

## Como diferentes perfis percorrem o currículo

O percurso considera:

- instrumento ativo;
- diagnóstico e evidências existentes;
- objetivos e horizonte;
- repertório desejado;
- tempo disponível;
- preferências de aprendizagem e acessibilidade;
- fadiga, histórico e revisões devidas;
- competências prontas, frágeis ou não observadas.

O sistema não deve confundir preferência com competência nem objetivo com nível.

## Navegação do Coach

O Coach seleciona uma próxima experiência em cinco passos conceituais:

1. Identificar a intenção do aluno e as obrigações pedagógicas atuais.
2. Localizar competências candidatas no caminho ativo.
3. Remover opções bloqueadas, inadequadas ou redundantes.
4. Comparar valor pedagógico, dificuldade relativa, revisão e motivação.
5. Recomendar opções explicando objetivo, motivo e alternativa.

O Coach pode recomendar:

- introdução;
- prática controlada;
- integração entre competências;
- aplicação em música;
- recuperação;
- revisão;
- transferência;
- avaliação;
- prática livre sem promessa curricular.

## Quando avançar e quando revisar

Avançar quando existe evidência suficiente para que a próxima experiência seja
produtiva, mesmo que o domínio final ainda exija aplicação e retenção.

Revisar quando:

- evidência necessária envelheceu;
- uma competência sustenta o próximo passo e está frágil;
- resultados recentes contradizem a hipótese atual;
- falta retenção ou transferência;
- revisão pode ser incorporada a uma música ou missão atual.

Avanço e revisão podem coexistir. O currículo não deve forçar o aluno a concluir
todo conteúdo anterior antes de experimentar música nova, nem permitir que
novidade substitua permanentemente consolidação.

## Invariantes

- Todo caminho leva a resultados musicais observáveis.
- Toda competência possui propósito e política de evidência.
- Toda dependência possui justificativa pedagógica.
- Todo bloqueio é explicável.
- Toda fundação aparece em aplicação musical tão cedo quanto possível.
- Objetivos personalizam sem falsificar pré-requisitos.
- O currículo admite múltiplos caminhos e retorno.
- Revisão faz parte do caminho, não é um módulo isolado no final.
- O currículo orienta o Coach; dados disponíveis não podem redefinir pedagogia.

