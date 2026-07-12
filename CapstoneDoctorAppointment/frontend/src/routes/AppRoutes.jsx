import { Route, Routes } from "react-router-dom";
import ProtectedRoute from "../components/common/ProtectedRoute";
import HomePage from "../pages/HomePage";
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import ResetPasswordPage from "../pages/auth/ResetPasswordPage";
import DoctorSearchPage from "../pages/patient/DoctorSearchPage";
import DoctorDetailsPage from "../pages/patient/DoctorDetailsPage";
import PaymentPage from "../pages/patient/PaymentPage";
import MyAppointmentsPage from "../pages/patient/MyAppointmentsPage";
import NotFoundPage from "../pages/NotFoundPage";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />

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
          <ProtectedRoute>
            <PaymentPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/my-appointments"
        element={
          <ProtectedRoute>
            <MyAppointmentsPage />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};

export default AppRoutes;
