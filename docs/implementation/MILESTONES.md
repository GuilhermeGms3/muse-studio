# Milestones

## M1 - Contratos protegidos

**Stories:** IMP-001 a IMP-004.

**Resultado:** rotas, hooks, endpoints e matriz visual documentados.

**Aceite:** qualquer PR posterior sabe exatamente quais contratos validar.

## M2 - Arquitetura paralela criada

**Stories:** IMP-005 a IMP-009.

**Resultado:** macro contextos, registry, adapter, tabs metadata e features skeleton existem sem impacto visual.

**Aceite:** estruturas futuras podem ser consumidas gradualmente.

## M3 - Navegacao migrada com compatibilidade

**Stories:** IMP-010 a IMP-019.

**Resultado:** Sidebar e menus contextuais usam o modelo novo sem remover acessos.

**Aceite:** todas as rotas continuam acessiveis por pelo menos um caminho claro.

## M4 - Workspace e Tabs estabilizados

**Stories:** IMP-020 a IMP-027.

**Resultado:** shell modular, atalhos centralizados e tabs compativeis com modelo rico.

**Aceite:** workspace smoke passa em desktop/mobile e storage invalido nao quebra app.

## M5 - Superficies visuais canonicas

**Stories:** IMP-028 a IMP-032.

**Resultado:** cards e panels possuem primitives canonicos e primeiras rotas migradas.

**Aceite:** plano, repertorio, exercicios, skills e biblioteca preservam comportamento.

## M6 - Rotas delegadas para features

**Stories:** IMP-033 a IMP-040.

**Resultado:** rotas viram delegadoras finas para modulos `features/*`.

**Aceite:** URLs atuais e fluxos de detalhe/audio/sessao continuam funcionando.

## M7 - API frontend modular

**Stories:** IMP-041 a IMP-045.

**Resultado:** `music-api.ts` preserva facade sobre modulos por dominio.

**Aceite:** nenhum import externo precisa mudar.

## M8 - Ferramentas persistentes preparadas

**Stories:** IMP-046 a IMP-048.

**Resultado:** Inspector modular, Dock vazio seguro e metronomo adaptavel ao Dock.

**Aceite:** rota `/metronomo` segue funcional e Dock nao causa conflito.

## M9 - Fundacao visual futura

**Stories:** IMP-049 a IMP-052.

**Resultado:** tokens semanticos e preview de tema claro opt-in.

**Aceite:** tema atual permanece default e contraste essencial passa.

## M10 - Migracao encerrada

**Stories:** IMP-053 a IMP-055.

**Resultado:** legado sem uso removido, docs atualizadas e release candidate aprovada.

**Aceite:** migracao concluida sem feature removida.
