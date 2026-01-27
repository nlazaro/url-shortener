# 🔗 Professional URL Shortener

A high-performance URL shortening service built with **Spring Boot 3.4**, **PostgreSQL**, and **Redis**.

## Features
* **Randomized Short Codes:** Uses `SecureRandom` to generate unpredictable 7-character strings (e.g., `aB9x2kL`).
* **High Performance:** Dual-layer storage using **Redis** for sub-millisecond redirects and **PostgreSQL** for permanent persistence.
* **Simple UI:** Clean, responsive frontend for "Average Joe" to easily shorten links.
* **Developer Friendly:** Fully documented API via **Swagger/OpenAPI**.
* **Tested:** Robust test suite covering Unit logic and Integration flows.

## Tech Stack
* **Java 24** (Corretto)
* **Spring Boot 4.0** (Web, Data JPA, Data Redis)
* **PostgreSQL** (Primary Database)
* **Redis** (Caching Layer)
* **Lombok** (Boilerplate reduction)
* **JUnit 5 & Mockito** (Testing)
---

## Architecture
The application follows a standard N-Tier architecture:
1. **Controller Layer:** Handles REST requests and UI serving.
2. **Service Layer:** Manages random string generation and cache-aside logic.
3. **Repository Layer:** Interacts with PostgreSQL.
4. **Cache Layer:** Interacts with Redis for high-speed lookups.
---

## Getting Started

### Prerequisites
* Docker (for Redis/Postgres) or local installations.
* JDK 24.
* Maven.

### Installation
1. Clone the repo:
   ```bash
   git clone https://github.com/nlazaro/url-shortener.git
   ```
2. Build the jar
```bash
./mvnw clean package -DskipTests
```
3. Run docker
```bash
docker compose up
```

## API Endpoints
- `POST /shorten` Takes a JSON body and returns a 7 char code
- `GET /r/{code}` Redirects the user to the original URL

## Testing
```bash
./mvnw test
```

## Example
`![Project Example](assets/example.gif)`
