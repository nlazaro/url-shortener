# High performance URL shortener
A containerized REST API built wiht **Spring Boot**, **Redis**, and **PostgreSQL (H2 for dev) that converts long URLs into compapct 6 character aliases using Base62 encoding

## Features
- Base62 Encoding: Custom algorithm to map database IDs to short, URL safe strings
- High performance caching: Integreated Redis to reduce databse read latency for frequently accessed URLs
- Global Exception Handling: Clean, standardized JSON error responses for invalid or missing URLs
- Containerized Architecture: Fulled dockerized for seamless deployment and enviornment parity
- In memory H2 Console: Integreated databse management interface for real time data inspection

## Tech Stack
- Language: Java 24 (Amazon Corretto)
- Framework: Spring Boot 4.0.1
- Database: H2 (In-Memory) / PostgreSQL
- Cache: Redis
- DevOps: Docker, Docker Compose
- Testing: JUnit 5, Mockito

## Getting Started
1. Clone the repository:
```
git clone https://github.com/nlazaro/url-shortener.git
cd url-shortener
```
2. Spin up the enviornment:
```
docker compose up --build
```

## API Documentation
1. Shorten a URL
`POST /shorten`
- Request Body:
  ```
  {
  "url": "https://www.youtube.com"
  }
  ```
- Response: `201 Created`
  - Returns the short code (e.g., `b`)

 2. Redirect to Original URL
`GET /{shortCode}`
- Redirects the client to the original long URL with a `302 Found` status

3. Database Inspection
Access the H2 Console at `http://localhost:8080/h2-console`
- JDBC URL `jdbc:h2:mem:urldb`
- User `sa`
- Password password`

 ## System Architecture
 1. **Write Path**: When a URL is shortened, it is saved to the database. The auto incremented ID is then encoded into Base62
 2. **Read Path**: The system follows a Cache-Aside Pattern. It checks Redis for the short code; if not found (cache miss), it queries the database and populates the cache for subsequent requests.
