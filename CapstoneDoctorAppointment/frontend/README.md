# Doctor Appointment Booking System - Frontend

React single page application for the patient, doctor and admin portals.

## Tech Stack

- React 19 with Vite
- React Router for routing
- Axios for API calls
- React Bootstrap for the UI
- React Hook Form for form validation
- React Toastify for notifications

## Prerequisites

- Node.js 20 or above
- The backend running on `http://127.0.0.1:8000`

## How to run

1. Copy `.env.example` to `.env` and set the API URL:

   ```
   VITE_API_BASE_URL=http://127.0.0.1:8000
   ```

2. Install the dependencies:

   ```
   npm install
   ```

3. Start the dev server:

   ```
   npm run dev
   ```

The app runs on `http://localhost:5173`.

## Scripts

| Command           | Description                  |
| ----------------- | ---------------------------- |
| `npm run dev`     | Start the development server |
| `npm run build`   | Build the production bundle  |
| `npm run preview` | Preview the production build |
| `npm run lint`    | Run ESLint over the project  |

## Folder Structure

```
src/
  api/          Axios instance and the API calls per module
  components/   Reusable UI components
  context/      Auth context provider
  hooks/        Custom hooks
  pages/        Route level pages grouped by role
  routes/       Route definitions and protected routes
  styles/       Global styles and CSS variables
  utils/        Constants, formatters and error helpers
```

## Roles

- **Patient** - search doctors, book a slot, pay and manage appointments
- **Doctor** - manage availability and appointments
- **Admin** - verify doctors, review cancellation requests and view the dashboard
