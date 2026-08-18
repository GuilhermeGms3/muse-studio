# Music OS no Docker

O Compose padrão constrói o frontend e a API a partir deste checkout. Assim,
`docker compose up -d` recompila automaticamente quando o código, as dependências
ou os Dockerfiles mudam e recria somente os containers cuja imagem foi alterada.

## Subir a versão atual do checkout

```powershell
docker compose up -d
```

O Music OS fica disponível em `http://localhost:3000`. Os dados pessoais são
mantidos na pasta local `data`, mesmo quando os containers são recriados.

O build usa o `package-lock.json` com `npm ci`, portanto uma alteração de
dependência também invalida corretamente a camada de dependências do frontend.

## Atualizações remotas (opcional)

O repositório ainda publica imagens no GHCR pelo workflow de CI. O updater foi
movido para o perfil `remote-updates`, pois ele não deve substituir uma imagem
local recém-construída pelo Compose padrão.

Para iniciar somente esse serviço quando necessário:

```powershell
docker compose --profile remote-updates up -d updater
```

Para acompanhar:

```powershell
docker compose ps
docker compose logs -f api web
```
