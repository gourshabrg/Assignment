from datetime import date, datetime, time

from beanie import Document
from pydantic import Field

from shared.enums.appointment_status_enum import AppointmentStatus


class Appointment(Document):

    patient_id: str

    doctor_id: str

    slot_id: str

    appointment_date: date

    start_time: time

    end_time: time

    status: AppointmentStatus = AppointmentStatus.BOOKED

    created_at: datetime = Field(
        default_factory=datetime.utcnow
    )

    updated_at: datetime = Field(
        default_factory=datetime.utcnow
    )

    class Settings:
        name = "appointments"
