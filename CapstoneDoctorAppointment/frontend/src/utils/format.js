import { SPECIALIZATION_OPTIONS } from "./constants";

export const formatSpecialization = (value) => {
  const match = SPECIALIZATION_OPTIONS.find((option) => option.value === value);

  return match ? match.label : value;
};

export const formatTime = (value) => {
  const [hours, minutes] = value.split(":");
  const hour = Number(hours);
  const suffix = hour >= 12 ? "PM" : "AM";
  const displayHour = hour % 12 || 12;

  return `${displayHour}:${minutes} ${suffix}`;
};

export const formatDate = (value) =>
  new Date(value).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric"
  });

export const formatFee = (value) => `₹${Number(value).toFixed(2)}`;

// Some names are already stored with a "Dr" prefix, so only add one when
// it is missing.
export const formatDoctorName = (fullName = "") => {
  const name = fullName.trim();

  return /^dr\.?\s/i.test(name) ? name : `Dr ${name}`;
};

export const formatStatus = (status) =>
  status
    .split("_")
    .map((word) => word[0] + word.slice(1).toLowerCase())
    .join(" ");

export const getInitials = (fullName) =>
  fullName
    .split(" ")
    .map((part) => part[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
