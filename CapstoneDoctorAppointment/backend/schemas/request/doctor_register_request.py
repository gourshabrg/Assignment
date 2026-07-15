from pydantic import BaseModel, EmailStr, Field, field_validator

from enums.specialization_enum import Specialization
from utils.validators import validate_phone, validate_email_domain


class DoctorRegisterRequest(BaseModel):
    """Fields required to register as a doctor."""

    full_name: str = Field(
        min_length=2,
        max_length=100
    )

    email: EmailStr

    password: str = Field(
        min_length=8,
        max_length=12
    )

    phone: str = Field(
        min_length=10,
        max_length=10
    )

    qualification: str

    specialization: Specialization

    experience: int = Field(
        ge=0
    )

    license_number: str

    consultation_fee: float = Field(
        gt=0
    )

    clinic_address: str

    _validate_phone = field_validator("phone")(validate_phone)
    _validate_email_domain = field_validator("email")(validate_email_domain)
