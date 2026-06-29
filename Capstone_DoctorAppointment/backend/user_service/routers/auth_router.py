from fastapi import APIRouter, status

from user_service.schemas.request.patient_register_request import (
    PatientRegisterRequest
)

from user_service.schemas.request.doctor_register_request import (
    DoctorRegisterRequest
)

from user_service.schemas.request.login_request import (
    LoginRequest
)

from user_service.services.auth_service import AuthService

from fastapi import Depends

from shared.security.current_user import (
    get_current_user
)

from user_service.models.user_model import User



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

    return await auth_service.login(
        request=request
    )

@router.get("/me")
async def me(

    current_user: User = Depends(
        get_current_user
    )

):

    return current_user