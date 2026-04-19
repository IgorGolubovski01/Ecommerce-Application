# E-Commerce Microservices

A Spring-based e-commerce backend built with microservices, centralized config, service discovery, API gateway, and async messaging.

## Services
- `config-server` - Centralized configuration (Spring Cloud Config Server) on port `8888`
- `eureka` - Service discovery (Netflix Eureka Server) on port `8761`
- `gateway` - API Gateway (Spring Cloud Gateway, WebFlux, Redis, Resilience4j) on port `8080`
- `product` - Product service (MySQL + JPA) on port `8081`
- `user` - User service (MongoDB) on port `8082`
- `order` - Order service (MySQL + JPA + RabbitMQ producer) on port `8083`
- `notification` - Notification service (RabbitMQ consumer) on port `8084`

## Tech Stack
- Java `21`
- Spring Boot `4.0.5`
- Spring Cloud `2025.1.1`
- Spring Cloud Config Server
- Spring Cloud Netflix Eureka (Server + Clients)
- Spring Cloud Gateway (WebFlux)
- Spring Data JPA (Hibernate)
- Spring Data MongoDB
- RabbitMQ (AMQP)
- Resilience4j Circuit Breaker/Retry/Rate Limiter
- Redis (reactive, gateway)
- MySQL
- Maven
- Lombok

## Run Order
1. Start `config-server`
2. Start `eureka`
3. Start core services: `product`, `user`, `order`, `notification`
4. Start `gateway`

## Build & Run
Run in each service folder:

```bash
./mvnw clean spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean spring-boot:run
```
