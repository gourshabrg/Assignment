from pydantic import BaseModel


class AdminDoctorResponse(BaseModel):

    doctor_id: str

    full_name: str

    email: str

    phone: str

    specialization: str

    qualification: str

    consultation_fee: float

    is_active: bool
