# ✅ Demo Todo Management API

A secure RESTful API built with **Spring Boot**, **Kotlin**, and **MongoDB Atlas** for managing todos. This project includes full user authentication with JWT and provides CRUD operations for todos.

---

## 🚀 Features

- 🔐 JWT Authentication with secure token storage
- 🧾 User Registration & Login
- ✅ CRUD for Todos
- 🌍 MongoDB Atlas Integration
- 📦 Gradle for dependency management
- ⚙️ Spring Security + Spring Data MongoDB

---

## 🛠 Tech Stack

- Kotlin
- Spring Boot (Web, Security, Data MongoDB)
- JWT (JSON Web Token)
- MongoDB Atlas
- Gradle

---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- Kotlin
- Gradle
- MongoDB Atlas cluster

---

## 🔧 Configuration

On your configuration profile include these two environment varibles:



```
JWT_SECRET_BASE64=
MONGO_CNX=
````

---

## ▶️ Running the App

```bash
./gradlew bootRun
```

API will be available at: `http://localhost:8080`

---

## 🔐 Authentication

* **Register:** `POST /api/auth/register`
* **Login:** `POST /api/auth/login`
  ➤ On login, receive a JWT token. Use it in headers:

```
Authorization: Bearer <JWT-TOKEN>
```
* **Refresh Token:** `POST /api/auth/refresh`
  ➤ On refresh, use your Refresh Token. Use it in body:

```
Body: {"refreshToken":<YOUR_REFRESH_TOKEN>}
```

---

## 📋 API Endpoints

### Auth

| Method | Endpoint         | Description         |
| ------ | ---------------- | ------------------- |
| POST   | `/api/auth/register` | Create a new user   |
| POST   | `/api/auth/login`    | Login and get token |
| POST   | `/api/auth/login`    | Refresh token |

### Todos (Protected)

| Method | Endpoint      | Description           |
| ------ | ------------- | --------------------- |
| GET    | `/api/todos`      | List all user's todos |
| GET    | `/api/todos/by-owner`      | List owner user's todos |
| POST   | `/api/todos`      | Create a new todo     |
| GET    | `/api/todos/{id}` | Retrieve a todo       |
| PUT    | `/api/todos/{id}` | Update a todo         |
| PATCH | `/api/todos/mark-as-uncompleted/{id}` | Mark todo as uncompleted        |
| PATCH | `/api/todos/mark-as-completed/{id}` | Mark todo as completed        |
| DELETE | `/api/todos/{id}` | Delete a todo         |

---

## 📄 License

MIT License - see the [LICENSE](LICENSE) file for details.

---

