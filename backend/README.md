# Music OS API

Backend local do Music OS, feito com Java 21, Spring Boot e H2 persistente em arquivo.

## Executar

Na raiz do projeto:

```powershell
npm run api:dev
```

A API fica disponível apenas na máquina local, em `http://127.0.0.1:8081/api/v1`.
O banco é criado em `backend/data/` e preservado entre execuções.

Para ativar sugestões do YouTube, crie uma chave nova, restrita à YouTube Data
API v3, e defina a variável apenas no terminal:

```powershell
$env:YOUTUBE_API_KEY="sua-chave-rotacionada"
npm run api:dev
```

Sem essa variável, o aplicativo oferece buscas do YouTube como fallback. A
chave nunca é enviada ao frontend nem persistida no banco.

Para rodar os testes:

```powershell
npm run api:test
```

## Contratos principais

| Método | Endpoint | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/home?instrument=guitar` | Resumo progressivo da Home |
| `GET` | `/api/v1/instruments` | Instrumentos |
| `GET` | `/api/v1/plans/today?instrument=guitar` | Plano de hoje |
| `POST` | `/api/v1/plans/today/regenerate?instrument=guitar` | Recalcular o plano adaptativo |
| `PATCH` | `/api/v1/plans/activities/{id}` | Concluir uma atividade |
| `GET` | `/api/v1/skills?instrument=guitar` | Nós da Skill Tree |
| `GET` | `/api/v1/skills/{id}` | Painel de uma habilidade |
| `PATCH` | `/api/v1/skills/{id}/state` | Alterar progresso da habilidade |
| `POST` | `/api/v1/skills/{id}/evidence` | Registrar prática, precisão, BPM e revisão |
| `PUT/DELETE` | `/api/v1/library/{id}` | Criar, editar ou remover uma aula |
| `PUT/DELETE` | `/api/v1/exercises/{id}` | Criar, editar ou remover um exercício |
| `POST` | `/api/v1/exercises/{id}/attempts` | Avaliar e registrar uma tentativa |
| `GET` | `/api/v1/ear-training/stats` | Consultar evolução auditiva |
| `POST` | `/api/v1/ear-training/attempts` | Registrar resposta auditiva e evidência perceptiva provisória rastreável |
| `GET/PUT` | `/api/v1/preferences` | Preferências, nível e tempo disponível |
| `GET` | `/api/v1/recommendations/songs` | Sugestões por gosto e habilidade |
| `GET` | `/api/v1/library` | Biblioteca |
| `GET` | `/api/v1/library/{id}` | Conteúdo da biblioteca |
| `GET` | `/api/v1/songs` | Repertório |
| `GET` | `/api/v1/exercises?instrument=guitar` | Exercícios |
| `GET` | `/api/v1/projects` | Projetos |
| `GET` | `/api/v1/journal` | Diário |
| `POST` | `/api/v1/sessions` | Iniciar sessão focada |
| `PATCH` | `/api/v1/sessions/{id}` | Salvar cronômetro, atividade e notas |
| `POST` | `/api/v1/sessions/{id}/finish` | Finalizar e registrar no diário |

## Exemplos

Iniciar uma sessão:

```http
POST /api/v1/sessions
Content-Type: application/json

{"instrument":"guitar"}
```

Atualizar uma habilidade:

```http
PATCH /api/v1/skills/bends/state
Content-Type: application/json

{"state":"practicing"}
```

Finalizar a sessão:

```http
POST /api/v1/sessions/{id}/finish
Content-Type: application/json

{
  "elapsedSeconds": 3600,
  "notes": "Bends mais afinados hoje.",
  "difficulties": "Ainda perco a afinação no final.",
  "improvements": "Consegui sustentar por dois tempos."
}
```

## Recursos adaptativos e dados locais

Novos contratos principais:

| Metodo | Endpoint | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/diagnostic` | Salvar diagnostico e posicionar a Skill Tree |
| `POST` | `/api/v1/sessions/{id}/activities/{activityId}/result` | Registrar dificuldade, precisao e BPM |
| `GET` | `/api/v1/sessions/{id}/summary` | Gerar a revisao pos-sessao |
| `POST` | `/api/v1/songs/{id}/practice-plan` | Transformar uma musica em plano de estudo |
| `POST/GET` | `/api/v1/recordings` | Salvar e listar gravacoes analisadas |
| `GET` | `/api/v1/missions/{id}?instrument=guitar` | Carregar experiência, atividades, evidências e repertório editorial vinculado |
| `POST` | `/api/v1/missions/{id}/experience` | Iniciar ou retomar uma experiência de Mission |
| `PATCH` | `/api/v1/missions/{id}/experience` | Persistir atividade semântica, pausa e gravação vinculada |
| `POST` | `/api/v1/missions/{id}/experience/complete` | Concluir após práticas, gravação e reflexão; preservar Assessment e Evidence |
| `GET` | `/api/v1/data/backup` | Exportar backup local em JSON |
| `POST` | `/api/v1/data/restore` | Restaurar um backup local |
| `GET` | `/api/v1/data/journal.csv` | Exportar o historico em CSV |
| `POST` | `/api/v1/data/imports` | Importar audio, MIDI, MusicXML ou Guitar Pro |
| `GET` | `/api/v1/data/status` | Consultar pasta local e estado dos dados |

Por padrao, gravacoes e importacoes ficam em `~/.music-os`. Para usar outra
pasta, defina `MUSIC_OS_DATA_DIR` antes de iniciar a API:

```powershell
$env:MUSIC_OS_DATA_DIR="D:\Music OS"
npm run api:dev
```

O funcionamento principal nao depende da internet. Sugestoes e reproducao do
YouTube sao integracoes opcionais.
