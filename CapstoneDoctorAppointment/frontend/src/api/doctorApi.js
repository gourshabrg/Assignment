import axiosInstance from "./axiosInstance";

export const searchDoctors = (params) =>
  axiosInstance.get("/doctors/search", { params });

export const getDoctorById = (doctorId) =>
  axiosInstance.get(`/doctors/${doctorId}`);
