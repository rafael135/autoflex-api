# autoflex-api

REST API desenvolvida com **Quarkus** e **PostgreSQL** para controle de produção industrial  gerenciamento de produtos, materias-primas e cálculo da capacidade produtiva com base no estoque disponível.

## Pré-requisitos

- Java 17+
- Maven
- Docker

## Subindo o banco de dados

```shell
docker-compose up -d
```

Isso sobe um container PostgreSQL com as seguintes credenciais:

| Parametro | Valor          |
|-----------|----------------|
| Host      | localhost:5432 |
| Database  | autoflex_db    |
| User      | autoflex       |
| Password  | autoflex123    |

As migrations são aplicadas automaticamente via **Flyway** ao iniciar a aplicacão.

## Rodando a aplicacão

```shell
./mvnw quarkus:dev
```

A API estara disponivel em `http://localhost:8080`.

## Rodando os testes

```shell
./mvnw test
```

---

## Decisões de projeto

### Estrutura por features

O codigo e organizado por feature (ex: `features/product`, `features/rawMaterial`, `features/production`) em vez de por camada tecnica. Isso facilita localizar e manter tudo relacionado a um caso de uso no mesmo lugar.

### Cálculo da capacidade produtiva  Strategy Pattern

O endpoint `GET /api/production` aceita um query param `strategy` para definir o critério de priorização:

| Valor | Estratégia                    | Criterio                                                                  |
|-------|-------------------------------|---------------------------------------------------------------------------|
| `0`   | `HighestPriceStrategy`        | Prioriza produtos pelo **maior valor de venda**, conforme requisito RF004  |
| `1`   | `HighestEfficiencyStrategy`   | Prioriza produtos pelo **maior ROI** (valor / custo de materia-prima)     |

A estrategia padrão (`0`) atende diretamente ao requisito do teste: _"a priorização deve ser pelos produtos de maior valor"_.

A `HighestEfficiencyStrategy` foi implementada como extensao além do requisito, pois em cenarios reais priorizar pelo maior valor absoluto pode desperdiçar materia-prima cara em produtos de baixa margem. O ROI oferece uma visão mais equilibrada da produção.

Ambas as estrategias implementam a interface `IProductionCalculationStrategy`, tornando simples adicionar novos critérios sem alterar o código existente.

### Migrations com Flyway

O schema do banco é versionado via Flyway, garantindo rastreabilidade e reprodutibilidade do ambiente em qualquer máquina.