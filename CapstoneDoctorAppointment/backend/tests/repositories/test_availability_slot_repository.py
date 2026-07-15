from datetime import date, time, timedelta
import pytest
from models.availability_slot_model import AvailabilitySlot
from repositories.availability_slot_repository import (
    AvailabilitySlotRepository
)
from tests.conftest import DOCTOR_ID
FUTURE_DATE = date.today() + timedelta(days=10)


@pytest.fixture
def repository() -> AvailabilitySlotRepository:
    return AvailabilitySlotRepository()


def _slot(start: str, end: str, slot_date=FUTURE_DATE, is_booked=False):
    return AvailabilitySlot(
        doctor_id=DOCTOR_ID,
        slot_date=slot_date,
        start_time=start,
        end_time=end,
        is_booked=is_booked
    )


class TestCreateAndGet:

    async def test_create_then_get_by_id(self, repository):
        created = await repository.create(slot=_slot("10:00:00", "10:30:00"))

        found = await repository.get_by_id(slot_id=str(created.id))

        assert found.start_time == "10:00:00"

    async def test_get_by_doctor(self, repository):
        await repository.create(slot=_slot("10:00:00", "10:30:00"))
        await repository.create(slot=_slot("11:00:00", "11:30:00"))

        slots = await repository.get_by_doctor(doctor_id=DOCTOR_ID)

        assert len(slots) == 2


class TestGetAvailableByDoctor:

    async def test_returns_only_unbooked_future_slots(self, repository):
        await repository.create(slot=_slot("10:00:00", "10:30:00"))
        await repository.create(
            slot=_slot("11:00:00", "11:30:00", is_booked=True)
        )

        slots = await repository.get_available_by_doctor(
            doctor_id=DOCTOR_ID,
            from_date=date.today()
        )

        assert len(slots) == 1
        assert slots[0].is_booked is False


class TestGetOverlappingSlot:

    async def test_detects_overlap(self, repository):
        await repository.create(slot=_slot("10:00:00", "11:00:00"))

        overlap = await repository.get_overlapping_slot(
            doctor_id=DOCTOR_ID,
            slot_date=FUTURE_DATE,
            start_time=time(10, 30),
            end_time=time(11, 30)
        )

        assert overlap is not None

    async def test_returns_none_when_no_overlap(self, repository):
        await repository.create(slot=_slot("10:00:00", "11:00:00"))

        overlap = await repository.get_overlapping_slot(
            doctor_id=DOCTOR_ID,
            slot_date=FUTURE_DATE,
            start_time=time(11, 0),
            end_time=time(12, 0)
        )

        assert overlap is None


class TestUpdateAndDelete:

    async def test_update(self, repository):
        created = await repository.create(slot=_slot("10:00:00", "10:30:00"))

        created.start_time = "12:00:00"
        await repository.update(slot=created)

        found = await repository.get_by_id(slot_id=str(created.id))

        assert found.start_time == "12:00:00"

    async def test_delete(self, repository):
        created = await repository.create(slot=_slot("10:00:00", "10:30:00"))

        await repository.delete(slot=created)

        assert await repository.get_by_id(slot_id=str(created.id)) is None


class TestBookIfAvailable:
    """SRS test case: Double Booking Prevention."""

    async def test_books_free_slot(self, repository):
        created = await repository.create(slot=_slot("10:00:00", "10:30:00"))

        assert await repository.book_if_available(
            slot_id=str(created.id)
        ) is True

        found = await repository.get_by_id(slot_id=str(created.id))
        assert found.is_booked is True

    async def test_second_booking_of_same_slot_fails(self, repository):
        created = await repository.create(slot=_slot("10:00:00", "10:30:00"))

        first = await repository.book_if_available(slot_id=str(created.id))
        second = await repository.book_if_available(slot_id=str(created.id))

        assert first is True
        assert second is False
