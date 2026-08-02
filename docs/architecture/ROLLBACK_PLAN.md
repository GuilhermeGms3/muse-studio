# Rollback Plan

Toda fase da migração precisa ser reversível. Rollback deve ser simples, rápido e não depender de reescrita.

## Princípios

- Uma fase por PR.
- Um objetivo por PR.
- Não misturar move + comportamento + visual.
- Manter wrappers até todos os consumidores migrarem.
- Manter endpoints e rotas.
- Preferir feature flags para mudanças visíveis.

## Rollback Por Tipo De Mudança

## Novos Arquivos Paralelos

Exemplos:

- Navigation registry.
- Tab model.
- API modules.
- Card primitives.

Rollback:

- Remover arquivos novos.
- Reverter imports que apontam para eles.

Risco:

- Baixo.

## Reexports / Wrappers

Rollback:

- Voltar import para caminho antigo.
- Manter wrapper antigo até limpeza.

Risco:

- Baixo.

## Sidebar Contextual

Rollback:

- Reativar renderização baseada em `navTree`.
- Manter registry novo inativo.

Validação:

- Todas as rotas acessíveis.
- Command palette funcionando.

## WorkspaceShell

Rollback:

- Reverter extração do subcomponente da fase.
- Evitar múltiplas extrações no mesmo PR.

Validação:

- Layout normal.
- Focus mode `/sessao`.
- Atalhos.

## Tabs

Rollback:

- Ignorar campos novos.
- Dedupe por `path`.
- Remover persistência nova se causar problema.

Validação:

- Abrir/fechar/navegar abas.

## API Client

Rollback:

- Reverter imports de feature para `src/lib/music-api.ts`.
- Manter módulos novos não usados até limpeza.

Validação:

- Build.
- Queries das rotas.

## Design Tokens

Rollback:

- Reverter somente bloco de tokens alterado.
- Não fazer alteração funcional junto.

Validação:

- Visual smoke de rotas principais.
- Contraste básico.

## Backend

Rollback:

- Reverter controller/service da fase.
- Não migrar DTOs sem teste.
- Preferir adicionar endpoint novo a modificar antigo.

Validação:

- `npm run api:test`.
- Smoke endpoint.

## Critérios Para Acionar Rollback

Acionar rollback se:

- Build falha sem correção simples.
- Rota existente deixa de abrir.
- Endpoint existente muda resposta.
- Sessão não inicia/finaliza.
- Abas deixam de abrir/fechar.
- Navegação perde feature.
- Dados do usuário podem ser perdidos.

## Estratégia Com Lovable

O projeto está conectado ao Lovable. Evitar:

- Force push.
- Rebase de histórico publicado.
- Amend/squash de commits já enviados.

Rollback deve ser feito por novo commit revertendo a mudança problemática quando já tiver sido publicado.

