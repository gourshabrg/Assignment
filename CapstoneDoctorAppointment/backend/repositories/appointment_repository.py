from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.appointment_model import Appointment


class AppointmentRepository:
    """Direct MongoDB access for the Appointment document."""

    async def create(
        self,
        appointment: Appointment,
        session: AsyncIOMotorClientSession | None = None
    ) -> Appointment:

        await appointment.insert(session=session)

        return appointment

    async def get_by_id(
        self,
        appointment_id: str
    ) -> Optional[Appointment]:

        return await Appointment.get(appointment_id)
