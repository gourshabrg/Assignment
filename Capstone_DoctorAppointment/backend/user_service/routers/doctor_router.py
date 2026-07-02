from fastapi import APIRouter, Depends, Query, status

from user_service.models.user_model import User

from user_service.schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)

from user_service.services.doctor_service import DoctorService

from shared.security.current_user import get_current_user
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


@router.get(
    "/search",
    status_code=status.HTTP_200_OK
)
async def search_doctors(
    name: str | None = Query(default=None),
    specialization: str | None = Query(default=None),
    location: str | None = Query(default=None),
    min_experience: int | None = Query(default=None, ge=0),
    max_fee: float | None = Query(default=None, gt=0),
    current_user: User = Depends(
        get_current_user
    )
):

    return await doctor_service.search_doctors(
        name=name,
        specialization=specialization,
        location=location,
        min_experience=min_experience,
        max_fee=max_fee
    )


@router.get(
    "/{doctor_id}",
    status_code=status.HTTP_200_OK
)
async def get_doctor_details(
    doctor_id: str,
    current_user: User = Depends(
        get_current_user
    )
):

    return await doctor_service.get_doctor_by_id(
        doctor_id=doctor_id
    )
