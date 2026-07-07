from datetime import date, datetime

from beanie import Document
from pydantic import Field


class AvailabilitySlot(Document):
    """MongoDB 'availability_slots' collection -- one bookable time
    block for one doctor. start_time/end_time are stored as ISO
    strings (HH:MM:SS) since MongoDB/BSON has no native time type --
    see utils/time_utils.py.
    """

    doctor_id: str

    slot_date: date

    start_time: str

    end_time: str

    is_booked: bool = False

    created_at: datetime = Field(default_factory=datetime.utcnow)

    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "availability_slots"
