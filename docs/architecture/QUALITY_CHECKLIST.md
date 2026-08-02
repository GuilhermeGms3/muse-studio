# Quality Checklist

Checklist obrigatório para cada fase de migração.

## Antes De Iniciar

- Ler `docs/product`.
- Ler `docs/ux`.
- Ler `docs/ui`.
- Confirmar fase atual em `REFACTOR_PHASES.md`.
- Confirmar arquivos envolvidos.
- Confirmar rollback.
- Verificar `git status`.
- Identificar mudanças não relacionadas no worktree.
- Definir critério de aceite.
- Definir testes a rodar.

## Durante

- Manter mudança pequena.
- Não alterar comportamento fora do escopo.
- Não renomear rotas.
- Não alterar endpoints.
- Não mover e refatorar profundamente no mesmo passo.
- Preservar exports antigos.
- Usar adapters quando necessário.
- Atualizar documentação se arquitetura real mudar.
- Validar cada import após mover arquivos.

## Depois

- Rodar `npm run build`.
- Rodar `npm run lint` quando a fase tocar frontend.
- Rodar `npm run api:test` quando tocar backend.
- Fazer smoke manual das rotas afetadas.
- Testar busca global se navegação/API mudou.
- Testar abas se workspace/tabs mudou.
- Testar sessão se `workspace-store`, `/sessao` ou API de sessão mudou.
- Testar import/backup se `dados` ou API de data mudou.

## Smoke Manual Mínimo

Rotas:

- `/`
- `/sessao`
- `/plano`
- `/biblioteca`
- `/skills`
- `/repertorio`
- `/exercicios`
- `/ouvido`
- `/metronomo`
- `/projetos`
- `/diario`
- `/dados`

Interações:

- Trocar instrumento.
- Abrir busca global.
- Abrir música pela busca.
- Abrir skill tree.
- Abrir e fechar abas.
- Alternar sidebar.
- Alternar inspector.
- Alternar metrônomo.

## Critérios De Aceite Por Fase

Aceitar somente se:

- Todas as rotas antigas ainda existem.
- Nenhuma feature desapareceu.
- Build passa.
- Mudança é revisável.
- Rollback é claro.
- Documentação permanece coerente.

## Checklist De Contratos API

Para fases que tocam API:

- Request fields preservados.
- Response fields preservados.
- Status HTTP preservado.
- Erros continuam recuperáveis.
- Query keys frontend preservadas ou adaptadas.
- Testes backend passam.

## Checklist Visual

Para fases que tocam UI:

- Workspace continua protagonista.
- Sidebar não expõe tudo ao mesmo tempo.
- Abas continuam visíveis.
- Cards não viram blocos decorativos.
- Ferramentas não competem com conteúdo.
- Estados loading/empty/error continuam legíveis.
- Foco de teclado visível.

## Checklist De Segurança De Dados

Especialmente para:

- Sessões.
- Diário.
- Backup/restore.
- Importação.
- Gravações.
- Preferências.

Validar:

- Dados digitados não somem em erro.
- Ações destrutivas têm confirmação.
- Restore não roda sem decisão explícita.
- Upload/import tem feedback.

