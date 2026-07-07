from pydantic import BaseModel
from enums.specialization_enum import Specialization

class AdminDoctorResponse(BaseModel):

    doctor_id: str

    full_name: str

    email: str

    phone: str

    specialization: Specialization

    qualification: str

    consultation_fee: float

    is_active: bool
