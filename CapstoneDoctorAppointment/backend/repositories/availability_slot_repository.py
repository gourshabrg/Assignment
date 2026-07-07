from datetime import date, datetime
from typing import Optional

from beanie import PydanticObjectId

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

    async def get_available_by_doctor(
        self,
        doctor_id: str,
        from_date: date
    ) -> list[AvailabilitySlot]:
        """Unbooked, from-today-onward slots, soonest first."""

        return await AvailabilitySlot.find(
            AvailabilitySlot.doctor_id == doctor_id,
            AvailabilitySlot.is_booked == False,  # noqa: E712
            AvailabilitySlot.slot_date >= from_date
        ).sort(
            "slot_date",
            "start_time"
        ).to_list()

    async def get_overlapping_slot(
        self,
        doctor_id: str,
        slot_date: date,
        start_time,
        end_time
    ) -> Optional[AvailabilitySlot]:
        """Finds an existing slot overlapping the given time range."""

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

    async def book_if_available(self, slot_id: str) -> bool:
        """Atomically flips is_booked False->True.

        The filter on is_booked=False makes this a single-document
        compare-and-swap: MongoDB guarantees a single document update
        is atomic, so if two requests race to book the same slot,
        only one can ever match this filter and succeed -- no
        multi-document transaction (and no replica set) needed.
        Returns True if this call was the one that booked it.
        """

        result = await AvailabilitySlot.find_one(
            AvailabilitySlot.id == PydanticObjectId(slot_id),
            AvailabilitySlot.is_booked == False  # noqa: E712
        ).update(
            {"$set": {
                "is_booked": True,
                "updated_at": datetime.utcnow()
            }}
        )

        return result.modified_count == 1
