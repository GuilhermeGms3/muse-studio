# Evidence Framework

## Autoridade e propósito

Este documento formaliza como o Muse Studio interpreta evidências de
aprendizagem. Ele é normativo para diagnósticos, missões, exercícios, avaliações,
Coach, desbloqueios e afirmações de domínio.

O objetivo não é decidir definitivamente se o aluno sabe. O objetivo é estimar
quão justificável é acreditar que uma competência foi adquirida, em quais
condições e com quais incertezas.

Este framework complementa o
[Motor de Domínio Baseado em Evidências](../architecture/EVIDENCE_MASTERY_ENGINE.md),
que descreve responsabilidades arquiteturais e segurança de medição, e o
[Assessment Framework](./ASSESSMENT_FRAMEWORK.md), que define como observações
são provocadas.

## Princípios invioláveis

1. Evidência é observação contextualizada, não pontuação isolada.
2. Ausência de evidência não é evidência de ausência.
3. Falha de medição não é falha musical.
4. Uma observação não comprova domínio permanente.
5. Tempo, clique e conclusão visual não comprovam execução.
6. Autoavaliação informa experiência; não prova sozinha capacidade observável.
7. Critério obrigatório ausente não pode ser compensado por média.
8. Evidências correlacionadas possuem retorno decrescente.
9. Evidência recente não apaga automaticamente histórico consistente.
10. Toda conclusão deve ser explicável e recalculável.
11. Incerteza deve ser representada, não escondida em porcentagem.
12. Quanto maior a consequência pedagógica, maior a exigência de cobertura,
    independência e confiabilidade.

## Evidence Policy da competência

Cada competência possui uma política própria que declara:

- o que precisa ser observado;
- em quais condições;
- quais critérios são obrigatórios;
- quais fontes são aceitas;
- que grau de independência e diversidade é necessário;
- quais evidências apenas apoiam e quais podem sustentar conclusão;
- quando evidências envelhecem;
- como revalidar;
- quais resultados contraditórios exigem nova observação;
- quais conclusões o sistema pode comunicar.

Não existe peso universal para "áudio", "BPM", "música" ou "professor". A
relevância depende da competência. BPM pode ser central para uma execução
rítmica e irrelevante para reconhecer função harmônica sem limite de tempo.

## Unidade de evidência

Uma evidência deve preservar:

- identidade do aluno e perfil instrumental;
- competência e critério;
- atividade e propósito;
- instante e duração real;
- condição de execução;
- material, trecho ou estímulo;
- apoios disponíveis;
- tentativa e relação com tentativas próximas;
- fonte de observação;
- dado bruto ou referência auditável, quando permitido;
- resultado observado;
- qualidade da captura;
- confiabilidade da medição;
- versão do protocolo e analisador;
- interpretação pedagógica;
- estado de validade.

Sem contexto suficiente, um dado pode permanecer histórico, mas não deve entrar
em conclusão de domínio.

## Tipos de evidência

### Evidência direta de execução

Observa a competência sendo realizada sob condições conhecidas. Exemplos:
sequência de notas, troca de acordes, padrão rítmico ou leitura.

### Evidência direta de percepção ou resposta

Observa decisão, reconhecimento, reprodução ou explicação relacionada à
competência. Acerto casual e formato da pergunta precisam ser considerados.

### Evidência de aplicação

Observa a competência dentro de música, improvisação, acompanhamento ou criação.
Possui alto valor ecológico, mas pode dificultar isolamento da causa.

### Evidência de transferência

Observa uso em contexto não idêntico ao treinado: nova tonalidade, posição,
música, representação ou condição.

### Evidência de retenção

Observa recuperação após intervalo relevante, preferencialmente antes de nova
instrução completa.

### Evidência de processo

Registra estratégia, capacidade de usar feedback, autonomia, tensão percebida ou
qualidade de auto-observação. Ajuda o Coach, mas não substitui o resultado quando
a competência exige performance.

### Evidência declarativa

Inclui autoavaliação, relato e confiança percebida. É valiosa para intenção,
motivação, esforço e percepção de dificuldade. Sua influência sobre domínio é
limitada pela política da competência.

### Evidência contextual

Tempo, frequência, condições, dispositivo e histórico. Explica observações e
apoia planejamento, mas raramente sustenta domínio sozinha.

## Fontes de evidência

Fontes possíveis:

- resposta registrada pela interface;
- métrica determinística validada;
- analisador DSP/MIR;
- comparação com referência;
- take ou gravação;
- observação humana estruturada;
- aplicação em repertório;
- artefato criado;
- autorrelato;
- comportamento de prática.

A origem não determina automaticamente confiabilidade. Um professor sem
protocolo pode produzir observação ambígua; um analisador calibrado pode ser
muito confiável para afinação monofônica e inadequado para acordes com distorção.

## Estado da evidência

- **Provisória:** recebida, ainda sem validação suficiente.
- **Válida:** atende protocolo e pode contribuir para a política.
- **Limitada:** utilizável apenas para parte dos critérios ou sob ressalvas.
- **Contestada:** usuário, sistema ou nova análise identificou possível problema.
- **Contraditória:** diverge de evidências relevantes e pede interpretação.
- **Inválida:** captura, protocolo ou associação inadequados.
- **Envelhecida:** perdeu força para inferir estado atual.
- **Substituída:** reprocessamento mais confiável da mesma observação.

Invalidar não significa apagar. Auditoria e privacidade determinam retenção.

## Confiabilidade

Confiabilidade responde: "quanto podemos confiar que esta observação representa
o que afirma observar?"

Ela considera:

- validade do protocolo;
- qualidade do sinal;
- adequação do analisador;
- alinhamento e latência;
- clareza do critério;
- possibilidade de acaso;
- consistência do avaliador;
- interferências externas;
- apoios usados;
- completude dos dados.

Confiabilidade da evidência não é confiança de domínio. Uma única evidência pode
ser medida com alta confiabilidade e ainda ser insuficiente para concluir que a
competência é consistente e retida.

## Relevância e força

### Relevância

Quanto a observação corresponde ao critério e às condições da competência.

### Força

Quanto a observação reduz incerteza. Depende de relevância, confiabilidade,
dificuldade adequada, independência e novidade informacional.

Uma execução correta em música pode ser forte para aplicação e fraca para
localizar qual movimento técnico produziu o resultado. A mesma evidência pode
contribuir de formas diferentes para critérios diferentes.

## Ponderação

O sistema não deve manter uma tabela global de pesos arbitrários. Cada Evidence
Policy classifica fontes e condições por função:

- **Obrigatória:** cobre critério sem o qual a conclusão não existe.
- **Primária:** pode sustentar diretamente um critério.
- **Corroboradora:** aumenta confiança, mas não sustenta sozinha.
- **Contextual:** orienta interpretação ou recomendação.
- **Não admissível:** não mede o critério para esse uso.

Conceitualmente, a contribuição de uma evidência depende de:

```text
contribuição = relevância ao critério
             x confiabilidade da observação
             x adequação do desafio
             x independência
             x recência
             x valor de diversidade/transferência
```

Essa expressão não obriga multiplicação numérica nem exposição de percentual.
Ela define fatores que nenhuma implementação pode ignorar.

Regras de ponderação:

- evidência não admissível contribui zero para a conclusão;
- critério obrigatório exige cobertura própria;
- várias observações iguais possuem retorno decrescente;
- evidência de retenção não é substituída por volume no mesmo dia;
- aplicação não substitui automaticamente precisão isolada, nem o inverso;
- maior dificuldade só aumenta força se a competência-alvo continuar observável;
- ajuda excessiva pode limitar conclusão de autonomia sem invalidar aprendizado.

## Independência e diversidade

Evidências são menos independentes quando ocorrem:

- na mesma sessão;
- após repetição imediata do mesmo material;
- com a mesma referência e apoio;
- a partir do mesmo arquivo reprocessado;
- sob o mesmo erro sistemático de medição.

Diversidade relevante pode vir de:

- dias diferentes;
- repertório diferente;
- mudança controlada de tonalidade, posição ou padrão;
- execução isolada e aplicação;
- tentativa com e sem apoio;
- avaliação por fonte independente;
- retenção após intervalo.

Diversidade não deve ser adicionada apenas para dificultar. Precisa testar
generalização pertinente à competência.

## Evidências conflitantes

Conflito não é resolvido por média automática. O sistema deve investigar:

1. As evidências observam o mesmo critério?
2. As condições e dificuldades são comparáveis?
3. Alguma medição possui baixa confiabilidade?
4. O resultado pode refletir fadiga, ajuda, repertório ou latência?
5. Houve mudança real ao longo do tempo?
6. É necessário protocolo mais isolado ou nova observação?

Possíveis resultados:

- manter hipótese e marcar condição específica como frágil;
- reduzir confiança em um critério;
- invalidar captura;
- solicitar avaliação focal;
- programar revisão;
- reconhecer variabilidade sem concluir perda de domínio.

Uma falha recente não apaga imediatamente várias evidências fortes. Várias
falhas confiáveis em condições previamente dominadas justificam reavaliação.

## Evidência negativa

Evidência negativa é observação confiável de que um critério não foi atendido
naquela condição. Ela não significa incapacidade permanente.

É diferente de:

- ausência de tentativa;
- dado ausente;
- sinal inválido;
- atividade abandonada;
- recusa de recomendação;
- desempenho não observável pelo método usado.

Somente evidência negativa válida pode reduzir uma hipótese, e sua influência
depende da condição, recência e repetição.

## Evidências insuficientes

A conclusão permanece insuficiente quando existe:

- pouca cobertura de critérios;
- apenas fonte declarativa ou contextual;
- baixa confiabilidade;
- falta de independência;
- ausência de aplicação quando obrigatória;
- ausência de retenção quando alegada;
- contradição não resolvida;
- condição muito facilitada para a conclusão desejada.

O produto deve dizer o que já observou e qual evidência ainda precisa, em vez de
mostrar zero ou reprovação genérica.

## Decaimento temporal

Evidência não desaparece com o tempo; sua força para inferir capacidade atual
pode diminuir. O decaimento depende de:

- natureza da competência;
- estágio de consolidação;
- histórico de retenção;
- uso indireto em outras atividades;
- intervalo desde observação válida;
- qualidade e diversidade das evidências anteriores;
- sinais recentes compatíveis ou contraditórios.

Conhecimento conceitual, precisão motora e repertório podem envelhecer em ritmos
diferentes. Não existe meia-vida universal.

O sistema deve distinguir:

- evidência histórica: o aluno já demonstrou;
- confiança atual: provável disponibilidade agora;
- revisão devida: vale observar novamente;
- perda observada: nova evidência indica degradação.

## Revalidação

Revalidar é obter nova observação adequada ao risco de incerteza. Pode ocorrer
por revisão curta, aplicação em missão atual, performance ou avaliação focal.

Priorizar revalidação quando:

- a competência é pré-requisito imediato;
- a última evidência envelheceu;
- existe contradição;
- a consequência do avanço é alta;
- a competência não aparece em uso recente;
- o contexto futuro é mais exigente.

Revalidação deve evitar repetir toda a trilha quando uma observação curta e
válida é suficiente.

## Evolução da hipótese de domínio

Estados conceituais:

### Não observada

Não há base suficiente para inferência.

### Hipótese inicial

Existem sinais preliminares, diagnóstico ou evidência limitada.

### Em desenvolvimento

Há evidências diretas, porém critérios, consistência ou autonomia ainda são
parciais.

### Consistente em condição controlada

Critérios centrais aparecem repetidamente em prática ou avaliação conhecida.

### Domínio provável em aplicação

Critérios obrigatórios possuem evidência forte, independente e relevante,
incluindo aplicação quando exigida.

### Retido

A competência foi reobservada depois de intervalo relevante.

### Revalidação necessária

Evidências envelheceram, entraram em conflito ou não cobrem a próxima condição.

O estado não é uma escada irreversível. Ele pode ser específico por condição e
mudar com novas evidências.

## Regra para domínio provável

Uma competência só pode chegar a domínio provável quando:

- todos os critérios obrigatórios estão cobertos;
- existe quantidade mínima de evidências primárias definida pela política;
- observações possuem independência suficiente;
- confiabilidade atende à consequência da decisão;
- dificuldade corresponde às condições declaradas;
- contradições relevantes foram resolvidas ou explicitadas;
- existe aplicação ou transferência quando a competência as exige;
- o sistema consegue explicar a conclusão sem depender de média opaca.

Retenção é conclusão adicional. Não deve ser presumida no mesmo dia da aquisição.

## Comunicação sem porcentagem enganosa

O produto pode usar indicadores visuais, mas deve comunicar primeiro:

- estado da hipótese;
- condições em que a competência foi observada;
- evidências fortes;
- critérios ainda ausentes;
- recência e necessidade de revisão;
- incertezas e limitações.

Exemplo:

```text
Troca G–D: consistente em exercício controlado

Sustentado por:
- três execuções válidas em dois dias;
- pulso mantido a 60 BPM;
- troca observada sem apoio visual na última tentativa.

Ainda falta:
- aplicar em música sem interromper;
- revisar após sete dias.
```

Se uma porcentagem interna existir, ela nunca deve substituir essa explicação nem
ser apresentada como probabilidade científica sem calibração correspondente.

## Exemplo de política: troca G–D

Exemplo conceitual, não limiar universal:

- critério obrigatório: formar ambos os acordes com som aceitável;
- critério obrigatório: realizar troca mantendo pulso em condição declarada;
- critério primário: sequência repetida sob protocolo controlado;
- critério de aplicação: usar a troca em trecho musical;
- retenção: reobservar após intervalo;
- corroboradora: autoavaliação de tensão e esforço;
- contextual: minutos praticados;
- não admissível para domínio: clicar que concluiu a aula.

Uma gravação com ataques claros pode apoiar tempo de troca. Se o analisador não
consegue verificar notas do acorde com confiabilidade, não pode afirmar qualidade
harmônica; esse critério permanece dependente de outra fonte.

## Auditoria e contestação

Toda conclusão precisa permitir inspeção de:

- políticas e versões usadas;
- evidências incluídas e excluídas;
- razões de validade e peso funcional;
- conflitos;
- decaimento e revalidação;
- decisão pedagógica resultante.

O aluno pode marcar captura inválida, adicionar contexto e solicitar nova
tentativa. Correção não deve apagar silenciosamente histórico nem preservar uma
conclusão que perdeu suporte.

## Calibração

Pesos funcionais, limiares e regras de confiança precisam ser calibrados por
competência e uso. O processo inclui:

- protocolo reproduzível;
- amostras variadas;
- avaliação humana de referência;
- análise de falsos positivos, falsos negativos e inconclusivos;
- diversidade de instrumentos, dispositivos, timbres e perfis;
- comparação longitudinal;
- revisão pedagógica;
- versionamento e teste de regressão.

Até existir calibração suficiente, o sistema deve usar linguagem conservadora e
tratar resultados como apoio formativo, não certificação.

## Saída para o Coach

O framework entrega ao Coach:

- hipótese atual por competência e condição;
- cobertura de critérios;
- evidências recentes e históricas;
- confiabilidade e conflitos;
- necessidade de introdução, prática, aplicação, transferência ou revisão;
- tipo de nova observação com maior valor informacional;
- limitações que impedem conclusão.

O valor do motor não é acumular dados. É escolher a próxima experiência que mais
provavelmente aumenta a capacidade musical e reduz uma incerteza relevante.

