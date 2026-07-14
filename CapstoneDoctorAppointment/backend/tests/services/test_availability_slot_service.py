from datetime import date, time, timedelta
from unittest.mock import AsyncMock
import pytest
from exceptions import (
    PastSlotDateException,
    InvalidSlotTimeException,
    SlotOverlapException,
    SlotNotFoundException,
    SlotAlreadyBookedException,
    AccessDeniedException
)
from schemas.request.availability_slot_request import (
    CreateAvailabilitySlotRequest,
    UpdateAvailabilitySlotRequest
)
from services.availability_slot_service import AvailabilitySlotService
from tests.conftest import SLOT_ID

FUTURE_DATE = date.today() + timedelta(days=10)


@pytest.fixture
def service() -> AvailabilitySlotService:
    slot_service = AvailabilitySlotService()
    slot_service.slot_repository = AsyncMock()

    return slot_service


@pytest.fixture
def create_request() -> CreateAvailabilitySlotRequest:
    return CreateAvailabilitySlotRequest(
        slot_date=FUTURE_DATE,
        start_time=time(10, 0),
        end_time=time(10, 30)
    )


class TestCreateSlot:
    """test case: Slot Validation."""

    async def test_creates_valid_slot(
        self, service, doctor, create_request, future_slot
    ):
        service.slot_repository.get_overlapping_slot.return_value = None
        service.slot_repository.create.return_value = future_slot

        response = await service.create_slot(
            current_user=doctor,
            request=create_request
        )

        assert response.success is True
        service.slot_repository.create.assert_awaited_once()

    async def test_rejects_past_date(self, service, doctor):
        with pytest.raises(PastSlotDateException):
            await service.create_slot(
                current_user=doctor,
                request=CreateAvailabilitySlotRequest(
                    slot_date=date.today() - timedelta(days=1),
                    start_time=time(10, 0),
                    end_time=time(10, 30)
                )
            )

    async def test_rejects_start_after_end(self, service, doctor):
        with pytest.raises(InvalidSlotTimeException):
            await service.create_slot(
                current_user=doctor,
                request=CreateAvailabilitySlotRequest(
                    slot_date=FUTURE_DATE,
                    start_time=time(11, 0),
                    end_time=time(10, 0)
                )
            )

    async def test_rejects_equal_start_and_end(self, service, doctor):
        with pytest.raises(InvalidSlotTimeException):
            await service.create_slot(
                current_user=doctor,
                request=CreateAvailabilitySlotRequest(
                    slot_date=FUTURE_DATE,
                    start_time=time(10, 0),
                    end_time=time(10, 0)
                )
            )

    async def test_rejects_overlapping_slot(
        self, service, doctor, create_request, future_slot
    ):
        service.slot_repository.get_overlapping_slot.return_value = future_slot

        with pytest.raises(SlotOverlapException):
            await service.create_slot(
                current_user=doctor,
                request=create_request
            )


class TestGetMySlots:

    async def test_returns_own_slots(self, service, doctor, future_slot):
        service.slot_repository.get_by_doctor.return_value = [future_slot]

        response = await service.get_my_slots(current_user=doctor)

        assert len(response.data) == 1


class TestUpdateSlot:

    async def test_updates_own_slot(self, service, doctor, future_slot):
        service.slot_repository.get_by_id.return_value = future_slot
        service.slot_repository.get_overlapping_slot.return_value = None
        service.slot_repository.update.return_value = future_slot

        response = await service.update_slot(
            current_user=doctor,
            slot_id=SLOT_ID,
            request=UpdateAvailabilitySlotRequest(
                slot_date=FUTURE_DATE,
                start_time=time(11, 0),
                end_time=time(11, 30)
            )
        )

        assert response.success is True

    async def test_rejects_unknown_slot(self, service, doctor):
        service.slot_repository.get_by_id.return_value = None

        with pytest.raises(SlotNotFoundException):
            await service.update_slot(
                current_user=doctor,
                slot_id=SLOT_ID,
                request=UpdateAvailabilitySlotRequest(
                    slot_date=FUTURE_DATE,
                    start_time=time(11, 0),
                    end_time=time(11, 30)
                )
            )

    async def test_rejects_other_doctors_slot(self, service, patient, future_slot):
        service.slot_repository.get_by_id.return_value = future_slot

        with pytest.raises(AccessDeniedException):
            await service.update_slot(
                current_user=patient,
                slot_id=SLOT_ID,
                request=UpdateAvailabilitySlotRequest(
                    slot_date=FUTURE_DATE,
                    start_time=time(11, 0),
                    end_time=time(11, 30)
                )
            )

    async def test_rejects_booked_slot(self, service, doctor, booked_slot):
        service.slot_repository.get_by_id.return_value = booked_slot

        with pytest.raises(SlotAlreadyBookedException):
            await service.update_slot(
                current_user=doctor,
                slot_id=SLOT_ID,
                request=UpdateAvailabilitySlotRequest(
                    slot_date=FUTURE_DATE,
                    start_time=time(11, 0),
                    end_time=time(11, 30)
                )
            )

    async def test_rejects_past_date_on_update(
        self, service, doctor, future_slot
    ):
        service.slot_repository.get_by_id.return_value = future_slot

        with pytest.raises(PastSlotDateException):
            await service.update_slot(
                current_user=doctor,
                slot_id=SLOT_ID,
                request=UpdateAvailabilitySlotRequest(
                    slot_date=date.today() - timedelta(days=1),
                    start_time=time(11, 0),
                    end_time=time(11, 30)
                )
            )


class TestDeleteSlot:

    async def test_deletes_own_slot(self, service, doctor, future_slot):
        service.slot_repository.get_by_id.return_value = future_slot

        response = await service.delete_slot(
            current_user=doctor,
            slot_id=SLOT_ID
        )

        assert response.success is True
        service.slot_repository.delete.assert_awaited_once()

    async def test_rejects_booked_slot(self, service, doctor, booked_slot):
        service.slot_repository.get_by_id.return_value = booked_slot

        with pytest.raises(SlotAlreadyBookedException):
            await service.delete_slot(
                current_user=doctor,
                slot_id=SLOT_ID
            )

        service.slot_repository.delete.assert_not_called()
