from datetime import date, time

from pydantic import BaseModel


class CreateAvailabilitySlotRequest(BaseModel):

    slot_date: date

    start_time: time

    end_time: time


class UpdateAvailabilitySlotRequest(BaseModel):

    slot_date: date

    start_time: time

    end_time: time
