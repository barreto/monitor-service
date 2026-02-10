# Monitor Service

Serviço Kotlin + Spring Boot para exposição e consulta de métricas do sistema, pronto para rodar com Docker / Docker
Compose e integrado com Prometheus.

## Visão geral

- Expõe métricas do host (CPU, memória, discos e métricas do serviço) em formato compatível com Prometheus em
  `/api/metrics/prometheus`.
- Consulta o Prometheus para fornecer endpoints REST:
    - GET `/api/metrics/current` — métricas atuais (instant query).
    - GET `/api/metrics/history` — histórico (range query).
- Projeto modular, com WebClient para comunicação com Prometheus, Micrometer para exposição de métricas e configuração
  para execução via Docker Compose.

## Principais tecnologias

- Kotlin (JDK 17)
- Spring Boot (Web + WebFlux)
- Micrometer Prometheus
- WebClient (Spring WebFlux)
- Maven
- Docker
- Docker Compose

## Como executar

### Pré-requisitos

- Docker e Docker Compose instalados.
- Build local antes do docker-compose.
```shell
  mvn -DskipTests clean package
```

### Execução com Docker Compose (recomendado)

Build e subida (reconstrói a imagem e recria containers): 

```shell
  docker-compose up --build --force-recreate
```

### Execução local

Build e subida apenas com requisitos para execução local:

```shell
  docker compose -f docker-compose.local.yml up --build --force-recreat
```

Definir perfil "local" como ativo para o Spring através da seguinte VM Option:

```text
    -Dspring.profiles.active=local
```

## Endpoints e exemplos de uso

### 1. Exposição Prometheus

GET `/api/metrics/prometheus` — retorna texto no formato de scraping do Prometheus.
```shell
  curl http://localhost:8080/api/metrics/prometheus
```

Output:
![Output endpoint prometheus](docs/output-prometheus.png)

### 2. Métrica atual (instant query)

GET `/api/metrics/current` — retorna texto no formato de scraping do Prometheus.
```shell
  curl --request GET --url http://localhost:8080/api/metrics/current
```

Output:
![Output endpoint current](docs/output-current.png)

### 3. Exposição Prometheus

GET `/api/metrics/http://localhost:8080/api/metrics/history` — retorna texto no formato de scraping do Prometheus.
```shell
  curl --request GET --url http://localhost:8080/api/metrics/current
```

Output:
![Output endpoint history](docs/output-history.png)

## Decisões de arquitetura

- WebClient (reactive) para chamadas não bloqueantes ao Prometheus.
- Config via environment para facilitar deploy em diferentes ambientes (local/docker).
