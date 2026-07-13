from enum import Enum


class AppointmentStatus(str, Enum):
    """Lifecycle states of an appointment."""

    PENDING_PAYMENT = "PENDING_PAYMENT"
    BOOKED = "BOOKED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NOT_ATTENDED = "NOT_ATTENDED"
