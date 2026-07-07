from pydantic import BaseModel

from enums.appointment_status_enum import AppointmentStatus


class UpdateAppointmentStatusRequest(BaseModel):

    status: AppointmentStatus
