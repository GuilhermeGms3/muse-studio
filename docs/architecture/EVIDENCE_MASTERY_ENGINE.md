# Motor de Domínio Baseado em Evidências

## Objetivo

O motor não deve decidir simplesmente se o usuário "sabe" ou "não sabe". Ele
deve estimar, de forma auditável, quão forte é a evidência de que uma competência
está disponível, consistente e retida em condições musicais relevantes.

Porcentagem, BPM e tempo podem fazer parte da evidência, mas nenhum deles deve
ser confundido isoladamente com domínio.

## Conceitos fundamentais

### Competência

Capacidade musical específica e observável, como trocar G e D mantendo o pulso,
afinar um bend de um tom ou reconhecer direção melódica.

### Critério

Condição que descreve uma manifestação da competência. Uma skill pode exigir
critérios de precisão, continuidade, tempo, aplicação, transferência e retenção.

### Evidência

Registro imutável de uma observação, contendo pelo menos:

- competência e critério relacionados;
- missão, exercício, música ou avaliação de origem;
- instrumento e perfil;
- data e duração real;
- condições, como BPM, tonalidade, trecho e acompanhamento;
- método de observação;
- resultado observado;
- qualidade do sinal;
- confiabilidade da medição;
- versão do analisador e do critério;
- artefato de origem quando permitido, como áudio ou take do Reaper.

### Confiabilidade da medição

Probabilidade de a observação estar correta. Não é a mesma coisa que domínio.
Uma captura ruidosa pode gerar uma medição pouco confiável mesmo quando o aluno
tocou corretamente.

### Confiança de domínio

Força acumulada da hipótese de que o usuário consegue reproduzir a competência
em condições relevantes e após algum tempo.

## Fontes de evidência

Fontes possíveis, em ordem não fixa de confiabilidade:

- autoavaliação;
- confirmação de conclusão;
- resposta objetiva em teste;
- medição por DSP/MIR;
- análise de áudio sob condições controladas;
- aplicação em exercício;
- aplicação em trecho musical;
- repetição em dias diferentes;
- revisão após intervalo;
- avaliação humana.

Os pesos não devem ser universais nem fixados por intuição. Precisam variar por
competência e ser calibrados com dados rotulados e testes reais.

Autoavaliação pode orientar dificuldade, fadiga e confiança pessoal. Sozinha,
não deve liberar gates de domínio que aleguem execução comprovada.

## Dimensões de domínio

Uma hipótese de domínio deve considerar mais de uma dimensão:

- precisão;
- estabilidade temporal;
- continuidade;
- velocidade ou tempo adequado;
- qualidade técnica mensurável;
- independência de ajuda visual;
- aplicação musical;
- transferência para outro contexto;
- consistência entre tentativas;
- repetição em dias diferentes;
- retenção após intervalo.

Nem toda skill usa todas as dimensões. Critérios devem ser específicos.

## Estados sugeridos

Estados devem comunicar evidência, não recompensa:

- **Não observado:** não existem dados suficientes.
- **Em exploração:** o sistema ainda identifica o ponto de partida.
- **Em desenvolvimento:** há evidências parciais ou inconsistentes.
- **Consistente em condição controlada:** execução repetida no exercício-alvo.
- **Aplicado musicalmente:** competência observada em repertório ou contexto real.
- **Retido:** competência reaparece após intervalo relevante.
- **Precisa de revisão:** evidência antiga, degradada ou contraditória.

Nomes finais dependem de pesquisa de linguagem com usuários.

## Atualização da hipótese

O motor deve combinar:

- força da evidência;
- confiabilidade da medição;
- independência entre observações;
- diversidade de contexto;
- recência;
- dificuldade da condição;
- resultados contraditórios;
- cobertura dos critérios obrigatórios.

Dez tentativas iguais no mesmo minuto não equivalem a evidências distribuídas
por dias, contextos e revisões. Evidências correlacionadas devem ter retorno
decrescente.

## Proteção contra falsos resultados

Antes de usar uma medição para progressão, o sistema deve validar:

- dispositivo e canal corretos;
- presença de sinal;
- relação sinal-ruído mínima;
- latência conhecida ou estimada;
- alinhamento entre referência e gravação;
- compatibilidade entre o analisador e o tipo de áudio;
- ausência de clipping;
- confiança mínima do algoritmo.

Quando a confiança for baixa, o resultado deve ser "não foi possível avaliar",
nunca "incorreto".

Outras proteções:

- nenhuma evidência isolada produz domínio permanente;
- gates críticos exigem repetição em momentos diferentes;
- resultados contraditórios reduzem certeza e podem solicitar reavaliação;
- o usuário pode contestar ou excluir uma captura inválida;
- decisões são recalculáveis com versões novas do motor;
- mudanças de critérios são versionadas e auditáveis.

## Calibração obrigatória

O motor só pode ser considerado assertivo depois de:

1. Definir uma competência estreita e observável.
2. Criar um protocolo de avaliação reproduzível.
3. Coletar execuções corretas, limítrofes e incorretas.
4. Obter rótulos humanos confiáveis.
5. Medir falso positivo, falso negativo e casos inconclusivos.
6. Calibrar limiares por instrumento, técnica e condição.
7. Testar dispositivos, ruídos, timbres e latências diferentes.
8. Comparar a decisão do motor com avaliação humana.
9. Permitir resultado inconclusivo.
10. Monitorar regressões a cada versão do analisador.

## Primeira implementação recomendada

Começar com competências tecnicamente mensuráveis e condições controladas:

- pulso e estabilidade rítmica com ataques claros;
- nota monofônica em relação a uma referência;
- duração e continuidade;
- sequência curta de notas conhecidas;
- tempo de troca em evento simples, após protocolo validado.

Não começar por avaliação ampla de musicalidade, acordes complexos com efeitos,
expressividade ou execução polifônica livre. Esses problemas exigem analisadores
e protocolos mais maduros.

## Saída pedagógica

O motor não deve produzir apenas uma pontuação. Deve retornar:

- o que foi observado;
- o que não pôde ser observado;
- confiança da medição;
- evidências que sustentam a hipótese atual;
- evidências ainda ausentes;
- próxima experiência recomendada;
- condição sugerida para nova tentativa ou revisão.

