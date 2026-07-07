from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.appointment_model import Appointment
from enums.appointment_status_enum import AppointmentStatus


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

    async def get_by_patient(
        self,
        patient_id: str
    ) -> list[Appointment]:

        return await Appointment.find(
            Appointment.patient_id == patient_id
        ).sort(
            "-appointment_date",
            "-start_time"
        ).to_list()

    async def get_by_doctor(
        self,
        doctor_id: str
    ) -> list[Appointment]:

        return await Appointment.find(
            Appointment.doctor_id == doctor_id
        ).sort(
            "-appointment_date",
            "-start_time"
        ).to_list()

    async def update(
        self,
        appointment: Appointment,
        session: AsyncIOMotorClientSession | None = None
    ) -> Appointment:

        await appointment.save(session=session)

        return appointment

    async def count_all(self) -> int:

        return await Appointment.find().count()

    async def count_by_status(self, status: AppointmentStatus) -> int:

        return await Appointment.find(
            Appointment.status == status
        ).count()

    async def get_recent(self, limit: int = 20) -> list[Appointment]:

        return await Appointment.find().sort(
            "-created_at"
        ).limit(limit).to_list()
