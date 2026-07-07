from datetime import date
from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.availability_slot_model import AvailabilitySlot

from utils.time_utils import time_to_str


class AvailabilitySlotRepository:
    """Direct MongoDB access for the AvailabilitySlot document."""

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
        """Finds a slot for this doctor/date whose time range overlaps
        the given range. Times are stored as zero-padded "HH:MM:SS"
        strings, so string comparison gives the same ordering as
        comparing time objects.
        """

        slots = await AvailabilitySlot.find(
            AvailabilitySlot.doctor_id == doctor_id,
            AvailabilitySlot.slot_date == slot_date
        ).to_list()

        start_time_str = time_to_str(start_time)
        end_time_str = time_to_str(end_time)

        for slot in slots:
            if (
                start_time_str < slot.end_time
                and slot.start_time < end_time_str
            ):
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
