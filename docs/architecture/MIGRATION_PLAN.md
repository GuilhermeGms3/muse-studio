# Migration Plan

Este é o plano oficial de migração arquitetural do Muse Studio. Ele deve ser executado em pequenas etapas independentes, reversíveis e revisáveis.

## Estratégia Geral

1. Congelar contratos atuais como baseline.
2. Criar modelos novos em paralelo.
3. Manter rotas e endpoints estáveis.
4. Migrar shell e navegação por camadas.
5. Migrar cards/panels como primitives reutilizáveis.
6. Migrar features uma por vez.
7. Separar API client por domínio sem quebrar exports.
8. Evoluir workspace store para slices.
9. Introduzir dock e inspector modular.
10. Migrar tokens visuais por compatibilidade, não troca brusca.

## O Que Migra Primeiro

Primeiro:

- Testes de caracterização.
- Navigation/context registry paralelo.
- Tab model compatível.
- Reexports/adapters.

Por quê:

- Baixo risco.
- Não muda comportamento.
- Cria base para sidebar, tabs, palette e inspector.

## O Que Migra Depois

Depois:

- Sidebar contextual.
- Command palette baseada em registry.
- Cards e panels canônicos.
- Rotas grandes para feature workspaces.

Por quê:

- Depende de modelo de navegação e módulos.
- Pode ser feito rota por rota.

## O Que Migra Por Último

Último:

- Divisão profunda de `workspace-store`.
- Dock completo.
- Tema claro/tokens globais.
- Remoção de wrappers antigos.
- Reorganização final de pastas.

Por quê:

- Alto impacto visual e estrutural.
- Precisa de cobertura e compatibilidade estabelecidas.

## Ordem Recomendada

```text
Fase 0: Baseline e testes
Fase A: Infraestrutura de arquitetura
Fase B: Navegação contextual
Fase C: Workspace shell modular
Fase D: Sistema de tabs
Fase E: Cards e panels
Fase F: Feature modules por contexto
Fase G: API client por domínio
Fase H: Inspector e dock
Fase I: Tokens visuais e tema
Fase J: Limpeza controlada
```

## Critérios Gerais De Aceite

Cada fase precisa:

- Ser pequena o suficiente para revisão.
- Preservar todas as rotas.
- Preservar todos os endpoints.
- Passar build.
- Passar lint quando aplicável.
- Passar testes backend quando backend for afetado.
- Ter rollback claro.
- Não remover feature.

## Estratégia De Testes

Frontend:

- `npm run build`
- `npm run lint`
- Smoke manual das rotas.
- Teste manual de tabs.
- Teste manual de command palette.
- Teste manual de sessão.

Backend:

- `npm run api:test`
- Smoke manual de endpoints usados pela fase.

Caracterização:

- Antes de refatorar uma área, documentar comportamento atual.
- Quando possível, criar teste que falha se comportamento mudar.

## Migração Gradual De Pastas

Regra:

- Não mover arquivo e alterar comportamento no mesmo PR.

Sequência segura:

1. Criar novo destino.
2. Copiar/mover com reexport ou alias.
3. Atualizar imports de poucos consumidores.
4. Validar.
5. Atualizar consumidores restantes.
6. Remover wrapper em fase de limpeza.

## Migração Gradual De Rotas

Regra:

- Rotas continuam sendo ponto de entrada.

Sequência:

1. Extrair componente de tela para `features/*`.
2. Rota passa a renderizar componente extraído.
3. Mover lógica local para hooks da feature.
4. Extrair cards/panels.
5. Validar rota.

## Migração Gradual De Store

Sequência:

1. Adicionar tipos ricos sem remover `OpenTab`.
2. Criar selectors.
3. Criar actions compatíveis.
4. Migrar TabBar.
5. Migrar WorkspaceShell.
6. Migrar CommandPalette.
7. Persistir versão nova.
8. Remover shape antigo apenas na limpeza.

## Migração Gradual De API

Sequência:

1. Criar `shared/api/client`.
2. Criar módulos por domínio.
3. Reexportar tudo por `music-api.ts`.
4. Migrar uma feature para importar domínio novo.
5. Validar query keys.
6. Manter `music-api.ts` como fachada até final.

