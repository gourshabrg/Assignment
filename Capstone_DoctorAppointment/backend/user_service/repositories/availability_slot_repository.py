from datetime import date
from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from user_service.models.availability_slot_model import AvailabilitySlot


class AvailabilitySlotRepository:

    async def create(
        self,
        slot: AvailabilitySlot,
        session: AsyncIOMotorClientSession | None = None
    ) -> AvailabilitySlot:

        await slot.insert(session=session)

        return slot

    async def get_by_id(
        self,
        slot_id: str
    ) -> Optional[AvailabilitySlot]:

        return await AvailabilitySlot.get(slot_id)

    async def get_by_doctor(
        self,
        doctor_id: str
    ) -> list[AvailabilitySlot]:

        return await AvailabilitySlot.find(
            AvailabilitySlot.doctor_id == doctor_id
        ).to_list()

    async def get_overlapping_slot(
        self,
        doctor_id: str,
        slot_date: date,
        start_time,
        end_time
    ) -> Optional[AvailabilitySlot]:

        slots = await AvailabilitySlot.find(
            AvailabilitySlot.doctor_id == doctor_id,
            AvailabilitySlot.slot_date == slot_date
        ).to_list()

        for slot in slots:
            if start_time < slot.end_time and slot.start_time < end_time:
                return slot

        return None

    async def update(
        self,
        slot: AvailabilitySlot,
        session: AsyncIOMotorClientSession | None = None
    ) -> AvailabilitySlot:

        await slot.save(session=session)

        return slot

    async def delete(
        self,
        slot: AvailabilitySlot,
        session: AsyncIOMotorClientSession | None = None
    ):

        await slot.delete(session=session)
