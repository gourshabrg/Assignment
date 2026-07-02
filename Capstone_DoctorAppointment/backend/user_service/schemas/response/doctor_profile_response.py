from datetime import datetime

from pydantic import BaseModel


class DoctorProfileResponse(BaseModel):

    user_id: str

    full_name: str

    email: str

    phone: str

    qualification: str

    specialization: str

    experience: int

    license_number: str

    consultation_fee: float

    clinic_address: str

    is_active: bool

    created_at: datetime

    updated_at: datetime
