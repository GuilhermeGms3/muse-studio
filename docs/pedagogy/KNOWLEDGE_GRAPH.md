# Knowledge Graph

## Objetivo

O Knowledge Graph representa relações entre competências musicais. Ele não é
uma árvore simples nem uma visualização decorativa. É o modelo conceitual que
permite justificar pré-requisitos, diagnosticar lacunas, construir caminhos,
programar revisões e explicar recomendações.

## Por que um grafo

Conhecimento musical possui convergências, dependências parciais e competências
compartilhadas. Pulso sustenta levadas, leitura, improvisação e performance.
Uma música pode integrar ritmo, técnica, ouvido e memória. Uma árvore obrigaria
cada competência a ter um único pai e produziria sequências artificiais.

No grafo:

- uma competência pode depender de várias outras;
- uma competência pode sustentar vários caminhos;
- aplicações conectam domínios diferentes;
- competências conceituais podem ser compartilhadas entre instrumentos;
- evidências permanecem contextualizadas ao que foi observado.

## Nós

O nó principal é a Competency definida no
[Curriculum Engine](./CURRICULUM_ENGINE.md). Outros objetos podem se relacionar
ao grafo sem se tornarem competências:

- domínio;
- módulo e unidade;
- missão;
- aula;
- exercício;
- avaliação;
- música e seção;
- objetivo;
- evidência.

Uma competência precisa ser observável. "Harmonia" é domínio; "reconhecer a
função dominante em progressões simples" pode ser competência.

## Tipos de aresta

- **Requer:** pré-requisito estrito.
- **Prepara:** facilita aprendizado futuro sem bloquear.
- **Co-desenvolve:** competências que evoluem produtivamente juntas.
- **Aplica:** objeto musical usa a competência.
- **Transfere para:** domínio em um contexto contribui para outro, sem equivaler.
- **Contrasta com:** comparação ajuda percepção e compreensão.
- **Compõe:** competência ampla depende de subcompetências coordenadas.
- **Alternativa de caminho:** resultados semelhantes por abordagens diferentes.
- **Evidencia:** avaliação ou atividade pode produzir observação relevante.
- **Serve ao objetivo:** relação de prioridade com um Learning Goal.

Cada aresta deve possuir justificativa pedagógica, força e condições. Relações
não devem ser criadas apenas por semelhança de palavras.

## Dependência e desbloqueio

O desbloqueio considera apenas relações relevantes à variante ou missão. Uma
aresta `Requer` pode bloquear prática guiada; `Prepara` influencia recomendação;
`Co-desenvolve` favorece missão integrada.

O grafo não decide sozinho. Ele informa o Curriculum Engine, que combina
dependências com evidências, dificuldade, objetivo e contexto.

Nós bloqueados permanecem visíveis e explicáveis. O usuário deve conseguir ver:

- o que a competência permite fazer;
- por que ainda não é recomendada;
- quais relações criam o bloqueio;
- quais caminhos podem satisfazê-lo;
- quais evidências já existem.

## Diagnóstico

O diagnóstico não precisa testar todos os nós. Ele seleciona competências
informativas cuja observação reduz incerteza sobre uma região do grafo.

Uma resposta pode:

- sustentar hipótese sobre um nó;
- sugerir observação de dependências;
- abrir caminho para avaliação mais específica;
- revelar diferença entre conhecimento e execução;
- permanecer inconclusiva sem classificar toda a trilha.

O diagnóstico deve evitar propagar domínio automaticamente por arestas. Saber
uma competência avançada pode justificar testar pré-requisitos, não marcá-los
como comprovados sem observação compatível.

## Recomendações do Coach

O Coach usa o grafo para encontrar:

- próximo nó de alto valor no caminho ativo;
- pré-requisito que bloqueia múltiplos objetivos;
- competência pronta para aplicação;
- alternativa que trabalha a mesma lacuna em contexto preferido;
- oportunidade de integrar competências já observadas;
- nó central cuja revisão reduz risco de perda em várias áreas.

Toda recomendação deve poder expor o caminho causal em linguagem simples.

## Revisão e retenção

Evidência envelhecida em um nó não invalida automaticamente descendentes. Pode,
porém, aumentar prioridade de uma revisão contextual se o nó for fundação
relevante e resultados recentes indicarem fragilidade.

Revisões podem ser selecionadas por:

- centralidade pedagógica;
- proximidade de uso em missão atual;
- evidência antiga ou conflitante;
- oportunidade de observar várias competências em uma aplicação;
- custo de recuperação caso a fundação tenha degradado.

## Relações entre instrumentos

O grafo pode conter competências compartilhadas, como percepção de pulso, e
competências instrumentais específicas. Transferência precisa ser declarada por
aresta e confirmada no novo contexto.

Conhecimento de intervalo pode reduzir necessidade de explicação no teclado e
no violão. Isso não comprova mapa do instrumento, coordenação ou execução.

## Integridade conceitual

- Evitar ciclos de pré-requisito estrito.
- Permitir ciclos de co-desenvolvimento e revisão quando semanticamente claros.
- Não usar nível global como substituto da região do grafo.
- Não somar todos os nós em uma porcentagem única de músico.
- Versionar mudanças conceituais sem apagar evidências históricas.
- Diferenciar ausência de relação de relação ainda não modelada.
- Manter o grafo navegável por objetivo, instrumento, domínio e aplicação.

## Visualização

Skill Tree pode ser uma projeção simplificada do grafo para um caminho. Mapa do
Conhecimento pode expor relações mais amplas. Nenhuma visualização deve alterar
o significado pedagógico das relações para caber em uma árvore.

