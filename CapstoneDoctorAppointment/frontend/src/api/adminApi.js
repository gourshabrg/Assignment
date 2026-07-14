import axiosInstance from "./axiosInstance";

export const getDashboard = () =>
  axiosInstance.get("/admin/dashboard");

export const listDoctors = () =>
  axiosInstance.get("/admin/doctors");

export const verifyDoctor = (doctorId) =>
  axiosInstance.patch(`/admin/doctors/${doctorId}/verify`);

export const rejectDoctor = (doctorId) =>
  axiosInstance.patch(`/admin/doctors/${doctorId}/reject`);

export const getCancellationRequests = () =>
  axiosInstance.get("/admin/cancellation-requests");

export const approveCancellation = (appointmentId) =>
  axiosInstance.patch(`/admin/cancellation-requests/${appointmentId}/approve`);

export const rejectCancellation = (appointmentId) =>
  axiosInstance.patch(`/admin/cancellation-requests/${appointmentId}/reject`);
