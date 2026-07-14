import axiosInstance from "./axiosInstance";

export const registerPatient = (data) =>
  axiosInstance.post("/auth/register/patient", data);

export const registerDoctor = (data) =>
  axiosInstance.post("/auth/register/doctor", data);

export const login = (data) =>
  axiosInstance.post("/auth/login", data);

export const resetPassword = (data) =>
  axiosInstance.post("/auth/reset-password", data);

export const getProfile = () =>
  axiosInstance.get("/auth/profile");

export const changePassword = (data) =>
  axiosInstance.post("/auth/change-password", data);
