import axiosInstance from "./axiosInstance";

export const createSlot = (data) =>
  axiosInstance.post("/availability/slots", data);

export const getMySlots = () =>
  axiosInstance.get("/availability/myslots");

export const updateSlot = (slotId, data) =>
  axiosInstance.put(`/availability/slots/${slotId}`, data);

export const deleteSlot = (slotId) =>
  axiosInstance.delete(`/availability/slots/${slotId}`);
