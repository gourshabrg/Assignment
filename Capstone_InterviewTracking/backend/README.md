# Interview Tracking System — Backend

A Spring Boot REST API for managing end-to-end interview processes. HR teams post jobs, onboard candidates, schedule multi-round interviews, assign panel members, and collect structured feedback. Candidates apply online and track their application status. Panel members view their assigned interviews and submit feedback.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Environment Configuration](#environment-configuration)
- [API Overview](#api-overview)
- [Roles and Access Control](#roles-and-access-control)
- [Interview Stage Flow](#interview-stage-flow)
- [Running Tests](#running-tests)
- [Postman Collection](#postman-collection)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Persistence | Spring Data JPA + PostgreSQL |
| File Storage | Google Drive API v3 |
| Email | Spring Mail (SMTP / Gmail) |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven (Maven Wrapper included) |
| Test | JUnit 5 + Mockito |
| Coverage | JaCoCo (80% instruction coverage gate) |

---

## Architecture Overview

```
com.Capstone.InterviewTracking
├── config/          # CORS and data seeder configuration
├── constant/        # AppConstants — all API paths and email templates
├── controller/      # REST controllers (Auth, HR, Panel, Candidate, Jobs)
├── dto/             # Request and response DTOs
├── entity/          # JPA entities (User, Candidate, Application, Interview, etc.)
├── enums/           # Enums (RoleType, InterviewStage, InterviewRound, etc.)
├── exception/       # Custom exceptions and GlobalExceptionHandler
├── mapper/          # DTO-to-entity mappers
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JwtUtil, CustomUserDetailsService, SecurityConfig
├── service/         # Service interfaces and implementations
└── util/            # FileValidationUtil (resume upload validation)
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17 or above |
| PostgreSQL | 13 or above |
| Maven | 3.8+ (or use the included `mvnw` wrapper) |
| Google Cloud Project | With Drive API enabled |
| Gmail Account | With App Password configured for SMTP |

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Capstone_InterviewTracking/backend/InterviewTracking
```

### 2. Create the PostgreSQL Database

Connect to PostgreSQL and create the database:

```sql
CREATE DATABASE interview_tracking;
```

The schema is auto-created by Hibernate on first startup (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Configure `application.properties`

Open `src/main/resources/application.properties` and update the following values:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/interview_tracking
spring.datasource.username=<your-postgres-username>
spring.datasource.password=<your-postgres-password>

# JWT — must be at least 32 characters
jwt.secret=change-this-jwt-secret-key-minimum-32-chars
jwt.expiration-ms=86400000

# Gmail SMTP (use an App Password, not your Gmail login password)
spring.mail.username=<your-gmail-address>
spring.mail.password=<your-gmail-app-password>

# Frontend base URL for set-password email link
# Update if your frontend runs on a different host/port
app.cors.allowed-origins=http://127.0.0.1:5500,http://localhost:5500
```

#### How to generate a Gmail App Password

1. Go to your Google Account → Security → 2-Step Verification (must be enabled)
2. Scroll down to **App passwords**
3. Select app: Mail, device: Other → enter "Interview Tracking"
4. Copy the 16-character password and paste it into `spring.mail.password`

### 4. Configure Google Drive (Resume Storage)

The application stores candidate resumes in a Google Drive folder.

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project (or select an existing one)
3. Enable **Google Drive API**
4. Create a **Service Account** and download the JSON key file
5. Share your target Drive folder with the service account email (Editor access)
6. Place the JSON key file at: `src/main/resources/credentials.json`
7. Update the folder ID in `AppConstants.java`:

```java
public static final String DRIVE_FOLDER_ID = "<your-google-drive-folder-id>";
```

The folder ID is the last part of the folder URL:
`https://drive.google.com/drive/folders/`**`1XT5Us7SJseIH-MyiKzLsuS17on7c_tZn`**

### 5. Seed Initial HR User

On startup, `DataSeeder` automatically creates a default HR user if none exists:

| Field | Default Value |
|---|---|
| Email | `hr@company.com` |
| Password | `Password@123` |
| Role | `HR` |

You can change these defaults in `DataSeeder.java` before first run.

---

## Running the Application

### Using Maven Wrapper (recommended — no Maven installation needed)

**Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

### Using Maven (if installed globally)

```bash
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

---

## Environment Configuration

| Property | Description | Default |
|---|---|---|
| `server.port` | HTTP port | `8080` |
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/interview_tracking` |
| `spring.datasource.username` | DB username | `postgres` |
| `spring.datasource.password` | DB password | `123456789` |
| `jwt.secret` | JWT signing key (min 32 chars) | `change-this-jwt-secret-key-minimum-32-chars` |
| `jwt.expiration-ms` | Token TTL in milliseconds | `86400000` (24 hours) |
| `spring.mail.username` | Gmail address for sending emails | — |
| `spring.mail.password` | Gmail App Password | — |
| `spring.servlet.multipart.max-file-size` | Max resume file size | `5MB` |
| `spring.servlet.multipart.max-request-size` | Max multipart request size | `10MB` |
| `app.cors.allowed-origins` | Comma-separated allowed CORS origins | `http://127.0.0.1:5500,http://localhost:5500` |

---

## API Overview

All responses follow a standard envelope:

```json
{
  "success": true,
  "message": "Operation result message",
  "data": { },
  "errors": []
}
```

### Authentication — `/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/signup` | Public | Register a new candidate; sends verification email |
| POST | `/auth/set-password` | Public | Set password using token from email |
| POST | `/auth/login` | Public | Login and receive JWT token |

### Jobs — `/jobs`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/jobs` | Public | List all active job descriptions |
| GET | `/jobs/{id}` | Public | Get a single job by ID |
| GET | `/jobs/hr` | HR | List all jobs including inactive ones |
| POST | `/jobs` | HR | Create a new job description |
| PUT | `/jobs/{id}` | HR | Update a job description |
| PUT | `/jobs/{id}/toggle` | HR | Toggle job active/inactive status |
| DELETE | `/jobs/{id}` | HR | Permanently delete a job |

### Candidate — `/candidate`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/candidate/apply` | Public | Submit a job application with resume (multipart) |
| POST | `/hr/candidates` | HR | HR creates a candidate profile directly (multipart) |
| GET | `/candidate/my-application` | Candidate | View own application status and interview history |

### HR — Candidates — `/hr`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/hr/candidates` | HR | List all applications (filter by stage, status, jobId) |
| GET | `/hr/candidates/{applicationId}` | HR | Full candidate profile with interview history |
| PUT | `/hr/applications/{applicationId}/stage` | HR | Advance candidate to next interview stage |
| PUT | `/hr/applications/{applicationId}/reject` | HR | Reject a candidate's application |
| PUT | `/hr/applications/{applicationId}/select` | HR | Mark candidate as finally selected (HR stage only) |

### HR — Panels & Interviews — `/hr`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/hr/create-panel` | HR | Create a panel member account; sends verification email |
| GET | `/hr/panels` | HR | List all registered panel members |
| POST | `/hr/interviews/schedule` | HR | Schedule an interview for any round |
| GET | `/hr/interviews` | HR | List all HR-round interviews |
| GET | `/hr/interviews/{interviewId}/candidate` | HR | Get candidate detail for an HR-round interview |
| POST | `/hr/interviews/{interviewId}/feedback` | HR | Submit HR feedback for a completed HR interview |
| GET | `/hr/interviews/{interviewId}/feedback` | HR | Get all feedback for an interview |

### Panel — `/panel`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/panel/interviews` | Panel | View all interviews assigned to the logged-in panel member |
| GET | `/panel/interviews/{interviewId}/candidate` | Panel | View candidate profile for an assigned interview |
| POST | `/panel/interviews/{interviewId}/feedback` | Panel | Submit feedback for an assigned interview |

---

## Roles and Access Control

| Role | Registration | Access |
|---|---|---|
| `CANDIDATE` | Self-registration via `/auth/signup` | Apply for jobs, view own application |
| `HR` | Seeded by `DataSeeder` on startup | Full HR dashboard access |
| `PANEL` | Created by HR via `/hr/create-panel` | View assigned interviews, submit feedback |

JWT token is passed in every protected request as:

```
Authorization: Bearer <token>
```

---

## Interview Stage Flow

Candidates progress through stages **sequentially** — skipping stages is not allowed:

```
PROFILING → SCREENING → L1 → L2 → HR
```

- HR advances a stage using `PUT /hr/applications/{id}/stage`
- HR can **reject** a candidate at any stage
- **Final selection** (`PUT /hr/applications/{id}/select`) is only allowed at the `HR` stage
- Once REJECTED or SELECTED, the stage cannot be updated

### Available Enums

**InterviewStage:** `PROFILING`, `SCREENING`, `L1`, `L2`, `HR`

**InterviewRound:** `SCREENING`, `L1`, `L2`, `HR`

**ApplicationStatus:** `APPLIED`, `SELECTED`, `REJECTED`

**FeedbackStatus:** `SELECTED`, `REJECTED`

**JobType:** `FULL_TIME`, `CONTRACT`, `REMOTE`

---

## Running Tests

### Run all tests

```cmd
.\mvnw.cmd test
```

### Run tests with JaCoCo coverage report

```cmd
.\mvnw.cmd verify
```

The HTML coverage report is generated at:

```
target/site/jacoco/index.html
```

Open it in a browser to view per-class and per-method coverage.

### Coverage gate

The JaCoCo plugin enforces a minimum of **80% instruction coverage**. The build fails if coverage drops below this threshold.

Current test suite: **156 tests, 0 failures**.

---

## Postman Collection

The Postman collection is located at:

```
InterviewTracking.postman_collection.json
```

### Import steps

1. Open Postman
2. Click **Import** → select `InterviewTracking.postman_collection.json`
3. The collection includes 4 collection variables:

| Variable | Description |
|---|---|
| `BASE_URL` | API base URL (default: `http://localhost:8080`) |
| `HR_TOKEN` | Auto-populated when you run **Login (HR)** |
| `CANDIDATE_TOKEN` | Auto-populated when you run **Login (Candidate)** |
| `PANEL_TOKEN` | Auto-populated when you run **Login (Panel)** |

### Recommended test flow

1. **Login (HR)** — token saved automatically to `HR_TOKEN`
2. **Create Job** — note the returned job ID
3. **Create Candidate by HR** or **Apply for Job (Self)**
4. **Get All Candidates** — note the `applicationId`
5. **Update Application Stage** — advance from PROFILING → SCREENING → L1 → ...
6. **Create Panel Member** — sends verification email
7. Set password via **Set Password** using the token from the email
8. **Login (Panel)** — token saved automatically to `PANEL_TOKEN`
9. **Schedule Interview** — assign panelIds
10. **Get My Interviews** (as panel) — view assigned interviews
11. **Submit Feedback** (as panel)
12. **Schedule HR Round Interview** — set `"round": "HR"`
13. **Get All HR Round Interviews**
14. **Get Candidate Detail for HR Interview**
15. **Submit HR Feedback**
16. **Select Candidate (Final)**
