
# URL Shortener Microservices Architecture

## Overview

This project is a URL Shortener built using Java Spring Boot Microservices. The primary objective of this project is to learn and demonstrate backend engineering concepts such as microservice architecture, API Gateway routing, database-per-service design, Redis caching, asynchronous processing, inter-service communication, and system design fundamentals.

The system allows users to:

* Generate short URLs from long URLs
* Redirect short URLs to their original destinations
* Track click counts
* Retrieve analytics for a short URL

The project focuses on learning architecture and design patterns rather than handling production-scale traffic.

---

# Architecture

## Services

### Gateway Service (Port 8085)

Acts as the single entry point into the system.

Responsibilities:

* Route incoming requests
* Hide internal service URLs
* Centralize external access

Routes:

```http
POST /api/shorten
```

Routes to:

```text
Shortening Service
```

```http
GET /api/analytics/{shortCode}
```

Routes to:

```text
Analytics Service
```

```http
GET /{shortCode}
```

Routes to:

```text
Redirect Service
```

---

### Shortening Service (Port 8080)

Responsibilities:

* Generate short codes
* Store URL mappings
* Provide original URL lookup APIs

Public API:

```http
POST /api/shorten
```

Request:

```json
{
  "url": "https://example.com"
}
```

Response:

```json
{
  "shortUrl": "http://localhost:8085/abc123"
}
```

Internal API:

```http
GET /internal/url/{shortCode}
```

Response:

```json
{
  "originalUrl": "https://example.com"
}
```

Database:

```sql
url_mapping
```

| Column       | Type    |
| ------------ | ------- |
| id           | BIGINT  |
| short_code   | VARCHAR |
| original_url | TEXT    |

Database Name:

```text
shortening_db
```

---

### Redirect Service (Port 8081)

Responsibilities:

* Resolve short URLs
* Redirect users
* Maintain click counts
* Manage Redis cache

Public API:

```http
GET /{shortCode}
```

Internal API:

```http
GET /internal/clicks/{shortCode}
```

Response:

```json
{
  "clickCount": 15
}
```

Database:

```sql
redirect_stats
```

| Column      | Type    |
| ----------- | ------- |
| short_code  | VARCHAR |
| click_count | BIGINT  |

Database Name:

```text
redirect_db
```

---

### Analytics Service (Port 8082)

Responsibilities:

* Aggregate URL information
* Aggregate click count information
* Expose analytics endpoint

Public API:

```http
GET /api/analytics/{shortCode}
```

Response:

```json
{
  "shortUrl": "http://localhost:8085/abc123",
  "originalUrl": "https://example.com",
  "clickCount": 15
}
```

Internal Calls:

```http
GET http://localhost:8080/internal/url/{shortCode}
```

```http
GET http://localhost:8081/internal/clicks/{shortCode}
```

---

# High Level Request Flow

## URL Creation

```text
Client
  |
  v
Gateway
  |
  v
Shortening Service
  |
  v
shortening_db
```

---

## URL Redirection

```text
Client
  |
  v
Gateway
  |
  v
Redirect Service
  |
  +----> Redis Cache
  |
  +----> Shortening Service
              |
              v
        shortening_db
```

---

## Analytics Retrieval

```text
Client
  |
  v
Gateway
  |
  v
Analytics Service
  |
  +----> Shortening Service
  |
  +----> Redirect Service
```

---

# Redis Integration

## Why Redis?

Redirect requests are highly read-intensive.

Without caching:

```text
Every redirect
       |
       v
Shortening Service
       |
       v
Database Query
```

This increases latency and puts unnecessary load on the database.

Redis is used to reduce latency for frequently accessed short URLs.

---

# Cache Pattern

The project uses the Cache-Aside Pattern.

Flow:

```text
GET /abc
    |
    v
Redis Lookup

Cache Hit?
   |
 Yes
   |
   v
Return Original URL

No
 |
 v
Shortening Service
 |
 v
Database
 |
 v
Store Result In Redis
 |
 v
Return URL
```

---

# Cache Key Design

Redis stores:

```text
shortCode -> originalUrl
```

Example:

```text
abc123 -> https://leetcode.com
```

---

# Why Cache Only Redirects?

URL creation is relatively infrequent.

Redirect operations are significantly more frequent.

Caching redirect lookups provides the maximum performance benefit while keeping the architecture simple.

---

# Redis Failure Handling

Redis is treated as an optimization layer, not a source of truth.

If Redis becomes unavailable:

```text
Redirect Service
        |
        v
Redis Failure
        |
        v
Fallback
        |
        v
Shortening Service
        |
        v
Database
```

The system continues functioning even when Redis is unavailable.

---

# Consistency Model

Source of Truth:

```text
MySQL
```

Redis only stores cached copies.

All permanent data resides in:

```text
shortening_db
redirect_db
```

---

# Click Count Strategy

Click counts are stored directly in MySQL.

Flow:

```text
Redirect
   |
   v
Database Update
```

The project intentionally avoids:

* Redis click counters
* Write batching
* Delayed synchronization

Reason:

```text
Consistency is preferred over eventual consistency.
```

---

# Asynchronous Processing

Click count updates are performed asynchronously.

Technologies:

```java
@EnableAsync
@Async
```

Flow:

```text
Redirect Request
       |
       v
Return Redirect Response
       |
       v
Background Thread
       |
       v
Update Click Count
```

Benefits:

* Faster user response time
* Reduced request latency
* Better user experience

---

# Database Per Service Pattern

Each microservice owns its own database.

Shortening Service:

```text
shortening_db
```

Redirect Service:

```text
redirect_db
```

Benefits:

* Loose coupling
* Service autonomy
* Independent schema evolution
* Better microservice boundaries

Services never directly access another service's database.

All communication occurs through APIs.

---

# Inter-Service Communication

Current implementation uses:

```java
RestTemplate
```

Examples:

```text
Redirect Service
      |
      v
Shortening Service
```

```text
Analytics Service
      |
      +----> Shortening Service
      |
      +----> Redirect Service
```

---

# Environment Variables

## Gateway Service

```env
SERVER_PORT=8085
```

---

## Shortening Service

```env
SERVER_PORT=8080

DB_URL=jdbc:mysql://localhost:3306/shortening_db
DB_USERNAME=root
DB_PASSWORD=password
```

---

## Redirect Service

```env
SERVER_PORT=8081

DB_URL=jdbc:mysql://localhost:3306/redirect_db
DB_USERNAME=root
DB_PASSWORD=password

REDIS_HOST=localhost
REDIS_PORT=6379
```

---

## Analytics Service

```env
SERVER_PORT=8082

SHORTENING_SERVICE_URL=http://localhost:8080
REDIRECT_SERVICE_URL=http://localhost:8081
```

---
# Internal Service Security

## Current Implementation

Internal service APIs are currently unsecured.

Examples:

```http
GET /internal/url/{shortCode}
GET /internal/clicks/{shortCode}
```

These endpoints are intended for service-to-service communication but can currently be accessed directly if the endpoint URL is known.

This simplification was intentionally chosen to keep the project focused on learning:

* Microservices
* API Gateway
* Redis Caching
* Service Communication
* System Design

rather than authentication and authorization.

---

## Potential Improvement

A simple security mechanism can be added using a shared secret between services.

### Request Flow

```text
Analytics Service
      |
      | X-Internal-Secret: my-secret-key
      v
Redirect Service
```

The receiving service verifies:

```text
Header Secret
        ==
Configured Secret
```

If the secret matches:

```text
Request Allowed
```

Otherwise:

```text
403 Forbidden
```

---

## Example

Request Header:

```http
X-Internal-Secret: my-secret-key
```

Environment Variable:

```env
INTERNAL_SERVICE_SECRET=my-secret-key
```

Validation Logic:

```text
If header secret equals configured secret
    Allow request
Else
    Reject request
```

---

## Production Alternatives

In real-world systems, service-to-service communication is typically secured using:

* JWT Tokens
* OAuth2 Client Credentials
* Mutual TLS (mTLS)
* Service Mesh Authentication (Istio/Linkerd)
* API Gateway Authentication
* Kubernetes Service Accounts

For this project, a shared secret header would be sufficient and significantly simpler to implement while preventing direct access to internal APIs.


# CAP Theorem Discussion

CAP Theorem states that a distributed system can guarantee at most two of the following three properties simultaneously:

## Consistency

Every read receives the latest write.

Example:

```text
All users see the same click count.
```

---

## Availability

Every request receives a response.

Example:

```text
System continues serving requests even during failures.
```

---

## Partition Tolerance

The system continues operating despite network failures between nodes.

Example:

```text
Service A cannot communicate with Service B.
```

---

# CAP Decisions In This Project

The system prioritizes:

```text
Consistency
+
Partition Tolerance
```

over:

```text
Availability
```

Examples:

### Click Counts

Writes go directly to MySQL.

No batching.

No delayed synchronization.

Reason:

```text
Consistency preferred.
```

### Redis Cache

Redis is only a cache.

Database remains the source of truth.

### Analytics

Analytics data comes from authoritative databases rather than cached counters.

---

# Design Tradeoffs

## Chosen

### Simplicity

Easy to understand and debug.

### Strong Consistency

Database is source of truth.

### Service Ownership

Each service owns its own data.

### Cache Aside

Simple and effective caching strategy.

---

## Not Chosen

### Kafka Event Streaming

Avoided to keep architecture simpler.

### Redis Click Counters

Avoided because consistency was preferred.

### Eventual Consistency Analytics

Avoided because accurate analytics were preferred.

### Distributed Transactions

Unnecessary complexity for project goals.

---

# Technologies Used

* Java 21
* Spring Boot
* Spring Cloud Gateway
* Spring Data JPA
* Spring Data Redis
* RestTemplate
* MySQL
* Redis
* Maven
* Lombok
* Docker (planned)
* Docker Compose (planned)

---

# Future Enhancements

* OpenFeign
* Dockerization
* Docker Compose
* Eureka Service Discovery
* Resilience4j Circuit Breakers
* Kafka Event Streaming
* Distributed Tracing
* Monitoring with Prometheus and Grafana
* Kubernetes Deployment

---

# Key Concepts Demonstrated

* Microservices Architecture
* API Gateway Pattern
* Database Per Service Pattern
* Cache-Aside Pattern
* Redis Caching
* Asynchronous Processing
* Service-to-Service Communication
* CAP Theorem Tradeoffs
* Fault Tolerance
* Distributed Systems Fundamentals
* System Design Principles
