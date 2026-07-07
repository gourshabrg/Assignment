from datetime import date

from pydantic import BaseModel, EmailStr, Field, field_validator

from enums.gender_enum import Gender
from utils.validators import validate_phone, validate_email_domain


class PatientRegisterRequest(BaseModel):
    """Fields required to register as a patient."""

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

    gender: Gender

    dob: date

    _validate_phone = field_validator("phone")(validate_phone)
    _validate_email_domain = field_validator("email")(validate_email_domain)
