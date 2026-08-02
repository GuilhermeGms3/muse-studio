# Error States

Erros devem ser específicos, recuperáveis e respeitar a confiança local-first do Muse Studio.

## Princípios

- Dizer o que falhou.
- Dizer se dados foram preservados.
- Oferecer próxima ação.
- Não culpar o usuário.
- Não apagar contexto.

## API Indisponível

Mensagem:

- "Não foi possível conectar à API local."

Impacto:

- Dados remotos/locais servidos pela API podem não carregar.
- Workspace, abas e navegação continuam disponíveis.

Ações:

- Tentar novamente.
- Ver status local.
- Abrir Dados e Integrações.

## Falha Ao Salvar

Mensagem:

- "Não foi possível salvar agora. Seu rascunho continua nesta tela."

Ações:

- Tentar novamente.
- Copiar conteúdo futuro se necessário.
- Cancelar sem perder contexto.

Aplicável a:

- Sessão.
- Projeto.
- Música.
- Aula.
- Exercício.
- Preferências.

## Falha Ao Iniciar Sessão

Mensagem:

- "A sessão não pôde ser iniciada."

Ações:

- Tentar novamente.
- Iniciar prática livre local futura.
- Ver plano de hoje.

Prevenção:

- Mostrar instrumento selecionado antes de iniciar.

## Falha Ao Registrar Feedback

Mensagem:

- "O feedback desta atividade não foi salvo."

Ações:

- Tentar novamente.
- Manter feedback preenchido.
- Continuar sessão sem perder notas.

## Falha De Gravação

Mensagem:

- "Não foi possível gravar neste contexto."

Possíveis causas:

- Permissão de microfone.
- Dispositivo indisponível.
- Falha ao salvar arquivo.

Ações:

- Tentar novamente.
- Ver permissões.
- Descartar gravação incompleta.

## Importação Inválida

Mensagem:

- "Este arquivo não pode ser importado como áudio, MIDI, MusicXML ou Guitar Pro."

Ações:

- Escolher outro arquivo.
- Ver formatos aceitos.

## Restore De Backup

Mensagem antes da ação:

- "Restaurar backup pode substituir dados locais."

Erro:

- "A restauração não foi concluída."

Ações:

- Tentar novamente.
- Escolher outro arquivo.
- Manter dados atuais quando possível.

## Objeto Não Encontrado

Mensagem:

- "Este item não existe mais ou não pôde ser carregado."

Ações:

- Voltar para lista.
- Buscar novamente.
- Fechar aba.

## Erro No Mapa

Mensagem:

- "As conexões não puderam ser carregadas."

Ações:

- Tentar novamente.
- Ver como lista.
- Abrir Skill Tree.

## Erros Em Abas

Uma aba com erro deve:

- Permanecer aberta.
- Mostrar ação de retry.
- Permitir fechar.
- Preservar nome do objeto quando conhecido.

