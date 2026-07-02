from fastapi import APIRouter, Depends, status

from user_service.models.user_model import User

from user_service.schemas.request.availability_slot_request import (
    CreateAvailabilitySlotRequest,
    UpdateAvailabilitySlotRequest
)

from user_service.services.availability_slot_service import (
    AvailabilitySlotService
)

from shared.security.role_checker import doctor_required


router = APIRouter(
    prefix="/availability",
    tags=["Doctor Availability"]
)

availability_slot_service = AvailabilitySlotService()


@router.post(
    "/slots",
    status_code=status.HTTP_201_CREATED
)
async def create_slot(
    request: CreateAvailabilitySlotRequest,
    current_user: User = Depends(
        doctor_required
    )
):

    return await availability_slot_service.create_slot(
        current_user=current_user,
        request=request
    )


@router.get(
    "/slots/me",
    status_code=status.HTTP_200_OK
)
async def get_my_slots(
    current_user: User = Depends(
        doctor_required
    )
):

    return await availability_slot_service.get_my_slots(
        current_user=current_user
    )


@router.put(
    "/slots/{slot_id}",
    status_code=status.HTTP_200_OK
)
async def update_slot(
    slot_id: str,
    request: UpdateAvailabilitySlotRequest,
    current_user: User = Depends(
        doctor_required
    )
):

    return await availability_slot_service.update_slot(
        current_user=current_user,
        slot_id=slot_id,
        request=request
    )


@router.delete(
    "/slots/{slot_id}",
    status_code=status.HTTP_200_OK
)
async def delete_slot(
    slot_id: str,
    current_user: User = Depends(
        doctor_required
    )
):

    return await availability_slot_service.delete_slot(
        current_user=current_user,
        slot_id=slot_id
    )
