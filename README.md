![CI](https://github.com/maxim618/Online-Store-Website/actions/workflows/ci.yml/badge.svg)

# Online Store Backend (Spring Boot)

Production-ready backend for an e-commerce application with **JWT authentication**,  
**role-based authorization**, **ownership-safe access control (IDOR protection)**  
and a **full integration test suite**.

This project demonstrates a clean backend architecture, secure API design,
and realistic testing strategies used in real-world Spring Boot applications.

---

## ✨ Key Features

- JWT authentication (stateless)
- Role-based authorization (USER / ADMIN)
- Secure product catalog management (ADMIN-only CRUD)
- Shopping cart & wishlist functionality
- Order placement with transactional integrity
- **Ownership-based access control (IDOR-safe)**
- Global exception handling
- **Comprehensive integration tests**
- FK-aware database cleanup for deterministic tests

---

## 🧱 Architecture Overview

- Layered architecture:
- Stateless security with JWT
- User identity is derived **only from SecurityContext**
- Request parameters are never trusted for authorization
- Business invariants enforced at service layer
- Database integrity via foreign keys & unique constraints

---

## 🔐 Security Model

### Authentication
- JWT-based authentication
- Token validation via custom `JwtFilter`
- Blacklisted tokens on logout

### Authorization

| Endpoint | Access |
|--------|--------|
| `GET /api/products` | Public |
| `POST /api/products` | ADMIN |
| `PUT /api/products/{id}` | ADMIN |
| `DELETE /api/products/{id}` | ADMIN |
| `/api/cart/**` | Authenticated USER |
| `/api/wishlist/**` | Authenticated USER |
| `/api/orders/**` | Authenticated USER |
| Reading чужих заказов | ❌ Forbidden |

> **Important:**  
> User identity is never taken from request parameters.  
> Ownership is validated using JWT claims from `SecurityContext`.

This protects the API from **IDOR vulnerabilities** (Insecure Direct Object Reference).

---

## 🧪 Testing Strategy

The project contains a **full integration test suite**, covering both
business logic and security rules.

### Covered Scenarios

- JWT filter & security chain
- Product access control (USER vs ADMIN)
- Cart / Wishlist CRUD flows
- Order placement & cart cleanup
- Order ownership validation
- Unauthorized / forbidden access scenarios
- Authentication & logout flows

### Test Infrastructure

- JUnit 5 + MockMvc
- Real Spring context (`@SpringBootTest`)
- FK-aware database cleanup via shared `DbCleaner`
- Deterministic test execution

> Integration tests are treated as first-class citizens, not optional checks.

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL
- JWT (jjwt)
- JUnit 5
- MockMvc
- MapStruct
- Lombok

---

## ▶️ Run Locally

### Prerequisites
- Java 17+
- MySQL

### Steps

1. Configure database and JWT in `application.properties`
2. Run tests: 
   ```bash
   mvn clean test
   ```
3. Run application:
   ```bash
   mvn spring-boot:run
   ```
---
## 📁 Project Structure (Simplified)
src/main/java/com/ecommerce
- ├── config
- ├── security
- ├── service
- ├── persistence
- │ ├── model
- │   └── repository
- ├── web
- │   ├── controller
- │   ├── dto
- │   └── mapper

src/test/java/com/ecommerce
- ├── cart
- ├── order
- ├── security
- ├── wishlist
- ├── service
- └── testutil (DbCleaner)

---

## 📌 Notes

This repository focuses on backend responsibilities only
The project is designed to be used with a separate frontend (SPA)
