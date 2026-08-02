# Refactor Phases

## Fase 0: Baseline E Caracterização

Objetivo:

- Registrar comportamento atual antes de qualquer refactor.

Arquivos envolvidos:

- `src/routes/*`
- `src/components/workspace/*`
- `src/lib/music-api.ts`
- `src/lib/workspace-store.tsx`
- `backend/src/test/*`

Impacto:

- Nenhuma mudança funcional.

Dependências:

- Nenhuma.

Riscos:

- Baixo.

Estratégia:

- Criar checklist de smoke manual.
- Rodar build/lint/testes backend.
- Registrar rotas e endpoints como baseline.

Rollback:

- Não aplicável se for apenas testes/docs.

Critérios de conclusão:

- Baseline documentado.
- Comandos de validação conhecidos.

## Fase A: Infraestrutura De Arquitetura

Objetivo:

- Criar modelos e fachadas paralelas sem alterar UI.

Arquivos envolvidos:

- `src/lib/nav.ts`
- futuro `src/workspace/navigation/*`
- futuro `src/workspace/tabs/*`
- futuro `src/shared/api/*`

Impacto:

- Baixo; novos modelos não usados ou usados por adapter.

Dependências:

- Fase 0.

Riscos:

- Baixo.

Estratégia:

- Adicionar `navigationRegistry` paralelo ao `navTree`.
- Criar tipos `MacroContext`, `ContextMenuItem`, `WorkspaceTab`.
- Criar adapters para `navFlat` e `titleForPath`.

Rollback:

- Remover novos arquivos/modelos.

Critérios de conclusão:

- App compila.
- Navegação atual não muda.
- `navTree` antigo continua exportado.

## Fase B: Sistema De Navegação

Objetivo:

- Evoluir sidebar para macro contexto + menu contextual.

Arquivos envolvidos:

- `src/components/workspace/WorkspaceSidebar.tsx`
- `src/lib/nav.ts`
- futuros arquivos de navigation.
- `CommandPalette.tsx` apenas se usar registry compartilhado.

Impacto:

- Médio; navegação visível muda.

Dependências:

- Fase A.

Riscos:

- Features podem sumir do menu.

Estratégia:

- Primeiro renderizar menu novo em modo compatível.
- Garantir cobertura de todas as rotas.
- Manter command palette como acesso universal.
- Não remover paths antigos.

Rollback:

- Voltar `WorkspaceSidebar` para `navTree`.

Critérios de conclusão:

- Todos os macro contextos aparecem.
- Todas as rotas existentes são acessíveis.
- Busca global ainda encontra módulos.

## Fase C: Workspace

Objetivo:

- Dividir `WorkspaceShell` em subcomponentes sem mudar comportamento.

Arquivos envolvidos:

- `WorkspaceShell.tsx`
- futuros `TitleBar`, `WorkspaceBody`, `StatusBar`, `FocusShell`.

Impacto:

- Médio/alto por tocar shell global.

Dependências:

- Fase A.

Riscos:

- Quebrar layout ou atalhos.

Estratégia:

- Extrair um subcomponente por PR.
- Não mudar CSS/tokens na mesma fase.
- Manter props internas simples.

Rollback:

- Reverter extração do subcomponente afetado.

Critérios de conclusão:

- Layout normal e `/sessao` focus mode funcionam.
- Atalhos continuam funcionando.
- Sidebar/inspector/tabs preservados.

## Fase D: Tabs

Objetivo:

- Evoluir tabs de `path/title` para modelo rico compatível.

Arquivos envolvidos:

- `workspace-store.tsx`
- `TabBar.tsx`
- `WorkspaceShell.tsx`
- `nav.ts`

Impacto:

- Médio.

Dependências:

- Fase A.

Riscos:

- Abas duplicadas, fechamento incorreto, paths inválidos.

Estratégia:

- Adicionar campos opcionais.
- Manter `path` e `title`.
- Criar dedupe por `tab.id || path`.
- Só depois persistir tabs.

Rollback:

- Ignorar campos novos e voltar dedupe por path.

Critérios de conclusão:

- Abas abrem ao navegar.
- Fechar aba funciona.
- Reabrir rota ativa recria aba.

## Fase E: Cards E Panels

Objetivo:

- Criar primitives canônicas de card/panel e migrar padrões repetidos.

Arquivos envolvidos:

- `Panel.tsx`
- rotas de `plano`, `repertorio`, `exercicios`, `skills`, `projetos`, `diario`
- futuros `shared/ui/cards/*`

Impacto:

- Médio visual, baixo contratual.

Dependências:

- Fase C opcional.

Riscos:

- Regressão visual e ações perdidas.

Estratégia:

- Criar card primitives sem trocar tudo.
- Migrar uma rota por PR.
- Começar por rota simples: `plano` ou `diario`.

Rollback:

- Voltar rota para markup anterior.

Critérios de conclusão:

- Cards têm states básicos.
- Ações originais continuam.
- Layout não vira dashboard.

## Fase F: Contextos / Feature Modules

Objetivo:

- Reorganizar rotas por contexto sem mudar URLs.

Arquivos envolvidos:

- `src/routes/*`
- futuros `src/features/*`

Impacto:

- Médio.

Dependências:

- Fases B e E.

Riscos:

- Imports quebrados e lógica local perdida.

Estratégia:

- Extrair componente de tela para feature.
- Rota apenas importa e renderiza.
- Migrar uma feature por vez.

Ordem sugerida:

1. `metronomo`
2. `plano`
3. `diario`
4. `projetos`
5. `biblioteca`
6. `repertorio`
7. `exercicios`
8. `skills`
9. `sessao`
10. `dados`
11. `diagnostico`

Rollback:

- Rota volta a conter componente anterior.

Critérios de conclusão:

- URLs preservadas.
- Feature folder contém tela, hooks e módulos.

## Fase G: Módulos De API

Objetivo:

- Fatiar `music-api.ts` por domínio mantendo fachada.

Arquivos envolvidos:

- `src/lib/music-api.ts`
- futuros `src/shared/api/*`
- feature imports conforme migração.

Impacto:

- Médio/alto se feito sem cuidado.

Dependências:

- Fase F parcial.

Riscos:

- Query keys ou tipos mudarem.

Estratégia:

- Criar `client.ts` e `contracts.ts`.
- Criar módulos por domínio.
- Reexportar pelo arquivo antigo.
- Migrar imports de features já extraídas.

Rollback:

- Voltar imports para `music-api.ts`.

Critérios de conclusão:

- `music-api.ts` ainda funciona.
- Build sem alterações de contrato.

## Fase H: Ferramentas, Inspector E Dock

Objetivo:

- Modularizar Inspector e preparar Dock.

Arquivos envolvidos:

- `Inspector.tsx`
- `PracticeRecorder.tsx`
- `use-metronome.ts`
- futuros `workspace/inspector/*`, `workspace/dock/*`

Impacto:

- Médio.

Dependências:

- Fases C e D.

Riscos:

- Ferramentas competirem com conteúdo principal.

Estratégia:

- Dividir Inspector em seções.
- Criar DockHost vazio/feature flagged.
- Mover metronome/recorder para tools apenas depois.

Rollback:

- Desativar DockHost e usar Inspector antigo.

Critérios de conclusão:

- Inspector ainda mostra contexto.
- Dock não altera fluxo se desativado.

## Fase I: Design Tokens E Tema

Objetivo:

- Aproximar UI do design system documentado.

Arquivos envolvidos:

- `src/styles.css`
- primitives UI.
- workspace components.

Impacto:

- Alto visual.

Dependências:

- Fases E e C.

Riscos:

- Regressão visual ampla.

Estratégia:

- Criar tokens semânticos compatíveis com tema atual.
- Migrar componentes para tokens.
- Só depois alterar valores visuais.
- Fazer por contexto/tela.

Rollback:

- Reverter tokens da fase.

Critérios de conclusão:

- Contraste validado.
- Telas principais legíveis.
- Nenhuma mudança funcional.

## Fase J: Limpeza

Objetivo:

- Remover wrappers, imports antigos e código morto.

Arquivos envolvidos:

- Wrappers temporários.
- `components/workspace` antigos.
- `lib` reexports antigos, quando seguro.

Impacto:

- Médio.

Dependências:

- Todas as fases relevantes completas.

Riscos:

- Remover compatibilidade ainda usada.

Estratégia:

- Usar `rg` para confirmar imports.
- Remover um wrapper por PR.
- Rodar build/lint.

Rollback:

- Restaurar wrapper removido.

Critérios de conclusão:

- Sem imports antigos.
- Sem rotas quebradas.
- Arquitetura alvo documentada reflete código real.

