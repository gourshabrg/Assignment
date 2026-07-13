from pydantic import BaseModel, Field

from enums.specialization_enum import Specialization


class DoctorSearchRequest(BaseModel):
    """All filters optional -- only the ones sent are applied."""

    name: str | None = None

    specialization: Specialization | None = None

    location: str | None = None

    min_experience: int | None = Field(
        default=None,
        ge=0
    )

    max_fee: float | None = Field(
        default=None,
        gt=0
    )
