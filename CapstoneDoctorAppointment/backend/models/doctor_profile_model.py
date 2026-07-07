from datetime import datetime

from beanie import Document
from pydantic import Field

from enums.specialization_enum import Specialization


class DoctorProfile(Document):
    """MongoDB 'doctor_profiles' collection -- the doctor-specific
    details that don't belong on the shared User document.
    """

    user_id: str

    qualification: str = Field(
        min_length=2,
        max_length=100
    )

    specialization: Specialization

    experience: int = Field(
        ge=0
    )

    license_number: str

    consultation_fee: float = Field(
        gt=0
    )

    clinic_address: str

    created_at: datetime = Field(
        default_factory=datetime.utcnow
    )

    updated_at: datetime = Field(
        default_factory=datetime.utcnow
    )

    class Settings:
        name = "doctor_profiles"
