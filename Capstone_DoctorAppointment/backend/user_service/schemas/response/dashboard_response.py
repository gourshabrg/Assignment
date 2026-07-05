from pydantic import BaseModel


class DashboardResponse(BaseModel):

    total_doctors: int

    total_patients: int

    total_appointments: int

    completed_appointments: int

    cancelled_appointments: int
