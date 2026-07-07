from datetime import date, datetime

from beanie import Document
from pydantic import Field

from enums.appointment_status_enum import AppointmentStatus


class Appointment(Document):
    """MongoDB 'appointments' collection. start_time/end_time are
    stored as ISO strings (HH:MM:SS) since MongoDB/BSON has no native
    time type -- see utils/time_utils.py.
    """

    patient_id: str

    doctor_id: str

    slot_id: str

    appointment_date: date

    start_time: str

    end_time: str

    status: AppointmentStatus = AppointmentStatus.BOOKED

    created_at: datetime = Field(default_factory=datetime.utcnow)

    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "appointments"
