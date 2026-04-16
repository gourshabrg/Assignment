# Spring Core Assignment - User Management System

## Project Overview

This is a **Spring Boot backend application** developed as part of a Java assignment.

The project demonstrates:

- Layered Architecture
- Dependency Injection (Constructor-based)
- REST APIs
- Use of Components
- Clean code structure

---

## Project Structure

```
controller/   → Handles API requests
service/      → Business logic
repository/   → Data handling (in-memory)
model/        → Data classes
component/    → Reusable logic
```

---

## Technologies Used

- Java 17
- Spring Boot
- Maven
- REST APIs

---

## Features Implemented

### 1. User Management System

| API         | Method | Description    |
| ----------- | ------ | -------------- |
| /users      | GET    | Get all users  |
| /users      | POST   | Create user    |
| /users/{id} | GET    | Get user by ID |

👉 Uses in-memory storage (no database)

---

### 2. Notification System

| API     | Method | Description       |
| ------- | ------ | ----------------- |
| /notify | GET    | Send notification |

Uses `@Component` for reusable logic

---

### 3. Message Formatter System

| API                      | Method | Description           |
| ------------------------ | ------ | --------------------- |
| /message?type=SHORT/LONG | GET    | Get formatted message |

Uses multiple components:

- ShortMessageFormatter
- LongMessageFormatter

---

## Key Concepts Used

- IoC (Inversion of Control)
- Dependency Injection
- Component Scanning
- Layered Architecture
- REST API Design

---

## ▶ How to Run

1. Open project in VS Code
2. Open terminal
3. Run:

```
./mvnw spring-boot:run
```

4. Server runs on:

```
http://localhost:8080
```

---

## 🧪 Testing APIs

Use Postman or browser:

- http://localhost:8080/users
- http://localhost:8080/notify
- http://localhost:8080/message?type=SHORT

---
