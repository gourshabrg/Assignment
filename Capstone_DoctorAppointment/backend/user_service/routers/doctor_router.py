from fastapi import APIRouter, Depends, status

from user_service.models.user_model import User

from user_service.schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)

from user_service.services.doctor_service import DoctorService

from shared.security.role_checker import doctor_required


router = APIRouter(
    prefix="/doctors",
    tags=["Doctor Profile"]
)

doctor_service = DoctorService()


@router.get(
    "/me",
    status_code=status.HTTP_200_OK
)
async def get_my_profile(
    current_user: User = Depends(
        doctor_required
    )
):

    return await doctor_service.get_my_profile(
        current_user=current_user
    )


@router.put(
    "/me",
    status_code=status.HTTP_200_OK
)
async def update_my_profile(
    request: DoctorProfileUpdateRequest,
    current_user: User = Depends(
        doctor_required
    )
):

    return await doctor_service.update_my_profile(
        current_user=current_user,
        request=request
    )
