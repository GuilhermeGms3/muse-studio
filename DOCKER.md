# Music OS no Docker

O Compose executa o frontend, a API Spring Boot e um atualizador restrito aos
containers do Music OS.

## Primeira execução

Autentique o Docker no GitHub Container Registry:

```powershell
$config = Join-Path $HOME ".docker\music-os"
gh auth token | docker --config $config login ghcr.io -u GuilhermeGms3 --password-stdin
```

Depois inicie o aplicativo:

```powershell
docker compose up -d
```

O Music OS fica disponível em `http://localhost:3000`. Os dados pessoais são
mantidos na pasta local `data`, mesmo quando os containers são atualizados.

## Atualizações

Cada push para `main` executa testes e publica novas imagens no GHCR. O serviço
`music-os-updater` verifica as imagens a cada 60 segundos e recria somente os
containers identificados com a etiqueta de atualização.

Para acompanhar:

```powershell
docker compose ps
docker compose logs -f updater
```
