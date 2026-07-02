from datetime import date, datetime, time

from beanie import Document
from pydantic import Field


class AvailabilitySlot(Document):

    doctor_id: str

    slot_date: date

    start_time: time

    end_time: time

    is_booked: bool = False

    created_at: datetime = Field(
        default_factory=datetime.utcnow
    )

    updated_at: datetime = Field(
        default_factory=datetime.utcnow
    )

    class Settings:
        name = "availability_slots"
