import axiosInstance from "./axiosInstance";

export const bookAppointment = (data) =>
  axiosInstance.post("/appointments/book", data);

export const payForAppointment = (appointmentId) =>
  axiosInstance.post(`/appointments/${appointmentId}/pay`);

export const getMyAppointments = () =>
  axiosInstance.get("/appointments/patient");

export const cancelAppointment = (appointmentId) =>
  axiosInstance.post(`/appointments/${appointmentId}/cancel`);
