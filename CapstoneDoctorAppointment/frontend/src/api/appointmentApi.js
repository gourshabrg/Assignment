import axiosInstance from "./axiosInstance";

export const bookAppointment = (data) =>
  axiosInstance.post("/appointments/book", data);

export const payForAppointment = (appointmentId) =>
  axiosInstance.post(`/appointments/${appointmentId}/pay`);

export const getMyAppointments = () =>
  axiosInstance.get("/appointments/patient");

export const cancelAppointment = (appointmentId) =>
  axiosInstance.post(`/appointments/${appointmentId}/cancel`);

export const getDoctorAppointments = () =>
  axiosInstance.get("/appointments/doctor");

export const updateAppointmentStatus = (appointmentId, data) =>
  axiosInstance.patch(`/appointments/${appointmentId}/status`, data);

export const requestCancellation = (appointmentId, data) =>
  axiosInstance.post(
    `/appointments/${appointmentId}/request-cancellation`,
    data
  );
