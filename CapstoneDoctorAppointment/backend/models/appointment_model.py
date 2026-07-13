from datetime import date, datetime

from beanie import Document
from pydantic import Field

from enums.appointment_status_enum import AppointmentStatus


class Appointment(Document):
    """MongoDB 'appointments' collection."""

    patient_id: str

    doctor_id: str

    slot_id: str

    appointment_date: date

    start_time: str

    end_time: str

    status: AppointmentStatus = AppointmentStatus.PENDING_PAYMENT

    cancellation_reason: str | None = None

    created_at: datetime = Field(default_factory=datetime.utcnow)

    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "appointments"
