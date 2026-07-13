from datetime import date, datetime, time

from pydantic import BaseModel

from enums.appointment_status_enum import AppointmentStatus


class AppointmentResponse(BaseModel):

    id: str

    patient_id: str

    patient_name: str | None = None

    doctor_id: str

    doctor_name: str | None = None

    slot_id: str

    appointment_date: date

    start_time: time

    end_time: time

    status: AppointmentStatus

    cancellation_reason: str | None = None

    created_at: datetime
