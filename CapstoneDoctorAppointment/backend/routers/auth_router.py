from fastapi import APIRouter, Depends, status

from models.user_model import User

from schemas.request.patient_register_request import (
    PatientRegisterRequest
)
from schemas.request.doctor_register_request import (
    DoctorRegisterRequest
)
from schemas.request.login_request import LoginRequest
from schemas.request.change_password_request import (
    ChangePasswordRequest
)
from schemas.request.reset_password_request import (
    ResetPasswordRequest
)

from services.auth_service import AuthService

from security.current_user import get_current_user


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


@router.post(
    "/login",
    status_code=status.HTTP_200_OK
)
async def login(
    request: LoginRequest
):
    """Logs in with email/password and returns a JWT access token."""

    return await auth_service.login(
        request=request
    )


@router.get(
    "/me",
    status_code=status.HTTP_200_OK
)
async def me(
    current_user: User = Depends(
        get_current_user
    )
):
    """Returns the logged-in user's own profile."""

    return await auth_service.get_profile(
        user=current_user
    )


@router.post(
    "/logout",
    status_code=status.HTTP_200_OK
)
async def logout(
    current_user: User = Depends(
        get_current_user
    )
):
    """Logs the current user out."""

    return await auth_service.logout(
        user=current_user
    )


@router.post(
    "/change-password",
    status_code=status.HTTP_200_OK
)
async def change_password(
    request: ChangePasswordRequest,
    current_user: User = Depends(
        get_current_user
    )
):
    """Changes the logged-in user's password."""

    return await auth_service.change_password(
        user=current_user,
        request=request
    )


@router.post(
    "/reset-password",
    status_code=status.HTTP_200_OK
)
async def reset_password(
    request: ResetPasswordRequest
):
    """Resets a user's password by email."""

    return await auth_service.reset_password(
        request=request
    )
