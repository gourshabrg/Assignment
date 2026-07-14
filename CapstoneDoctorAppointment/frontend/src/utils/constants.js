export const TOKEN_STORAGE_KEY = "access_token";

export const ROLES = {
  PATIENT: "PATIENT",
  DOCTOR: "DOCTOR",
  ADMIN: "ADMIN"
};

export const APPOINTMENT_STATUS = {
  PENDING_PAYMENT: "PENDING_PAYMENT",
  BOOKED: "BOOKED",
  CANCELLATION_REQUESTED: "CANCELLATION_REQUESTED",
  CANCELLED: "CANCELLED",
  COMPLETED: "COMPLETED",
  NOT_ATTENDED: "NOT_ATTENDED"
};

// Tabs on the appointments page, mapped to the statuses they show.
export const APPOINTMENT_TABS = [
  {
    key: "upcoming",
    label: "Upcoming",
    statuses: [
      APPOINTMENT_STATUS.PENDING_PAYMENT,
      APPOINTMENT_STATUS.BOOKED,
      APPOINTMENT_STATUS.CANCELLATION_REQUESTED
    ]
  },
  {
    key: "completed",
    label: "Completed",
    statuses: [
      APPOINTMENT_STATUS.COMPLETED,
      APPOINTMENT_STATUS.NOT_ATTENDED
    ]
  },
  {
    key: "cancelled",
    label: "Cancelled",
    statuses: [APPOINTMENT_STATUS.CANCELLED]
  }
];

export const GENDER_OPTIONS = [
  { value: "MALE", label: "Male" },
  { value: "FEMALE", label: "Female" },
  { value: "OTHER", label: "Other" }
];

export const SPECIALIZATION_OPTIONS = [
  { value: "GENERAL_PHYSICIAN", label: "General Physician" },
  { value: "CARDIOLOGIST", label: "Cardiologist" },
  { value: "DERMATOLOGIST", label: "Dermatologist" },
  { value: "DENTIST", label: "Dentist" },
  { value: "NEUROLOGIST", label: "Neurologist" },
  { value: "ORTHOPEDIC", label: "Orthopedic" },
  { value: "PEDIATRICIAN", label: "Pediatrician" },
  { value: "GYNECOLOGIST", label: "Gynecologist" },
  { value: "ENT_SPECIALIST", label: "ENT Specialist" },
  { value: "PSYCHIATRIST", label: "Psychiatrist" },
  { value: "OPHTHALMOLOGIST", label: "Ophthalmologist" },
  { value: "UROLOGIST", label: "Urologist" },
  { value: "GASTROENTEROLOGIST", label: "Gastroenterologist" },
  { value: "PULMONOLOGIST", label: "Pulmonologist" },
  { value: "ENDOCRINOLOGIST", label: "Endocrinologist" }
];
