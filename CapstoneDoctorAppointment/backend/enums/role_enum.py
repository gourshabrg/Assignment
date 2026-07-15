from enum import Enum


class UserRole(str, Enum):
    """The three account types in the system."""

    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"
