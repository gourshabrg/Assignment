from fastapi import APIRouter, Depends, status

from models.user_model import User

from schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)

from services.doctor_service import DoctorService

from security.role_checker import doctor_required


router = APIRouter(
    prefix="/doctors",
    tags=["Doctor Profile"]
)

doctor_service = DoctorService()


@router.get(
    "/profile",
    status_code=status.HTTP_200_OK
)
async def get_my_profile(
    current_user: User = Depends(
        doctor_required
    )
):
    """Doctor views their own profile."""

    return await doctor_service.get_my_profile(
        current_user=current_user
    )


@router.put(
    "/profile",
    status_code=status.HTTP_200_OK
)
async def update_my_profile(
    request: DoctorProfileUpdateRequest,
    current_user: User = Depends(
        doctor_required
    )
):
    """Doctor updates their own profile."""

    return await doctor_service.update_my_profile(
        current_user=current_user,
        request=request
    )
