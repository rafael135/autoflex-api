# autoflex-api

REST API desenvolvida com **Quarkus** e **PostgreSQL** para controle de produção industrial — gerenciamento de produtos, matérias-primas e cálculo da capacidade produtiva com base no estoque disponível.

## Estrutura de pastas

```
autoflex-api/
├── docker-compose.yml                  # Sobe o banco PostgreSQL localmente
├── pom.xml                             # Dependências e configurações Maven/Quarkus
└── src/
    ├── main/
    │   ├── docker/                     # Dockerfiles para deploy (JVM, native, etc.)
    │   ├── java/com/projedata/autoflex/
    │   │   ├── domain/                 # Entidades JPA (Product, RawMaterial, ProductMaterial)
    │   │   ├── features/               # Casos de uso organizados por feature
    │   │   │   ├── product/            # CRUD de produtos
    │   │   │   │   ├── createProduct/
    │   │   │   │   ├── listProducts/
    │   │   │   │   ├── updateProduct/
    │   │   │   │   └── deleteProduct/
    │   │   │   ├── rawMaterial/        # CRUD de matérias-primas
    │   │   │   │   ├── createRawMaterial/
    │   │   │   │   ├── listRawMaterial/
    │   │   │   │   ├── updateRawMaterial/
    │   │   │   │   └── deleteRawMaterial/
    │   │   │   ├── production/         # Cálculo da capacidade produtiva
    │   │   │   │   └── getTotalProductionCapacity/
    │   │   │   │       ├── dto/        # DTOs de resposta
    │   │   │   │       └── services/   # Estratégias de cálculo (Strategy Pattern)
    │   │   │   └── shared/             # DTOs compartilhados (ex: paginação)
    │   │   └── infrastructure/         # Repositórios Panache, tratamento global de erros e seeder
    │   └── resources/
    │       ├── application.properties  # Configurações da aplicação (datasource, CORS, etc.)
    │       └── db/migration/           # Scripts SQL versionados com Flyway
    └── test/
        └── java/com/projedata/autoflex/
            ├── domain/                 # Testes unitários das entidades de domínio
            └── features/               # Testes de integração por feature
```

## Pré-requisitos

- Java 17+
- Maven
- Docker

## Subindo o banco de dados

```shell
docker-compose up -d
```

Isso sobe um container PostgreSQL com as seguintes credenciais:

| Parâmetro | Valor          |
|-----------|----------------|
| Host      | localhost:5432 |
| Database  | autoflex_db    |
| User      | autoflex       |
| Password  | autoflex123    |

As migrations são aplicadas automaticamente via **Flyway** ao iniciar a aplicação.

## Rodando a aplicação

```shell
./mvnw quarkus:dev
```

A API estará disponível em `http://localhost:8080`.

## Rodando os testes

```shell
./mvnw test
```

---

## Endpoints da API

### Matérias-primas — `/api/raw-materials`

| Método | Rota                    | Descrição                                                            |
|--------|-------------------------|----------------------------------------------------------------------|
| GET    | `/api/raw-materials`    | Lista matérias-primas com paginação (`page`, `itemsPerPage`) e filtro por `name` |
| POST   | `/api/raw-materials`    | Cadastra uma nova matéria-prima                                      |
| PUT    | `/api/raw-materials/{id}` | Atualiza nome e/ou estoque de uma matéria-prima                    |
| DELETE | `/api/raw-materials/{id}` | Remove uma matéria-prima                                           |

### Produtos — `/api/products`

| Método | Rota                 | Descrição                                                            |
|--------|----------------------|----------------------------------------------------------------------|
| GET    | `/api/products`      | Lista produtos com paginação (`page`, `itemsPerPage`)               |
| POST   | `/api/products`      | Cadastra um novo produto com seus requisitos de matéria-prima        |
| PUT    | `/api/products/{id}` | Atualiza nome, valor e/ou matérias-primas de um produto             |
| DELETE | `/api/products/{id}` | Remove um produto                                                    |

### Capacidade produtiva — `/api/production`

| Método | Rota              | Descrição                                                                                              |
|--------|-------------------|--------------------------------------------------------------------------------------------------------|
| GET    | `/api/production` | Calcula e retorna a capacidade produtiva total com base no estoque disponível. Aceita o query param `strategy` (`0` = maior valor, `1` = maior ROI). |

---

## Decisões de projeto

### Estrutura por features

O código é organizado por feature (ex: `features/product`, `features/rawMaterial`, `features/production`) em vez de por camada técnica. Isso facilita localizar e manter tudo relacionado a um caso de uso no mesmo lugar.

### Cálculo da capacidade produtiva — Strategy Pattern

O endpoint `GET /api/production` aceita um query param `strategy` para definir o critério de priorização:

| Valor | Estratégia                  | Critério                                                                  |
|-------|-----------------------------|--------------------------------------------------------------------------|
| `0`   | `HighestPriceStrategy`      | Prioriza produtos pelo **maior valor de venda**, conforme requisito RF004 |
| `1`   | `HighestEfficiencyStrategy` | Prioriza produtos pelo **maior ROI** (valor / custo de matéria-prima)    |

A estratégia padrão (`0`) atende diretamente ao requisito do teste: _"a priorização deve ser pelos produtos de maior valor"_.

A `HighestEfficiencyStrategy` foi implementada como extensão além do requisito, pois em cenários reais priorizar pelo maior valor absoluto pode desperdiçar matéria-prima cara em produtos de baixa margem. O ROI oferece uma visão mais equilibrada da produção.

Ambas as estratégias implementam a interface `IProductionCalculationStrategy`, tornando simples adicionar novos critérios sem alterar o código existente.

### Migrations com Flyway

O schema do banco é versionado via Flyway, garantindo rastreabilidade e reprodutibilidade do ambiente em qualquer máquina.