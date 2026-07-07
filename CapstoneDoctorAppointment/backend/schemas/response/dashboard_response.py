from pydantic import BaseModel
from schemas.response.admin_doctor_response import AdminDoctorResponse
from schemas.response.admin_appointment_response import (
    AdminAppointmentResponse
)

class DashboardResponse(BaseModel):

    total_doctors: int

    total_patients: int

    total_appointments: int

    completed_appointments: int

    cancelled_appointments: int

    doctors: list[AdminDoctorResponse]

    recent_appointments: list[AdminAppointmentResponse]
