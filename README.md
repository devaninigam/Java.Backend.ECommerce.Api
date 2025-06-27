# Java.Backend.ECommerce.Api

![Java](https://img.shields.io/badge/Java-21+-blue)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-orange)

---

### 🗓️ Overview

[`Java.Backend.ECommerce.Api`](https://javabackendecommerceapi-production.up.railway.app/api) is a full-fledged RESTful backend service built with **Spring Boot** and **Java**, crafted to power the core functionalities of an e-commerce platform. It handles everything behind the scenes—from user authentication and product management to secure order handling and cart operations.

---

### 🛠️ Tech Stack

* **Language**: Java 21+
* **Framework**: Spring Boot (modular, opinionated setup)
* **Security**: Spring Security with JWT (stateless token-based authentication)
* **Database**: PostgreSQL (RDBMS for storing structured data)
* **ORM**: Hibernate / JPA (for object-relational mapping)
* **Build Tool**: Maven
* **API Style**: RESTful with clean and consistent endpoints
* **Dev Tools**: Lombok, Validation, Swagger (optional for API docs)

---

### 🧹 Features

* 🔐 **User Auth & Role-Based Access**
  JWT-powered login/register flow, admin/user role segregation.

* 📦 **Product CRUD**
  Add, edit, delete, and fetch product data with validations.

* 💼 **Cart System**
  Add to cart, update quantities, view user-specific carts.

* 📃 **Order Management**
  Place orders, view order history, track status.

* 📿 **Category Support**
  Products grouped under categories for organized browsing.

* 📈 **Pagination & Filtering**
  Smart querying for large data sets.

* 🧑‍💻 **Exception Handling**
  Clean and consistent error responses using `@ControllerAdvice`.

---

### 🔧 Future Enhancements (Optional ideas)

* 📦 Inventory service or microservices split
* 🔔 Email notifications (via JavaMailSender)
* 📊 Admin dashboard with sales metrics

---

### 🔖 API Documentation

* 📄 [Swagger UI](https://javabackendecommerceapi-production.up.railway.app/swagger-ui/index.html)

---

### 🚀 Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/your-username/Java.Backend.ECommerce.Api.git
cd Java.Backend.ECommerce.Api

# 2. Configure your PostgreSQL DB in application.properties
#    src/main/resources/application.properties

# 3. Run the app
mvn spring-boot:run
```

---

### ✨ Notable Refactors

* ✅ **Removed unnecessary PasswordBCrypt autowiring**: Now using static utility methods to streamline the password encryption process and keep beans clean and lean.

---

> Made with ❤️ by [Nigam Devani](https://github.com/devaninigam/)
