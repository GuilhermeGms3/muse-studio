# Workspace Layouts

Muse Studio usa uma estrutura constante para reduzir carga cognitiva.

## Layout Base

```text
+--------------------------------------------------------------------------------+
| Product | Instrumentos | Buscar em tudo                         | BPM | Toggles |
+--------------------------------------------------------------------------------+
| Contexto/Menu       | Tabs                                                       |
|                     +------------------------------------------------------------+
|                     | Workspace ativo                                            |
|                     |                                                            |
|                     | Cards, paineis e modulos                                   |
|                     |                                                            |
|                     |                                                            |
|                     +------------------------------+-----------------------------+
|                     | Dock opcional                 | Inspector opcional          |
+--------------------------------------------------------------------------------+
| Status: sessao, tempo, BPM, instrumento, atalhos, salvamento                    |
+--------------------------------------------------------------------------------+
```

## Zonas

Title Bar:

- Produto.
- Instrumento atual.
- Busca global.
- Controle rápido de BPM/metrônomo.
- Alternância de sidebar e inspector.

Sidebar:

- Macro contexto.
- Menu contextual do contexto ativo.
- Pode colapsar.

Tab Bar:

- Abas abertas.
- Indica aba ativa.
- Permite fechar, navegar e reordenar conceitualmente.

Workspace:

- Área central.
- Nunca deve ser subordinada ao menu.
- Contém módulos da aba ativa.

Inspector:

- Contexto secundário.
- Segue seleção.
- Não substitui a área principal.

Dock:

- Ferramentas persistentes ou temporárias.
- Metronomo, gravador, player, import status.
- Deve ser recolhível.

Status Bar:

- Estado discreto do sistema.
- Sessão ativa/parada, tempo, BPM, instrumento, atalhos e estado de dados.

## Layout Sem Inspector

```text
+--------------------------------------------------------------------------------+
| Title Bar                                                                       |
+--------------------------------------------------------------------------------+
| Sidebar             | Tabs                                                       |
|                     +------------------------------------------------------------+
|                     | Workspace amplo                                           |
|                     |                                                            |
|                     | Grid de cards / paineis principais                         |
+--------------------------------------------------------------------------------+
| Status Bar                                                                      |
+--------------------------------------------------------------------------------+
```

Uso:

- Biblioteca em grade.
- Mapa do Conhecimento.
- Skill Tree expandida.
- Repertório.

## Layout Com Inspector

```text
+--------------------------------------------------------------------------------+
| Title Bar                                                                       |
+--------------------------------------------------------------------------------+
| Sidebar        | Tabs                                             | Inspector   |
|                +--------------------------------------------------+-------------+
|                | Workspace ativo                                  | Contexto    |
|                |                                                  | Relacoes    |
|                | Modulo principal + cards                         | Notas       |
+--------------------------------------------------------------------------------+
| Status Bar                                                                      |
+--------------------------------------------------------------------------------+
```

Uso:

- Seleção de skill.
- Música com seção selecionada.
- Projeto com riff selecionado.
- Diário com sessão selecionada.

## Layout Focus Session

```text
+--------------------------------------------------------------------------------+
| Sessao em foco | Instrumento | Timer | BPM | Pausar | Finalizar                 |
+--------------------------------------------------------------------------------+
|                                                                                |
|                         Atividade atual                                        |
|                                                                                |
|              +------------------+  +------------------+  +------------------+  |
|              | Timer            |  | Metronomo        |  | Notas            |  |
|              +------------------+  +------------------+  +------------------+  |
|                                                                                |
|              +--------------------------------------------------------------+  |
|              | Feedback da atividade                                       |  |
|              +--------------------------------------------------------------+  |
+--------------------------------------------------------------------------------+
```

Uso:

- Sessão iniciada.
- Menos navegação, mais foco.
- Ferramentas visíveis apenas se ajudam a atividade.

