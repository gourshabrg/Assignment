from pydantic import BaseModel, Field


class DoctorProfileUpdateRequest(BaseModel):
    """All fields optional -- only the ones sent get updated."""

    qualification: str | None = Field(
        default=None,
        min_length=2,
        max_length=100
    )

    consultation_fee: float | None = Field(
        default=None,
        gt=0
    )

    clinic_address: str | None = None
