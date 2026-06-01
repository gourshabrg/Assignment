# Interview Tracking System — Frontend

A vanilla HTML/CSS/JavaScript single-page-style frontend for the Interview Tracking System. It supports three roles — HR, Panel, and Candidate — each with their own dashboard and feature set.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Frontend](#running-the-frontend)
- [Configuration](#configuration)
- [Pages and Features](#pages-and-features)
- [Role-Based Access](#role-based-access)
- [API Integration](#api-integration)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Markup | HTML5 |
| Styling | CSS3 (custom, no framework) |
| Logic | Vanilla JavaScript (ES Modules) |
| HTTP | Fetch API (custom wrapper) |
| Auth | JWT stored in localStorage |
| Dev Server | VS Code Live Server (or any static server) |

---

## Project Structure

```
frontend/
├── pages/
│   ├── index.html                  # Home / landing page
│   ├── auth/
│   │   ├── login.html              # Login for all roles
│   │   ├── signup.html             # Candidate self-registration
│   │   └── set-password.html       # Set password via email token
│   ├── candidate/
│   │   ├── apply.html              # Apply for a job
│   │   └── dashboard.html          # Candidate: view application status
│   ├── hr/
│   │   ├── dashboard.html          # HR: candidates overview
│   │   ├── jobs.html               # HR: manage job descriptions
│   │   ├── panels.html             # HR: manage panel members
│   │   ├── hr-interviews.html      # HR: view and manage HR-round interviews
│   │   └── schedule.html           # HR: schedule interviews
│   └── panel/
│       ├── dashboard.html          # Panel: view assigned interviews
│       └── profiling.html          # Panel: view candidate profile & submit feedback
├── scripts/
│   ├── config/
│   │   └── site-config.js          # API base URL and all endpoint paths
│   ├── api/
│   │   ├── fetch-handler.js        # Central Fetch wrapper (auth headers, error handling)
│   │   ├── auth-api.js             # Auth API calls (login, signup, set-password)
│   │   ├── job-api.js              # Job API calls
│   │   ├── candidate-api.js        # Candidate API calls
│   │   ├── hr-api.js               # HR API calls
│   │   └── panel-api.js            # Panel API calls
│   ├── utils/
│   │   ├── storage.js              # localStorage helpers (token, role, user)
│   │   ├── validation.js           # Form validation helpers
│   │   └── dom.js                  # DOM utility helpers
│   ├── main/
│   │   ├── auth-controller.js      # Login / signup / set-password logic
│   │   ├── home.js                 # Landing page logic
│   │   ├── candidate-apply.js      # Apply page logic
│   │   ├── candidate-dashboard.js  # Candidate dashboard logic
│   │   ├── panel-dashboard.js      # Panel dashboard logic
│   │   ├── panel-profiling.js      # Panel candidate profiling logic
│   │   └── hr/
│   │       ├── sidebar.js          # HR sidebar navigation
│   │       ├── jobs.js             # HR jobs management logic
│   │       ├── panels.js           # HR panel management logic
│   │       ├── candidates.js       # HR candidates list logic
│   │       ├── schedule.js         # HR schedule interview logic
│   │       └── hr-interviews.js    # HR interview management logic
│   └── scripts.js                  # Shared entry point / bootstrap
├── styles/
│   ├── style.css                   # Global styles
│   ├── home.css                    # Landing page styles
│   ├── dashboard.css               # Shared dashboard styles
│   ├── auth/
│   │   └── auth.css                # Login / signup / set-password styles
│   ├── candidate/
│   │   ├── apply.css               # Apply page styles
│   │   └── dashboard.css           # Candidate dashboard styles
│   ├── hr/
│   │   └── dashboard.css           # HR dashboard styles
│   └── panel/
│       ├── dashboard.css           # Panel dashboard styles
│       └── profiling.css           # Panel profiling page styles
└── .gitignore
```

---

## Prerequisites

| Tool | Notes |
|---|---|
| Modern browser | Chrome, Firefox, or Edge (ES Module support required) |
| VS Code | Recommended — install the **Live Server** extension |
| Backend running | The Spring Boot backend must be running on `http://localhost:8080` |

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Capstone_InterviewTracking/frontend
```

### 2. Start the Backend

Make sure the Spring Boot backend is running before opening the frontend. See the [backend README](../backend/README.md) for setup instructions.

### 3. (Optional) Update the API URL

If your backend runs on a different host or port, open [scripts/config/site-config.js](scripts/config/site-config.js) and update:

```js
export const SITE_CONFIG = {
  API_URL: "http://localhost:8080",  // change this if needed
  ...
};
```

---

## Running the Frontend

### Using VS Code Live Server (recommended)

1. Open the `frontend/` folder in VS Code
2. Install the **Live Server** extension (if not already installed)
3. Right-click `pages/index.html` → **Open with Live Server**
4. The app opens at `http://127.0.0.1:5500/pages/index.html`

### Using any static file server

```bash
# Python 3
python -m http.server 5500

# Node.js (npx)
npx serve .
```

Then navigate to `http://localhost:5500/pages/index.html`.

> **Note:** The backend's CORS policy allows `http://127.0.0.1:5500` and `http://localhost:5500` by default.

---

## Configuration

All API endpoints are centrally defined in [scripts/config/site-config.js](scripts/config/site-config.js):

| Config Key | Default Value | Description |
|---|---|---|
| `API_URL` | `http://localhost:8080` | Backend base URL |
| `ENDPOINTS_AUTH` | `/auth/login`, `/auth/signup`, `/auth/set-password` | Auth endpoints |
| `ENDPOINTS_JOBS` | `/jobs` | Public job listing |
| `ENDPOINTS_CANDIDATE` | `/candidate/apply`, `/candidate/my-application` | Candidate endpoints |
| `ENDPOINTS_HR` | `/hr/...` | All HR management endpoints |
| `ENDPOINTS_PANEL` | `/panel/...` | Panel member endpoints |

---

## Pages and Features

### Public Pages

| Page | Path | Description |
|---|---|---|
| Home | `pages/index.html` | Landing page with job listings |
| Login | `pages/auth/login.html` | Login for all roles (HR, Panel, Candidate) |
| Sign Up | `pages/auth/signup.html` | Candidate self-registration |
| Set Password | `pages/auth/set-password.html` | Set password via emailed token link |
| Apply | `pages/candidate/apply.html` | Apply for a job (with resume upload) |

### Candidate Pages

| Page | Path | Description |
|---|---|---|
| Dashboard | `pages/candidate/dashboard.html` | View application status and interview history |

### HR Pages

| Page | Path | Description |
|---|---|---|
| Dashboard | `pages/hr/dashboard.html` | Candidate list with stage/status filters |
| Jobs | `pages/hr/jobs.html` | Create, edit, toggle, and delete job postings |
| Panels | `pages/hr/panels.html` | Create panel member accounts |
| Schedule | `pages/hr/schedule.html` | Schedule interviews for candidates |
| HR Interviews | `pages/hr/hr-interviews.html` | View HR-round interviews and submit feedback |

### Panel Pages

| Page | Path | Description |
|---|---|---|
| Dashboard | `pages/panel/dashboard.html` | View interviews assigned to the logged-in panel member |
| Profiling | `pages/panel/profiling.html` | View candidate profile and submit interview feedback |

---

## Role-Based Access

After login, the JWT token and role are stored in `localStorage`. Each page checks the stored role on load and redirects unauthorized users to the login page.

| Role | Entry Point After Login | Pages Accessible |
|---|---|---|
| `CANDIDATE` | `pages/candidate/dashboard.html` | Dashboard, Apply |
| `HR` | `pages/hr/dashboard.html` | All HR pages |
| `PANEL` | `pages/panel/dashboard.html` | Panel dashboard, Profiling |

---

## API Integration

All HTTP requests are routed through [scripts/api/fetch-handler.js](scripts/api/fetch-handler.js), which:

- Automatically attaches the `Authorization: Bearer <token>` header for protected routes
- Parses JSON responses
- Handles common HTTP error codes

Role-specific API modules:

| Module | File | Covers |
|---|---|---|
| Auth | `scripts/api/auth-api.js` | Login, signup, set-password |
| Jobs | `scripts/api/job-api.js` | Public and HR job endpoints |
| Candidate | `scripts/api/candidate-api.js` | Apply, view application |
| HR | `scripts/api/hr-api.js` | All HR management endpoints |
| Panel | `scripts/api/panel-api.js` | Panel interview and feedback endpoints |
