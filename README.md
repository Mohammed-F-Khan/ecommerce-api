# 🛍️ Real Pheel – E-Commerce Backend API  
**Capstone Project | Java Spring Boot REST API**

---

## 📖 Project Overview

**Real Pheel** is a **Spring Boot–based REST API** built to support an e-commerce clothing store.

This project was completed as a **capstone assignment**, where the primary goal was **not** to build an application from scratch, but to:

- Understand an existing production-style codebase  
- Identify bugs and broken functionality  
- Fix real backend issues  
- Implement missing features using best practices  

The **front-end application was already provided and fully functional**.  
All required work was completed **entirely in the Java Spring Boot backend**, with a strong emphasis on:

- Debugging real-world issues  
- Implementing missing controllers and DAO logic  
- Enforcing role-based security  
- Ensuring seamless front-end and backend integration  

By the end of this project, the API fully supports:

- Product browsing and filtering  
- Category management  
- User authentication and authorization  
- Admin-only actions  

---

## 🎯 Project Goals

The main goals of this capstone were to:

- Understand and navigate an existing Spring Boot application  
- Fix bugs in product searching and product updates  
- Implement a fully functional `CategoriesController`  
- Secure admin-only endpoints using Spring Security  
- Test all functionality using Insomnia  
- Prepare the project for a live demo and presentation  

---

## 🧰 Technologies Used

- **Java**
- **Spring Boot**
- **Spring Security (JWT Authentication)**
- **JDBC**
- **MySQL**
- **Insomnia** (API testing)
- **GitHub Projects** (planning & task tracking)

---

## 🗄️ Database Setup

The database was created using the provided SQL script:


This script was executed in **MySQL Workbench** and sets up:

- All required database tables  
- Sample product data  
- Sample categories  
- Demo users for authentication testing  

### Demo Users

| Username | Role  |
|--------|-------|
| user   | USER  |
| admin  | ADMIN |
| george | USER  |

**Password for all demo users:**  


---

## 🔐 Authentication & Security

Authentication is handled using **JWT tokens**.

 Register Endpoint


`json

  "username": "admin",
  "password": "password",
  "confirmPassword": "password",
  "role": "ADMIN"
}
POST /login

{
  "username": "admin",
  "password": "password"
}


## 🔐 Role-Based Access Control

The API enforces strict role-based security using **Spring Security** and **JWT authentication**.

- **Public users** can browse products and categories  
- **Admin users only** can create, update, or delete products and categories  

This ensures data integrity and protects sensitive admin operations.

---

## 🧩 Phase 1 – CategoriesController Implementation

The `CategoriesController` class was initially provided but **completely unimplemented**.

### Work Completed
- Implemented all required controller endpoints  
- Implemented all corresponding DAO methods  
- Secured admin-only endpoints using Spring Security  

### Category Endpoints

| Method | Endpoint | Access |
|------|---------|--------|
| GET | `/categories` | Public |
| GET | `/categories/{id}` | Public |
| GET | `/categories/{id}/products` | Public |
| POST | `/categories` | Admin |
| PUT | `/categories/{id}` | Admin |
| DELETE | `/categories/{id}` | Admin |

---

## 🐞 Phase 2 – Bug Fixes

### 🐛 Bug 1: Product Search Returned Incorrect Results

The product search endpoint returned incorrect results when filters were applied.

#### Problems Identified
- Filters were not applied correctly together  
- Price ranges behaved inconsistently  
- Some filters were ignored depending on input  

#### Fixes Applied
- Rewrote the SQL search logic  
- Ensured all filters are **optional and combinable**  
- Verified filtering by:
  - Category  
  - Minimum price  
  - Maximum price  
  - Subcategory (color)  

All search combinations were tested using **Insomnia** and confirmed working.

---

### 🐛 Bug 2: Product Updates Created Duplicate Products

Admins reported that updating a product resulted in duplicate database records.

#### Root Cause
- The update endpoint incorrectly called the `create()` method instead of `update()`

#### Fix Applied
- Corrected the controller logic to call the proper update method  
- Verified updates modify existing records instead of inserting new ones  

This fix prevents database pollution and ensures safe admin updates.

---

## 🧪 API Testing (Insomnia)

All endpoints were manually tested using **Insomnia**.

### Tests Include
- User registration and login  
- JWT authentication validation  
- Category CRUD operations  
- Product filtering combinations  
- Product updates  
- Admin-only access enforcement  

✅ **All required tests are passing successfully**

---

## ⭐ Interesting Code Example

One of the most critical fixes was improving the product search SQL logic to support optional filters.

``java
String sql = "SELECT * FROM products " +
             "WHERE (category_id = ? OR ? = -1) " +
             "AND (price >= ? OR ? = -1) " +
             "AND (price <= ? OR ? = -1) " +
             "AND (subcategory = ? OR ? = '')";

### Why This Matters

This implementation ensures that product filtering behaves consistently and reliably:

- Filters work independently  
- Filters can be combined in any order  
- Results are accurate and predictable  

This approach improves both developer maintainability and the end-user shopping experience.

---

## 🖥️ Front-End Integration

Although the front end was not modified, the completed API fully supports the **Real Pheel** website.

### Supported Features
- Product browsing  
- Category filtering  
- Price range filtering  
- Color filtering  
- Shopping cart functionality  
- Checkout flow  
- Admin functionality via API  

---

## 📸 Screenshots

All screenshots are stored in the `/screenshots` directory.

### Required Screenshots
- `home-products.png` – Product listing page
<img width="1915" height="905" alt="image" src="https://github.com/user-attachments/assets/a7f6e84f-3836-4ecc-b095-e35869b14ea9" />

- `category-filter.png` – Category filter in action
<img width="425" height="428" alt="image" src="https://github.com/user-attachments/assets/01d45d28-c80d-4769-a107-6d457b64c43a" />

- `price-filter.png` – Price range filter applied
<img width="432" height="187" alt="image" src="https://github.com/user-attachments/assets/3716602d-f37a-44b9-b3ba-c84750460f81" />

- `cart.png` – Shopping cart view
<img width="353" height="107" alt="image" src="https://github.com/user-attachments/assets/63590e20-b3eb-4fba-81da-fc78c130fe5d" />

- `insomnia-tests.png` – Passing Insomnia tests
<img width="1917" height="1077" alt="image" src="https://github.com/user-attachments/assets/02f83066-65e0-4811-ba83-5356700c4862" />

- `admin-category.png` – Admin category creation
<img width="1918" height="1075" alt="image" src="https://github.com/user-attachments/assets/851355fd-a494-4821-bbc1-b880afe4e1aa" />


---

## 📋 GitHub Project Board

A **GitHub Project board** was used to manage:

- Bug fixes  
- Feature implementation  
- Testing  
- Documentation  

All tasks have been completed and moved to **Done**.
