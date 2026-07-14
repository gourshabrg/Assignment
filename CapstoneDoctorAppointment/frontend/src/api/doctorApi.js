import axiosInstance from "./axiosInstance";

export const searchDoctors = (params) =>
  axiosInstance.get("/doctors/search", { params });

export const getDoctorById = (doctorId) =>
  axiosInstance.get(`/doctors/${doctorId}`);

export const getMyProfile = () =>
  axiosInstance.get("/doctors/profile");

export const updateMyProfile = (data) =>
  axiosInstance.put("/doctors/profile", data);
