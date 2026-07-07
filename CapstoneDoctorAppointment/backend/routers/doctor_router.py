from fastapi import APIRouter, Depends, Query, status

from enums.specialization_enum import Specialization

from models.user_model import User

from schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)

from services.doctor_service import DoctorService

from security.current_user import get_current_user
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


@router.get(
    "/search",
    status_code=status.HTTP_200_OK
)
async def search_doctors(
    name: str | None = Query(default=None),
    specialization: Specialization | None = Query(default=None),
    location: str | None = Query(default=None),
    min_experience: int | None = Query(default=None, ge=0),
    max_fee: float | None = Query(default=None, gt=0),
    current_user: User = Depends(
        get_current_user
    )
):
    """Any logged-in user searches active doctors. All filters are
    optional and combinable.
    """

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
    """View a doctor's public profile plus their available slots.

    Registered after /search so the literal path isn't swallowed by
    this route's {doctor_id} path parameter.
    """

    return await doctor_service.get_doctor_by_id(
        doctor_id=doctor_id
    )
