from datetime import date, datetime, time

from pydantic import BaseModel

from shared.enums.appointment_status_enum import AppointmentStatus


class AppointmentResponse(BaseModel):

    id: str

    patient_id: str

    doctor_id: str

    slot_id: str

    appointment_date: date

    start_time: time

    end_time: time

    status: AppointmentStatus

    created_at: datetime
