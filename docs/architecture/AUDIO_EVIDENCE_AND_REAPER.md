# Evidência de Áudio e Integração com Reaper

## Princípio

O Muse Studio deve orquestrar a prática e interpretar evidências. Ele não deve
tentar substituir uma DAW, construir um afinador universal ou resolver todo o
campo de análise musical em uma única implementação.

Whisper e reconhecimento de fala não são a tecnologia adequada para avaliar
guitarra, violão, teclado ou bateria. O domínio técnico relevante é Music
Information Retrieval (MIR) e processamento digital de sinais (DSP).

## O que pode ser observado

Dependendo do instrumento, material e qualidade do sinal:

- presença e início de ataques;
- BPM e intervalos entre ataques;
- desvio em relação à grade temporal;
- estabilidade rítmica;
- altura de notas monofônicas;
- desvio de afinação em cents;
- duração e sustentação;
- silêncios e interrupções;
- dinâmica aproximada;
- sequência de eventos em exercícios conhecidos.

Detecção confiável de acordes, voicings, articulações, expressividade e erros em
áudio polifônico é significativamente mais difícil. O sistema não deve prometer
essas capacidades antes de validação específica.

## Arquitetura conceitual

```text
Contrato da atividade
  -> fonte de áudio
  -> validação e calibração do sinal
  -> captura ou importação
  -> pré-processamento
  -> extração de características
  -> alinhamento com referência
  -> métricas com confiança
  -> Evidence Mastery Engine
  -> decisão pedagógica do Coach
```

O contrato da atividade precisa informar ao analisador o que esperar: notas,
ritmo, BPM, janela de tempo, tolerâncias e tipo de instrumento. Avaliar uma
execução conhecida é mais viável do que tentar transcrever música arbitrária.

## Estratégias de entrada

### 1. Captura direta pelo Muse Studio

Útil para microfone e dispositivos compatíveis com a captura do navegador.

Vantagens:

- fluxo simples;
- feedback durante a atividade;
- implementação incremental.

Limitações:

- seleção e disponibilidade de drivers;
- latência;
- processamento automático do navegador;
- possível conflito com ASIO e uso simultâneo pelo Reaper.

Não se deve assumir que a mesma interface ASIO poderá ser aberta por dois
aplicativos simultaneamente.

### 2. Pasta monitorada do Reaper

Primeiro candidato para integração local robusta. O Reaper grava ou renderiza um
take em uma pasta conhecida; o Muse Studio detecta o arquivo e associa ao
exercício ou à sessão ativos.

Vantagens:

- baixo acoplamento;
- não disputa o driver de áudio;
- fácil inspeção e reprocessamento;
- não exige VST inicial.

O protocolo precisa definir identificador da atividade, timestamps, formato,
sample rate, canais, BPM, latência conhecida e política de arquivos.

### 3. ReaScript, OSC ou Web Remote

Pode automatizar criação de projeto, início e fim de gravação, markers, tempo e
associação de takes. Deve evoluir sobre o contrato `ReaperBridge` já existente.

### 4. Plugin VST

Pode fornecer áudio ou características em tempo real, mas aumenta muito a
complexidade de build, distribuição, compatibilidade, segurança e suporte.
Somente deve ser adotado depois que o protocolo pedagógico e os analisadores
forem validados por uma integração mais simples.

## Estratégia incremental recomendada

### Fase A — Exercício controlado no navegador

- ataques rítmicos simples;
- notas monofônicas;
- gravação preservada como evidência;
- resultado inconclusivo quando o sinal não for adequado.

### Fase B — Importação e pasta monitorada

- Reaper renderiza ou salva take;
- Muse Studio importa automaticamente;
- usuário confirma a associação;
- análise ocorre fora do caminho crítico da gravação.

### Fase C — Controle do Reaper

- projeto ou template de prática;
- markers e região do exercício;
- BPM e compasso sincronizados;
- start/stop e retorno do take via bridge local.

### Fase D — Tempo real avançado

- avaliar necessidade real de extensão ou VST;
- prototipar apenas métricas que melhorem a decisão pedagógica;
- manter fallback por arquivo.

## Calibração de dispositivo

Cada perfil de entrada pode precisar armazenar:

- dispositivo e canal;
- instrumento;
- ganho esperado;
- ruído de fundo;
- latência de ida e volta;
- sample rate;
- presença de efeitos;
- teste de sinal mais recente.

Uma missão avaliativa deve confirmar que a configuração permanece válida antes
de produzir evidência de alta confiança.

## Segurança e privacidade

- Áudio é dado pessoal e deve permanecer local por padrão.
- O usuário controla retenção e exclusão de takes.
- Nenhum upload para serviço externo deve ocorrer sem ação e consentimento
  explícitos.
- Evidências derivadas devem manter referência à origem e à versão do analisador.
- A exclusão do áudio precisa declarar se métricas derivadas serão mantidas.

## Critério de sucesso

A integração é valiosa quando permite ao Muse Studio decidir melhor a próxima
experiência de aprendizado. Detectar uma nota ou exibir uma forma de onda, sem
consequência pedagógica confiável, não justifica a complexidade.

