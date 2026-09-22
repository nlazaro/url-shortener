# URL Shortener

A URL shortening service built with **Spring Boot 4.0.3**, **PostgreSQL**, and **Redis**.

## Features
* **Randomized Short Codes:** Uses `SecureRandom` to generate unpredictable 7-character strings (e.g., `aB9x2kL`).
* **Collision-Safe:** Retries with a new code on the rare short-code collision, backed by a unique database constraint.
* **Cache-Aside Layer:** Redis sits in front of PostgreSQL for redirects, with a `CACHE_ENABLED` flag to disable it for testing and benchmarking.
* **Simple UI:** Clean, responsive frontend for "Average Joe" to easily shorten links.
* **Developer Friendly:** Fully documented API via **Swagger/OpenAPI**.
* **Tested:** Unit tests (Mockito) covering short code generation, collision retries, and both cache hit/miss paths, plus a Spring Boot integration test.

## Tech Stack
* **Java 24** (Corretto), **Spring Boot 4.0.3** (Web, Data JPA, Data Redis), **PostgreSQL** (Primary Database), **Redis** (Cache-Aside Layer), **Lombok** (Boilerplate reduction), **JUnit 5 & Mockito** (Testing), **k6** (Load Testing)
---

## Architecture
The application follows a standard N-Tier architecture:
1. **Controller Layer:** Handles REST requests and UI serving.
2. **Service Layer:** Manages random string generation, collision retries, and cache-aside logic.
3. **Repository Layer:** Interacts with PostgreSQL.
4. **Cache Layer:** Interacts with Redis for repeat lookups (toggleable via `CACHE_ENABLED`).
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
   cd url-shortener
   ```
2. Run Docker
   ```bash
   docker compose up
   ```
3. Shutting down docker
   ```bash
   docker compose down
   ```

## Usage
Once the containers are running, you can access the application at:
* **User Interface:** [http://localhost:8080](http://localhost:8080) — The main dashboard to shorten your URLs.
* **API Documentation (Swagger):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) — Explore and test the REST endpoints directly.

## API Endpoints
- `POST /shorten` Takes a JSON body and returns a 7 char code
- `GET /r/{code}` Redirects the user to the original URL

## Example
<img src='assets/example.gif' width='' alt='Video Demo' />

## Caching Benchmark

The Redis cache-aside layer was load-tested with [k6](loadtest/) under three traffic patterns at 50 concurrent users, comparing `CACHE_ENABLED=true` against `CACHE_ENABLED=false`:

| Scenario | Cache off | Cache on |
|---|---|---|
| Small table (100 rows) | 20,107 req/s | 19,052 req/s* |
| 3M rows, uniform random access | 6,549 req/s | 3,972 req/s |
| 3M rows, 200-code hot set (repeat lookups) | 16,372 req/s | 9,167 req/s |

\* after switching Redis's serializer from Spring's JDK default to `StringRedisSerializer`, which recovered most of an initial ~13% regression. See `config/RedisConfig.java`.

**In all three scenarios, enabling the cache made redirects slower, including the hot-set case, which is exactly the access pattern a cache is supposed to help with.** The likely cause: Spring Boot's default Redis client (Lettuce) multiplexes requests over a single connection, so under concurrent load, requests queue behind each other, while PostgreSQL serves the same load over a genuine connection pool (HikariCP). This is documented but not yet fixed — a pooled Lettuce connection factory is the next thing to try.

Run the benchmarks yourself with the scripts in [`loadtest/`](loadtest/).

# Future
- Enable Lettuce connection pooling and re-run the hot-set benchmark
- Add authentication / authorization
- Add statistics tracker
