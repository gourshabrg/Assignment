from enum import Enum


class AppointmentStatus(str, Enum):
    """Lifecycle states of a booked appointment."""

    BOOKED = "BOOKED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NO_SHOW = "NO_SHOW"
