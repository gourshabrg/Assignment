from datetime import date, datetime

from fastapi import HTTPException

from user_service.models.user_model import User
from user_service.models.availability_slot_model import AvailabilitySlot

from user_service.repositories.availability_slot_repository import (
    AvailabilitySlotRepository
)

from user_service.schemas.request.availability_slot_request import (
    CreateAvailabilitySlotRequest,
    UpdateAvailabilitySlotRequest
)

from user_service.schemas.response.api_response import ApiResponse
from user_service.schemas.response.availability_slot_response import (
    AvailabilitySlotResponse
)

from shared.exceptions import (
    SlotNotFoundException,
    SlotAlreadyBookedException,
    InvalidSlotTimeException,
    PastSlotDateException,
    SlotOverlapException,
    AccessDeniedException
)

from shared.constants import (
    SLOT_CREATED,
    SLOT_UPDATED,
    SLOT_DELETED,
    SLOTS_FETCHED
)

from shared.logger.logger import get_logger

logger = get_logger(__name__)


class AvailabilitySlotService:

    def __init__(self):
        self.slot_repository = AvailabilitySlotRepository()

    def _build_response(
        self,
        slot: AvailabilitySlot
    ) -> AvailabilitySlotResponse:

        return AvailabilitySlotResponse(
            id=str(slot.id),
            doctor_id=slot.doctor_id,
            slot_date=slot.slot_date,
            start_time=slot.start_time,
            end_time=slot.end_time,
            is_booked=slot.is_booked,
            created_at=slot.created_at,
            updated_at=slot.updated_at
        )

    def _validate_slot_time(
        self,
        slot_date: date,
        start_time,
        end_time
    ):

        if slot_date < datetime.utcnow().date():
            raise PastSlotDateException()

        if start_time >= end_time:
            raise InvalidSlotTimeException()

    async def create_slot(
        self,
        current_user: User,
        request: CreateAvailabilitySlotRequest
    ) -> ApiResponse[AvailabilitySlotResponse]:

        try:

            self._validate_slot_time(
                slot_date=request.slot_date,
                start_time=request.start_time,
                end_time=request.end_time
            )

            overlapping_slot = (
                await self.slot_repository.get_overlapping_slot(
                    doctor_id=str(current_user.id),
                    slot_date=request.slot_date,
                    start_time=request.start_time,
                    end_time=request.end_time
                )
            )

            if overlapping_slot:
                logger.warning(
                    f"Slot creation failed: overlapping slot "
                    f"(doctor_id={current_user.id})"
                )
                raise SlotOverlapException()

            slot = AvailabilitySlot(
                doctor_id=str(current_user.id),
                slot_date=request.slot_date,
                start_time=request.start_time,
                end_time=request.end_time
            )

            saved_slot = await self.slot_repository.create(slot=slot)

            logger.info(
                f"Slot created: slot_id={saved_slot.id} "
                f"doctor_id={current_user.id}"
            )

            return ApiResponse(
                success=True,
                message=SLOT_CREATED,
                data=self._build_response(saved_slot)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error creating slot: {error}")
            raise

    async def get_my_slots(
        self,
        current_user: User
    ) -> ApiResponse[list[AvailabilitySlotResponse]]:

        try:

            slots = await self.slot_repository.get_by_doctor(
                doctor_id=str(current_user.id)
            )

            response = [
                self._build_response(slot) for slot in slots
            ]

            return ApiResponse(
                success=True,
                message=SLOTS_FETCHED,
                data=response
            )

        except Exception as error:
            logger.error(f"Unexpected error fetching slots: {error}")
            raise

    async def _get_owned_slot(
        self,
        current_user: User,
        slot_id: str
    ) -> AvailabilitySlot:

        slot = await self.slot_repository.get_by_id(slot_id=slot_id)

        if not slot:
            logger.warning(f"Slot not found: slot_id={slot_id}")
            raise SlotNotFoundException()

        if slot.doctor_id != str(current_user.id):
            logger.warning(
                f"Access denied: doctor_id={current_user.id} "
                f"does not own slot_id={slot_id}"
            )
            raise AccessDeniedException()

        return slot

    async def update_slot(
        self,
        current_user: User,
        slot_id: str,
        request: UpdateAvailabilitySlotRequest
    ) -> ApiResponse[AvailabilitySlotResponse]:

        try:

            slot = await self._get_owned_slot(
                current_user=current_user,
                slot_id=slot_id
            )

            if slot.is_booked:
                logger.warning(
                    f"Slot update failed: already booked "
                    f"slot_id={slot_id}"
                )
                raise SlotAlreadyBookedException()

            self._validate_slot_time(
                slot_date=request.slot_date,
                start_time=request.start_time,
                end_time=request.end_time
            )

            overlapping_slot = (
                await self.slot_repository.get_overlapping_slot(
                    doctor_id=str(current_user.id),
                    slot_date=request.slot_date,
                    start_time=request.start_time,
                    end_time=request.end_time
                )
            )

            if overlapping_slot and str(overlapping_slot.id) != slot_id:
                logger.warning(
                    f"Slot update failed: overlapping slot "
                    f"doctor_id={current_user.id}"
                )
                raise SlotOverlapException()

            slot.slot_date = request.slot_date
            slot.start_time = request.start_time
            slot.end_time = request.end_time
            slot.updated_at = datetime.utcnow()

            updated_slot = await self.slot_repository.update(slot=slot)

            logger.info(f"Slot updated: slot_id={slot_id}")

            return ApiResponse(
                success=True,
                message=SLOT_UPDATED,
                data=self._build_response(updated_slot)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error updating slot: {error}")
            raise

    async def delete_slot(
        self,
        current_user: User,
        slot_id: str
    ) -> ApiResponse[None]:

        try:

            slot = await self._get_owned_slot(
                current_user=current_user,
                slot_id=slot_id
            )

            if slot.is_booked:
                logger.warning(
                    f"Slot delete failed: already booked "
                    f"slot_id={slot_id}"
                )
                raise SlotAlreadyBookedException()

            await self.slot_repository.delete(slot=slot)

            logger.info(f"Slot deleted: slot_id={slot_id}")

            return ApiResponse(
                success=True,
                message=SLOT_DELETED,
                data=None
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error deleting slot: {error}")
            raise
