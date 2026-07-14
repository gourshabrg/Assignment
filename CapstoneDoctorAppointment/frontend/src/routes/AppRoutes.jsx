import { Route, Routes } from "react-router-dom";
import ProtectedRoute from "../components/common/ProtectedRoute";
import GuestRoute from "../components/common/GuestRoute";
import HomePage from "../pages/HomePage";
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import ResetPasswordPage from "../pages/auth/ResetPasswordPage";
import ChangePasswordPage from "../pages/auth/ChangePasswordPage";
import DoctorProfilePage from "../pages/doctor/DoctorProfilePage";
import DoctorSearchPage from "../pages/patient/DoctorSearchPage";
import DoctorDetailsPage from "../pages/patient/DoctorDetailsPage";
import PaymentPage from "../pages/patient/PaymentPage";
import MyAppointmentsPage from "../pages/patient/MyAppointmentsPage";
import DoctorDashboardPage from "../pages/doctor/DoctorDashboardPage";
import ManageAvailabilityPage from "../pages/doctor/ManageAvailabilityPage";
import DoctorAppointmentsPage from "../pages/doctor/DoctorAppointmentsPage";
import AdminDashboardPage from "../pages/admin/AdminDashboardPage";
import AdminDoctorsPage from "../pages/admin/AdminDoctorsPage";
import AdminCancellationsPage from "../pages/admin/AdminCancellationsPage";
import NotFoundPage from "../pages/NotFoundPage";
import { ROLES } from "../utils/constants";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route
        path="/login"
        element={
          <GuestRoute>
            <LoginPage />
          </GuestRoute>
        }
      />
      <Route
        path="/register"
        element={
          <GuestRoute>
            <RegisterPage />
          </GuestRoute>
        }
      />
      <Route
        path="/reset-password"
        element={
          <GuestRoute>
            <ResetPasswordPage />
          </GuestRoute>
        }
      />
      <Route
        path="/change-password"
        element={
          <ProtectedRoute>
            <ChangePasswordPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/doctors"
        element={
          <ProtectedRoute>
            <DoctorSearchPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/doctors/:doctorId"
        element={
          <ProtectedRoute>
            <DoctorDetailsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/payment/:appointmentId"
        element={
          <ProtectedRoute allowedRoles={[ROLES.PATIENT]}>
            <PaymentPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/my-appointments"
        element={
          <ProtectedRoute allowedRoles={[ROLES.PATIENT]}>
            <MyAppointmentsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/doctor/dashboard"
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorDashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/doctor/profile"
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorProfilePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/doctor/availability"
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <ManageAvailabilityPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/doctor/appointments"
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorAppointmentsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminDashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/doctors"
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminDoctorsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/cancellations"
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminCancellationsPage />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};

export default AppRoutes;
