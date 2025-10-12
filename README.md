# shakespeare-search

Little demo app for searching Shakespeare texts with Elasticsearch and Spring Boot.

## Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop (for Elasticsearch & Kibana)
- Python 3 (for preprocessing Shakespeare texts)

---

## Setup

1. **Clone repository**:

```bash
git clone <repo-url>
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

```bash

mvn spring-boot:run
```

* Frontend (Thymeleaf): http://localhost:8080/
* API for keyword count: GET http://localhost:8080/api/keyword-count?q=hamlet
* 