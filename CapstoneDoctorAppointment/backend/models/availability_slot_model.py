from datetime import date, datetime

from beanie import Document
from pydantic import Field


class AvailabilitySlot(Document):
    """MongoDB 'availability_slots' collection."""

    doctor_id: str

    slot_date: date

    start_time: str

    end_time: str

    is_booked: bool = False

    created_at: datetime = Field(default_factory=datetime.utcnow)

    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "availability_slots"
