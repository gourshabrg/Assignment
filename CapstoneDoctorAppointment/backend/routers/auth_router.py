from fastapi import APIRouter, status

from schemas.request.patient_register_request import (
    PatientRegisterRequest
)
from schemas.request.doctor_register_request import (
    DoctorRegisterRequest
)

from services.auth_service import AuthService


router = APIRouter(
    prefix="/auth",
    tags=["Authentication"]
)

auth_service = AuthService()


@router.post(
    "/register/patient",
    status_code=status.HTTP_201_CREATED
)
async def register_patient(
    request: PatientRegisterRequest
):
    """Registers a new patient account."""

    return await auth_service.register_patient(
        request=request
    )


@router.post(
    "/register/doctor",
    status_code=status.HTTP_201_CREATED
)
async def register_doctor(
    request: DoctorRegisterRequest
):
    """Registers a new doctor account (and its profile)."""

    return await auth_service.register_doctor(
        request=request
    )
