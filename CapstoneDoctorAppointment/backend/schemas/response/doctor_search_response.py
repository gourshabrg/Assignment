from pydantic import BaseModel

from enums.specialization_enum import Specialization

from schemas.response.availability_slot_response import (
    AvailabilitySlotResponse
)


class DoctorSearchResponse(BaseModel):

    doctor_id: str

    full_name: str

    qualification: str

    specialization: Specialization

    experience: int

    consultation_fee: float

    clinic_address: str


class DoctorDetailResponse(DoctorSearchResponse):

    available_slots: list[AvailabilitySlotResponse]
