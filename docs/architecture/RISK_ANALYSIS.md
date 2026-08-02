# Risk Analysis

## Matriz De Risco

| Risco | Nível | Por quê | Como reduzir | Como validar |
| --- | --- | --- | --- | --- |
| Alterar `WorkspaceShell` cedo demais | Alto | Afeta todo o app, rotas, atalhos, layout e providers | Extrair partes com wrappers, manter shell antigo como host | Build, navegação manual, teste de atalhos, screenshots |
| Alterar `workspace-store` sem compatibilidade | Alto | Store é usada por shell, tabs, inspector, sessão e metronomo | Criar selectors/adapters antes de mudar shape | Testes unitários de store e fluxo de tabs |
| Quebrar contratos de `music-api.ts` | Alto | Todas as rotas consomem hooks e tipos centralizados | Fatiar por reexport mantendo API antiga | Typecheck, build, testes de rotas |
| Mudar endpoints backend | Alto | Frontend e testes dependem de `/api/v1` | Manter endpoints; criar v2 apenas se inevitável | Testes Spring existentes e smoke API |
| Migrar tema escuro para claro de uma vez | Alto | `styles.css` atual define tokens escuros usados por toda UI | Introduzir tokens semânticos compatíveis antes de trocar valores | Visual QA por tela |
| Nova navegação ocultar features | Alto | Documentos exigem nenhuma feature removida | Criar mapa de cobertura de rotas e menu | Checklist de todas as rotas no menu/palette |
| `/mapa` virar rota real sem dados | Médio | Hoje é redirect; pode criar experiência quebrada | Manter redirect até modelo de mapa existir | Teste de rota e fallback para Skill Tree |
| Dividir `CommandPalette` | Médio | Busca central depende de vários hooks | Extrair result model primeiro, render antigo depois | Buscar música, skill, exercício, aula e módulo |
| Dividir `CatalogEditor` | Médio | Edição/criação em vários domínios | Criar tests de save/delete por kind antes | Salvar e excluir lesson/exercise/song/project |
| Migrar cards por feature | Médio | Padrões duplicados em rotas podem divergir | Criar card primitives e trocar uma feature por vez | Visual + teste de link/action |
| Persistir abas | Médio | Pode restaurar paths inválidos ou estado antigo | Versionar storage key e validar paths | Teste com localStorage antigo/novo |
| Dock inicial competir com workspace | Médio | Ferramentas podem atrapalhar fluxo | Introduzir dock apenas como host vazio/feature flagged | Verificar sessão, música, exercício |
| Reorganizar pastas quebrar imports | Baixo/Médio | Path aliases reduzem risco, mas rotas importam caminhos atuais | Reexport barrels temporários | Typecheck e lint |
| Adicionar providers demais | Médio | Pode aumentar complexidade e rerenders | Preferir modelo puro/selectors antes | Profiler/manual performance |
| Mover backend `ApiModels` por domínio | Médio | Controllers e frontend dependem de DTOs | Não fazer na primeira fase | Testes backend |

## Riscos Baixos

- Criar documentação.
- Criar modelos TypeScript paralelos.
- Criar wrappers/reexports.
- Criar testes de caracterização.
- Adicionar metadata sem remover campos antigos.

## Riscos Médios

- Extrair cards.
- Dividir rotas grandes.
- Separar `music-api.ts` em módulos.
- Evoluir command palette.
- Adicionar persistência de tabs.
- Criar dock host.

## Riscos Altos

- Trocar shell.
- Alterar shape de workspace store.
- Alterar endpoints.
- Trocar design tokens globais.
- Alterar comportamento de `/sessao`.
- Remover rotas antigas.

## Estratégia Geral De Redução

- Cada fase deve ter PR pequeno.
- Manter wrappers de compatibilidade.
- Criar testes antes da mudança quando possível.
- Preservar URLs.
- Preservar exports antigos.
- Migrar uma rota por vez.
- Não mudar layout e dados na mesma fase.
- Não mudar frontend e backend ao mesmo tempo, exceto quando contrato for explicitamente versionado.

