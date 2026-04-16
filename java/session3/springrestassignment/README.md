# Spring And REST Assignment

## Project Overview

This project is a simple Spring Boot REST API application developed as part of the assignment.  
It demonstrates basic Spring concepts like IoC, Dependency Injection, and layered architecture.

The application manages dummy user data (in-memory) and provides APIs to search, add, and delete users.

---

## Tech Stack

- Java 17
- Spring Boot
- Maven
- REST APIs

---

## Project Structure

SpringAndRestAssignment
│
├── controller
├── service
├── repository
├── model
├── exception

The project follows:
Controller → Service → Repository

---

## Features Implemented

### 1. Search Users API

- Endpoint: GET /users/search
- Supports optional parameters:
  - name
  - age
  - role

Example:
/users/search?name=Priya
/users/search?age=30
/users/search?role=USER
/users/search?age=30&role=USER

If no parameter → returns all users  
 Case-insensitive match for name and role  
 Exact match for age

---

### 2. Submit Data API

- Endpoint: POST /users/submit
- Accepts JSON input using @RequestBody

Example Request:
{
"name": "Ravi",
"age": 22,
"role": "USER"
}

Returns:

- 201 Created → success
- 400 Bad Request → invalid input

---

### 3. Delete User API

- Endpoint: DELETE /users/{id}
- Requires confirmation parameter

Example:
/users/1?confirm=true

If confirm=false or missing → "Confirmation required"  
 Deletes only when confirm=true

---

## Concepts Used

- Inversion of Control (IoC)
- Dependency Injection (Constructor-based)
- REST API Development
- Layered Architecture
- Exception Handling
- RequestParam and RequestBody

---

## How to Run

### Using VS Code / IntelliJ

1. Open the project
2. Run SpringRestAssignmentApplication.java

### Using Terminal

mvn spring-boot:run

---

## API Testing

You can test APIs using:

- Browser (for GET)
- Postman (for POST & DELETE)

---

## Notes

- No database is used (in-memory data)
- Code follows clean structure and separation of concerns
- Minimum required APIs are implemented as per assignment
