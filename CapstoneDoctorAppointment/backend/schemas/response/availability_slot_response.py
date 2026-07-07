from datetime import date, datetime, time

from pydantic import BaseModel


class AvailabilitySlotResponse(BaseModel):

    id: str

    doctor_id: str

    slot_date: date

    start_time: time

    end_time: time

    is_booked: bool

    created_at: datetime

    updated_at: datetime
