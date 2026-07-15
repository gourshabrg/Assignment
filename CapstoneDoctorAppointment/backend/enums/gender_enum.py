from enum import Enum


class Gender(str, Enum):
    """Allowed gender values for patient registration."""

    MALE = "MALE"
    FEMALE = "FEMALE"
    OTHER = "OTHER"
