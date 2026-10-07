# User Management API with Spring Security

A Spring Boot REST API demonstrating role-based access control, audit logging, rate limiting, and event-driven architecture.

## Features

- **Role-based access control** with three roles:
  - `USER` -- view own profile
  - `MANAGER` -- access reports and user summaries
  - `ADMIN` -- manage users, change roles, delete accounts, view audit log
- **User registration** with Bean Validation (email, password length)
- **Rate limiting** on registration endpoint (Bucket4j -- 5 requests/minute per IP)
- **Audit logging** -- tracks user registration, role changes, and deletions
- **Event-driven architecture** -- Spring Application Events for user lifecycle (registered, role changed, deleted)
- **Caching** -- in-memory cache for user lists and summaries with automatic eviction on mutations
- **Global exception handling** -- consistent error responses for validation, not found, conflict, rate limit
- **Seed data** -- loads demo users from `users.json` on startup
- **CORS configured** for Angular frontend integration

## Tech Stack

- Java 17
- Spring Boot 4.0
- Spring Security (HTTP Basic + role-based authorization)
- Spring Data JPA / Hibernate
- PostgreSQL
- Bucket4j (rate limiting)
- Bean Validation (Jakarta)
- Lombok
- Maven

## Prerequisites

- Java 17+
- PostgreSQL running locally
- A database created:
  ```sql
  CREATE DATABASE security_db;
  ```

## Getting Started

```bash
git clone https://github.com/oksanajonaite/user-security-api.git
cd user-security-api
```

Set your database credentials:
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

Run the application:
```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

## API Endpoints

### Public
| Method | Endpoint             | Description                |
|--------|----------------------|----------------------------|
| POST   | `/api/auth/register` | Register a new user (rate limited) |

### Authenticated (USER, ADMIN)
| Method | Endpoint        | Description             |
|--------|-----------------|-------------------------|
| GET    | `/api/user/me`  | Get current user profile |
| GET    | `/api/users`    | List users (admin sees all, user sees self) |

### Manager
| Method | Endpoint                  | Description        |
|--------|---------------------------|--------------------|
| GET    | `/api/reports/users-summary` | User count by role |

### Admin
| Method | Endpoint                   | Description          |
|--------|----------------------------|----------------------|
| PUT    | `/api/admin/users/{id}/role` | Change user role    |
| DELETE | `/api/admin/users/{id}`    | Delete a user        |
| GET    | `/api/admin/audit`         | View audit log       |

## Demo Users

All demo users have password: `demo`

| Username | Role    |
|----------|---------|
| admin    | ADMIN   |
| manager  | MANAGER |
| user     | USER    |
| jonas    | USER    |
| petras   | USER    |
| laura    | USER    |
| egle     | USER    |
| tomas    | ADMIN   |

## Usage Examples

Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "secret123", "email": "new@example.com"}'
```

Get your profile:
```bash
curl -u admin:demo http://localhost:8080/api/user/me
```

Change a user's role (admin only):
```bash
curl -u admin:demo -X PUT "http://localhost:8080/api/admin/users/3/role?role=MANAGER"
```

View audit log (admin only):
```bash
curl -u admin:demo http://localhost:8080/api/admin/audit
```

## Configuration

| Environment Variable | Default       | Description          |
|---------------------|---------------|----------------------|
| `DB_NAME`           | `security_db` | PostgreSQL database  |
| `DB_USERNAME`       | `postgres`    | Database username    |
| `DB_PASSWORD`       | *(empty)*     | Database password    |
| `APP_SEED_PASSWORD` | `demo`        | Password for seeded users |

## Related

This API has a companion [Angular frontend](https://github.com/oksanajonaite/user-management-frontend) with login, registration, user list, and audit log views.
