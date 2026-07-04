from pydantic import BaseModel

from shared.enums.appointment_status_enum import AppointmentStatus


class UpdateAppointmentStatusRequest(BaseModel):

    status: AppointmentStatus
