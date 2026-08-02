# Screen Specs

Cada tela responde uma pergunta principal. Nenhuma tela deve competir com outra pelo foco do usuário.

## Home

Objetivo: orientar o retorno ao estúdio.

Ação principal: iniciar ou continuar a prática mais relevante.

Ações secundárias:

- Abrir objetivo atual.
- Abrir música atual.
- Abrir projeto recente.
- Registrar nota rápida.
- Iniciar diagnóstico se necessário.

Informações exibidas:

- Instrumento atual.
- Plano de hoje resumido.
- Continuar de onde parou.
- Objetivo atual.
- Notas rápidas.
- Projetos recentes.
- Estado de sessão.

Estados vazios:

- Sem diagnóstico: destacar "Encontrar ponto de partida".
- Sem plano: sugerir gerar plano para o instrumento atual.
- Sem projetos: oferecer criar projeto em Compor.

Estados de erro:

- API indisponível: manter workspace local visível e indicar dados temporariamente indisponíveis.
- Falha ao carregar plano: oferecer tentar novamente.

Estados de carregamento:

- Cards skeleton compactos.
- Não bloquear busca, abas ou navegação básica.

Componentes utilizados:

- Cards de plano, continuar, objetivo, notas e projetos.
- Painéis para agrupamentos.
- Inspector opcional.
- Status bar persistente.

## Diagnóstico Inicial

Objetivo: calibrar ponto de partida.

Ação principal: concluir diagnóstico.

Ações secundárias:

- Voltar etapa.
- Pular apenas se houver caminho seguro para Home.
- Ajustar instrumento.

Informações exibidas:

- Instrumento principal.
- Nível.
- Tempo de sessão.
- Gêneros, artistas e músicas favoritas.
- Baseline rítmico, auditivo e técnico.

Estados vazios:

- Campos opcionais podem ficar vazios.
- Instrumento e tempo exigem padrão selecionado.

Estados de erro:

- Falha ao salvar: manter respostas e permitir tentar novamente.
- Valor inválido: indicar campo específico.

Estados de carregamento:

- Botão "Salvando" e bloqueio apenas da submissão.

Componentes utilizados:

- Stepper.
- Cards de escolha.
- Inputs curtos.
- Botões de navegação.

## Praticar / Sessão

Objetivo: manter o usuário praticando com foco.

Ação principal: executar atividade atual.

Ações secundárias:

- Pausar.
- Avançar atividade.
- Registrar feedback.
- Ajustar BPM.
- Gravar.
- Finalizar sessão.

Informações exibidas:

- Atividade atual.
- Timer.
- Instrumento.
- BPM.
- Notas.
- Próxima atividade.
- Feedback.

Estados vazios:

- Sem plano: iniciar prática livre ou gerar plano.
- Sem exercício selecionado: sugerir atividade do objetivo atual.

Estados de erro:

- Falha ao salvar feedback: preservar dados digitados.
- Falha de gravação: explicar permissão ou dispositivo.

Estados de carregamento:

- Iniciando sessão.
- Salvando atividade.
- Gerando resumo.

Componentes utilizados:

- Focus workspace.
- Painel de atividade.
- Timer.
- Controle BPM.
- Notas.
- Feedback modal ou painel.
- Dock de gravador/metrônomo.

## Plano De Hoje

Objetivo: transformar intenção em sequência praticável.

Ação principal: iniciar sessão com plano.

Ações secundárias:

- Marcar atividade como concluída.
- Reordenar conceitualmente em versão futura.
- Regenerar plano.
- Abrir habilidade, exercício ou música ligada.

Informações exibidas:

- Atividades.
- Duração.
- Tipo.
- Instrumento.
- Alvo.
- Estado concluído.

Estados vazios:

- Sem plano: sugerir gerar plano ou concluir diagnóstico.

Estados de erro:

- Falha ao atualizar atividade: desfazer estado otimista.

Estados de carregamento:

- Lista skeleton por atividade.

Componentes utilizados:

- Cards de atividade.
- Painel de prioridades.
- Ação primária fixa.
- Inspector com objetivo atual.

## Exercícios

Objetivo: escolher e executar exercício técnico.

Ação principal: iniciar exercício selecionado.

Ações secundárias:

- Filtrar por técnica.
- Ajustar BPM.
- Registrar tentativa.
- Abrir habilidade relacionada.
- Criar ou editar exercício.

Informações exibidas:

- Técnicas.
- Exercícios.
- BPM atual e alvo.
- Dificuldade.
- Critérios de aprovação.
- Variações.

Estados vazios:

- Sem exercícios para técnica: oferecer criar exercício.

Estados de erro:

- Falha ao registrar tentativa: manter valores.

Estados de carregamento:

- Lista de técnicas e cards skeleton.

Componentes utilizados:

- Lista de técnicas.
- Cards de exercícios.
- Runner em painel.
- Inspector com habilidade e critérios.

## Ear Training

Objetivo: treinar reconhecimento auditivo.

Ação principal: responder exercício atual.

Ações secundárias:

- Trocar módulo.
- Repetir áudio.
- Ver desempenho.
- Ajustar dificuldade futura.

Informações exibidas:

- Módulo.
- Prompt auditivo.
- Opções.
- Resposta.
- Precisão.
- Recomendação de foco.

Estados vazios:

- Sem estatísticas: começar primeiro exercício.

Estados de erro:

- Falha de áudio: permitir tentar novamente.
- Falha ao registrar resposta: manter resultado local temporário.

Estados de carregamento:

- Carregando estatísticas.

Componentes utilizados:

- Cards de módulo.
- Player simples.
- Botões de resposta.
- Painel de desempenho.

## Biblioteca

Objetivo: encontrar e organizar conteúdo musical.

Ação principal: abrir conteúdo relevante.

Ações secundárias:

- Filtrar por categoria.
- Abrir repertório.
- Abrir favoritos, playlists, downloads, artistas.
- Importar material.
- Criar ou editar conteúdo.

Informações exibidas:

- Índice.
- Categorias.
- Conteúdos.
- Nível.
- Tempo estimado.
- Relação com habilidades.

Estados vazios:

- Categoria sem conteúdo: sugerir criar conteúdo ou importar.

Estados de erro:

- Falha ao carregar biblioteca: mostrar retry.

Estados de carregamento:

- Índice e cards skeleton.

Componentes utilizados:

- Menu contextual.
- Índice.
- Cards de conteúdo.
- Editor contextual.
- Inspector para relações.

## Aula / Conceito

Objetivo: estudar um conceito com relações práticas.

Ação principal: ler/praticar o próximo passo.

Ações secundárias:

- Abrir exercício relacionado.
- Abrir música relacionada.
- Abrir habilidade.
- Expandir exemplos, diagramas, tablatura e erros comuns.

Informações exibidas:

- Resumo.
- Objetivos.
- Explicação.
- Exemplos.
- Diagrama.
- Tablatura.
- Erros comuns.
- Relações.

Estados vazios:

- Sem diagrama: ocultar módulo de diagrama.
- Sem relações: sugerir conectar habilidade ou exercício.

Estados de erro:

- Conteúdo não encontrado: voltar para Biblioteca.

Estados de carregamento:

- Cabeçalho e módulos skeleton.

Componentes utilizados:

- Painel de leitura.
- Cards de exemplos.
- Diagrama musical.
- Inspector de relações.

## Skill Tree / Habilidade

Objetivo: entender estado de domínio e próximo requisito.

Ação principal: escolher o próximo exercício, conteúdo ou revisão.

Ações secundárias:

- Alterar estado quando permitido.
- Registrar evidência.
- Abrir pré-requisito.
- Abrir próxima habilidade.
- Filtrar por domínio/instrumento.

Informações exibidas:

- Grafo/lista de habilidades.
- Estado.
- Progresso.
- Horas.
- Precisão.
- BPM atual/alvo.
- Pré-requisitos.
- Conteúdos.
- Exercícios.
- Músicas.
- Próximos requisitos.

Estados vazios:

- Sem habilidade selecionada: selecionar primeira habilidade ativa.

Estados de erro:

- Falha ao atualizar estado: manter estado anterior.

Estados de carregamento:

- Grafo skeleton ou lista compacta.

Componentes utilizados:

- Grafo/painel de skill.
- Cards de relação.
- Inspector.
- Meters e state tags.

## Mapa Do Conhecimento

Objetivo: explorar conexões musicais.

Ação principal: selecionar nó de conhecimento.

Ações secundárias:

- Abrir conteúdo.
- Abrir habilidade.
- Abrir exercício.
- Abrir música.
- Filtrar por instrumento ou domínio.

Informações exibidas:

- Rede de conceitos.
- Relações.
- Pré-requisitos.
- Próximos passos.

Estados vazios:

- Sem mapa calculado: usar Skill Tree como fallback explícito.

Estados de erro:

- Falha ao carregar relações: oferecer lista alternativa.

Estados de carregamento:

- Placeholder de grafo.

Componentes utilizados:

- Grafo.
- Inspector.
- Cards de conexões.

## Repertório

Objetivo: encontrar música para estudar, manter ou revisar.

Ação principal: abrir música.

Ações secundárias:

- Filtrar por instrumento, status, artista ou técnica.
- Criar ou editar música.
- Abrir artista, playlist ou favorito.

Informações exibidas:

- Música.
- Artista.
- Instrumento.
- Status.
- BPM.
- Dificuldade.
- Progresso.
- Técnicas.

Estados vazios:

- Sem músicas: sugerir criar música ou usar sugestões.

Estados de erro:

- Falha ao carregar repertório: retry.

Estados de carregamento:

- Cards de música skeleton.

Componentes utilizados:

- Cards de música.
- Filtros contextuais.
- Inspector de seleção.

## Música

Objetivo: avançar uma música por partes.

Ação principal: selecionar seção ou gerar plano de prática.

Ações secundárias:

- Ajustar BPM no metrônomo.
- Gravar.
- Editar música.
- Abrir habilidade relacionada.
- Abrir sessão focada.

Informações exibidas:

- Título.
- Artista.
- Instrumento.
- Afinação.
- Tom.
- BPM.
- Status.
- Notas.
- Seções.
- Técnicas.
- Escalas.
- Gravações.

Estados vazios:

- Sem seções: sugerir criar seções.
- Sem gravações: oferecer gravar neste contexto.

Estados de erro:

- Música não encontrada: voltar para Repertório.
- Falha ao gerar plano: manter música aberta.

Estados de carregamento:

- Detalhe da música skeleton.

Componentes utilizados:

- Painel de overview.
- Cards de seção.
- Tablatura.
- Recorder.
- Inspector.

## Projetos

Objetivo: escolher ou criar trabalho criativo.

Ação principal: abrir projeto ou capturar nova ideia.

Ações secundárias:

- Criar projeto.
- Filtrar por status.
- Abrir riffs, letras, versões ou referências.

Informações exibidas:

- Projetos.
- Status.
- Tom.
- BPM.
- Ideias recentes.

Estados vazios:

- Sem projetos: criar primeiro projeto.

Estados de erro:

- Falha ao salvar projeto: manter rascunho.

Estados de carregamento:

- Cards de projeto skeleton.

Componentes utilizados:

- Cards de projeto.
- Captura rápida.
- Inspector.

## Projeto

Objetivo: desenvolver material criativo.

Ação principal: editar/capturar riff, ideia ou letra.

Ações secundárias:

- Adicionar referência.
- Criar versão.
- Gravar.
- Preparar integração Reaper.

Informações exibidas:

- Riffs.
- Letras.
- Ideias.
- Referências.
- Versões.
- Gravações.
- Tom.
- BPM.
- Status.

Estados vazios:

- Sem riffs: campo de captura de riff.
- Sem versões: criar primeira versão.

Estados de erro:

- Falha ao salvar: manter edição local.

Estados de carregamento:

- Módulos skeleton.

Componentes utilizados:

- Cards.
- Painéis editáveis.
- Inspector.
- Dock de gravação.

## Diário / Revisar

Objetivo: revisar histórico e transformar observação em próximo passo.

Ação principal: abrir registro e agir sobre relação.

Ações secundárias:

- Filtrar por data, instrumento, habilidade, música ou exercício.
- Abrir sessão relacionada.
- Abrir habilidade para revisão.
- Exportar diário.

Informações exibidas:

- Sessões.
- Duração.
- Trabalhado.
- Dificuldades.
- Melhorias.
- Notas.

Estados vazios:

- Sem histórico: iniciar primeira sessão.

Estados de erro:

- Falha ao carregar diário: retry.

Estados de carregamento:

- Lista skeleton.

Componentes utilizados:

- Cards/lista de entradas.
- Painel de registro.
- Inspector de relações.

## Dados E Integrações

Objetivo: gerenciar arquivos, dados locais e integrações.

Ação principal: executar ferramenta escolhida no contexto.

Ações secundárias:

- Importar.
- Fazer backup.
- Restaurar.
- Exportar CSV.
- Ver status de dados.
- Configurar MIDI.

Informações exibidas:

- Diretório local.
- Contagem de dados.
- Arquivos importados.
- Estado de backup/import.
- Integrações disponíveis.

Estados vazios:

- Sem importações: área de drop.
- Sem MIDI: indicar desconectado.

Estados de erro:

- Falha de importação: explicar tipo/arquivo.
- Falha de restore: indicar que dados existentes permanecem seguros se aplicável.

Estados de carregamento:

- Importando.
- Exportando.
- Restaurando.

Componentes utilizados:

- Cards de ferramenta.
- Dropzone.
- Painéis de status.
- Confirmações.

## Preferências

Objetivo: ajustar o estúdio pessoal.

Ação principal: salvar preferências.

Ações secundárias:

- Alterar instrumento principal.
- Alterar tempo padrão de sessão.
- Ajustar nível.
- Editar gêneros, artistas e músicas favoritas.
- Revisar diretório local.
- Abrir integrações.
- Refazer diagnóstico quando fizer sentido.

Informações exibidas:

- Instrumento principal.
- Tempo de sessão.
- Nível.
- Preferências musicais.
- Estado de onboarding.
- Diretório local.
- Integrações disponíveis.

Estados vazios:

- Sem preferências musicais: permitir salvar sem bloquear e sugerir preencher depois.

Estados de erro:

- Falha ao salvar: manter alterações em tela.
- Diretório inválido futuro: explicar problema e pedir novo caminho.

Estados de carregamento:

- Salvando preferências.
- Carregando estado local.

Componentes utilizados:

- Formulário em painéis.
- Cards de instrumento.
- Inputs compactos.
- Confirmações para dados locais.
- Inspector com impacto das mudanças.
