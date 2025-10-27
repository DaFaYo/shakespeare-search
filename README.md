# shakespeare-search

Little demo app for searching Shakespeare texts with Elasticsearch and Spring Boot.

## Prerequisites

- Java 17 or higher (required for Spring Boot 3.x)
- Maven 3.9+ (for build and dependency management)
- Docker and Docker Compose (for MySQL, Elasticsearch, Kibana and Logstash)
- Python 3 (for preprocessing Shakespeare texts)

---

## Setup

1. **Clone repository**:

```bash
git clone git@github.com:DaFaYo/shakespeare-search.git
cd shakespeare-search
```

2. **Download Shakespeare texts (full works) and split them into separate files**:

```bash
python3 split_shakespeare.py
```
This will create a directory:
```bash
data/shakespeare/
```

## Running the app

Start docker:

```bash
docker-compose up -d

```
Build the application:

```bash

mvn clean compile
```

Run the application:

```bash

mvn spring-boot:run
```

## Important url's

Main application: http://localhost:8080/

| Functie                   | URL                                                                                        |
| ------------------------- | ------------------------------------------------------------------------------------------ |
| Swagger UI                | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| REST API base path        | `http://localhost:8080/api`                                                                |
| Document Search           | `http://localhost:8080/api/documents/search?q=hamlet`                                      |
| Database Search           | `http://localhost:8080/api/database/search?q=hamlet`                                       |
| CRUD Plays API            | `http://localhost:8080/api/plays`                                                          |
| Kibana (Elasticsearch UI) | [http://localhost:5601](http://localhost:5601)                                             |
| Elasticsearch API         | [http://localhost:9200](http://localhost:9200)                                             |


## Overview Diagram

```text
┌────────────────────┐
│     Frontend       │
│ (Thymeleaf + JS)   │
│ http://localhost:8080 │
└─────────┬──────────┘
          │ REST API-calls
          ▼
┌──────────────────────────────┐
│     Spring Boot Backend      │
│  (Shakespeare Search API)    │
│                              │
│  • Controllers (REST)        │
│  • Services (Business logic) │
│  • Repositories (JPA)        │
│  • DTOs & MapStruct mappers  │
│  • Validation (Jakarta)      │
└──────────┬───────────┬──────┘
           │           │
           │           │
           │           │
     CRUD via JPA      │ Full-text search
           │           │
           ▼           ▼
┌────────────────┐    ┌─────────────────────┐
│     MySQL      │    │   Elasticsearch     │
│  plays-tabel    │    │ index: shakespeare │
│ id | title | text │  │ index: plays       │
└─────────┬────────┘    └─────────┬──────────┘
          │                       ▲
          │ JDBC Poll             │
          ▼                       │
     ┌────────────────────┐       │
     │     Logstash       │───────┘
     │ - Pollt MySQL elke minuut │
     │ - Schrijft naar Elasticsearch │
     └────────────────────┘

┌────────────────────────────┐
│         Kibana             │
│  http://localhost:5601     │
│  Visualisatie & search UI  │
└────────────────────────────┘

```
## Summary of the flow

| Actie                 | Bron                          | Verwerking                        | Doel                                                   |
| --------------------- | ----------------------------- | --------------------------------- | ------------------------------------------------------ |
| Nieuwe play toevoegen | Swagger (POST /api/plays)     | Spring Boot → MySQL               | Wordt later door Logstash toegevoegd aan Elasticsearch |
| Play zoeken           | Webinterface of API           | Elasticsearch query               | Retourneert resultaten met highlight                   |
| Play wijzigen         | Swagger (PUT /api/plays/{id}) | Spring Boot → MySQL               | Logstash herindexeert                                  |
| Document zoeken       | /api/documents/search         | Elasticsearch (shakespeare index) | Doorzoekt alle werken van Shakespeare                  |
| Analyseren            | Kibana UI                     | Elasticsearch data                | Grafieken en tekstanalyse                              |

