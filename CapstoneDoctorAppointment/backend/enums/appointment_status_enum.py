from enum import Enum


class AppointmentStatus(str, Enum):
    """Lifecycle states of an appointment. PENDING_PAYMENT is the
    state right after booking -- the slot is reserved but the
    appointment only becomes BOOKED once payment completes.
    """

    PENDING_PAYMENT = "PENDING_PAYMENT"
    BOOKED = "BOOKED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NO_SHOW = "NO_SHOW"
