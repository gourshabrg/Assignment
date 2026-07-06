from datetime import date

from pydantic import BaseModel, EmailStr, Field


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

    gender: str

    dob: date
