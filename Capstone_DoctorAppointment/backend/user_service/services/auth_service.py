from user_service.models.user_model import User
from user_service.models.doctor_profile_model import DoctorProfile

from user_service.repositories.user_repository import UserRepository
from user_service.repositories.doctor_profile_repository import (
    DoctorProfileRepository
)

from user_service.schemas.request.patient_register_request import (
    PatientRegisterRequest
)
from user_service.schemas.request.doctor_register_request import (
    DoctorRegisterRequest
)
from user_service.schemas.request.login_request import (
    LoginRequest
)

from user_service.schemas.response.api_response import ApiResponse
from user_service.schemas.response.user_response import UserResponse
from user_service.schemas.response.login_response import LoginResponse

from user_service.utils.password import PasswordManager
from user_service.utils.jwt_handler import JWTManager

from shared.enums.role_enum import UserRole

from shared.exceptions import (
    UserAlreadyExistsException,
    UserNotFoundException,
    InvalidPasswordException,
    InvalidCredentialsException
)

from shared.constants import (
    PATIENT_REGISTER_SUCCESS,
    DOCTOR_REGISTER_SUCCESS,
    LOGIN_SUCCESS
)


class AuthService:

    def __init__(self):
        self.user_repository = UserRepository()
        self.doctor_repository = DoctorProfileRepository()

    async def register_patient(
        self,
        request: PatientRegisterRequest
    ) -> ApiResponse[UserResponse]:

        existing_user = await self.user_repository.get_by_email(
            email=request.email
        )

        if existing_user:
            raise UserAlreadyExistsException()

        if not PasswordManager.validate_password(
            password=request.password
        ):
            raise InvalidPasswordException()

        hashed_password = PasswordManager.hash_password(
            password=request.password
        )

        user = User(
            full_name=request.full_name,
            email=request.email,
            password=hashed_password,
            phone=request.phone,
            gender=request.gender,
            dob=request.dob,
            role=UserRole.PATIENT,
            is_active=True
        )

        saved_user = await self.user_repository.create(
            user=user
        )

        response = UserResponse(
            id=str(saved_user.id),
            full_name=saved_user.full_name,
            email=saved_user.email,
            phone=saved_user.phone,
            role=saved_user.role,
            is_active=saved_user.is_active,
            created_at=saved_user.created_at
        )

        return ApiResponse(
            success=True,
            message=PATIENT_REGISTER_SUCCESS,
            data=response
        )

    async def register_doctor(
        self,
        request: DoctorRegisterRequest
    ) -> ApiResponse[UserResponse]:

        existing_user = await self.user_repository.get_by_email(
            email=request.email
        )

        if existing_user:
            raise UserAlreadyExistsException()

        if not PasswordManager.validate_password(
            password=request.password
        ):
            raise InvalidPasswordException()

        hashed_password = PasswordManager.hash_password(
            password=request.password
        )

        user = User(
            full_name=request.full_name,
            email=request.email,
            password=hashed_password,
            phone=request.phone,
            role=UserRole.DOCTOR,
            is_active=True
        )

        saved_user = await self.user_repository.create(
            user=user
        )

        doctor_profile = DoctorProfile(
            user_id=str(saved_user.id),
            qualification=request.qualification,
            specialization=request.specialization,
            experience=request.experience,
            license_number=request.license_number,
            consultation_fee=request.consultation_fee,
            clinic_address=request.clinic_address
        )

        await self.doctor_repository.create(
            profile=doctor_profile
        )

        response = UserResponse(
            id=str(saved_user.id),
            full_name=saved_user.full_name,
            email=saved_user.email,
            phone=saved_user.phone,
            role=saved_user.role,
            is_active=saved_user.is_active,
            created_at=saved_user.created_at
        )

        return ApiResponse(
            success=True,
            message=DOCTOR_REGISTER_SUCCESS,
            data=response
        )

    async def login(
        self,
        request: LoginRequest
    ) -> ApiResponse[LoginResponse]:

        user = await self.user_repository.get_by_email(
            email=request.email
        )

        if not user:
            raise UserNotFoundException()

        if not PasswordManager.verify_password(
            plain_password=request.password,
            hashed_password=user.password
        ):
            raise InvalidCredentialsException()

        access_token = JWTManager.create_access_token(
            user_id=str(user.id),
            email=user.email,
            role=user.role.value
        )

        response = LoginResponse(
            access_token=access_token
        )

        return ApiResponse(
            success=True,
            message=LOGIN_SUCCESS,
            data=response
        )