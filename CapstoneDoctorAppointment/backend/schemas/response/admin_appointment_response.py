from datetime import date
from pydantic import BaseModel
from enums.appointment_status_enum import AppointmentStatus

class AdminAppointmentResponse(BaseModel):

    id: str

    patient_name: str

    doctor_name: str

    appointment_date: date

    status: AppointmentStatus
