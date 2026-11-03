# TopCinnamon – Backend

Spring Boot REST API for **TopCinnamon**, an e-commerce website for Sri Lankan cinnamon products. It handles products, user accounts, shopping carts, checkout and payments, product reviews and customer contact messages, and serves the data used by the [React frontend](https://github.com/NisalWeerakoon/topcinnamon-frontend).

> Team project (SLIIT, Year 2). This was the team's first full-stack web application.

## Features

- **Products & store:** product catalogue with categories, stored in MySQL
- **Admin dashboard:** product management and an overview of payments
- **User accounts:** registration and login secured with Spring Security
- **Shopping cart:** database-backed cart per user (add, update, remove, clear)
- **Checkout & payments:** checkout flow with a mock payment gateway, payment records and status tracking
- **Reviews:** customers can post and edit product reviews, and admins can moderate them
- **Contact:** contact form messages with email notifications

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot (Web, Data JPA, Security, Validation, Mail, Actuator) |
| Database | MySQL with Hibernate (JPA) |
| Views | Thymeleaf (server-rendered admin pages) |
| Build | Maven |

## Project Structure

```
src/main/java/Project/CinnamonProducts/
├── controller/   REST and web controllers (products, users, cart, checkout, payment, reviews, contact)
├── service/      Business logic
├── repository/   Spring Data JPA repositories
├── model/        JPA entities (Product, User, Review, Contact, Cart)
├── entity/       Payment entities and enums
├── dto/          Request and response objects
└── config/       Security and payment configuration
```

## Getting Started

### Prerequisites
- Java 24 (JDK)
- Maven (or the included Maven wrapper)
- MySQL 8 running locally

### Run

1. Clone the repository:
   ```bash
   git clone https://github.com/NisalWeerakoon/topcinnamon-backend.git
   cd topcinnamon-backend
   ```
2. Set your MySQL username and password in `src/main/resources/application.properties`. The `cinnamon_products` database is created automatically on first run.
3. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The API runs at `http://localhost:8080`. Check the payment service at `http://localhost:8080/api/payment/health`.

## Main API Endpoints

| Area | Base path |
|---|---|
| Products | `/products` |
| Authentication & users | `/api/auth` |
| Cart | `/api/cart` |
| Payments | `/api/payment` |

## Team

| Member | Contribution |
|---|---|
| [Nisal Weerakoon](https://github.com/NisalWeerakoon) | Project setup, payment management, backend integration |
| [Ravindu Senanayake](https://github.com/Ravindu-S) | Store features, product management and admin dashboard |
