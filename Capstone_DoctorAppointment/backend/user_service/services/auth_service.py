from fastapi import HTTPException

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
from user_service.schemas.request.change_password_request import (
    ChangePasswordRequest
)
from user_service.schemas.request.reset_password_request import (
    ResetPasswordRequest
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
    InvalidCredentialsException,
    SamePasswordException,
    IncorrectOldPasswordException
)

from shared.constants import (
    PATIENT_REGISTER_SUCCESS,
    DOCTOR_REGISTER_SUCCESS,
    LOGIN_SUCCESS,
    LOGOUT_SUCCESS,
    PASSWORD_CHANGED_SUCCESS,
    PASSWORD_RESET_SUCCESS
)

from shared.logger.logger import get_logger

logger = get_logger(__name__)


class AuthService:

    def __init__(self):
        self.user_repository = UserRepository()
        self.doctor_repository = DoctorProfileRepository()

    async def register_patient(
        self,
        request: PatientRegisterRequest
    ) -> ApiResponse[UserResponse]:

        try:

            existing_user = await self.user_repository.get_by_email(
                email=request.email
            )

            if existing_user:
                logger.warning(
                    f"Patient registration failed: "
                    f"email already exists ({request.email})"
                )
                raise UserAlreadyExistsException()

            if not PasswordManager.validate_password(
                password=request.password
            ):
                logger.warning(
                    f"Patient registration failed: "
                    f"invalid password for ({request.email})"
                )
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

            logger.info(
                f"Patient registered successfully: user_id={saved_user.id}"
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

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error during patient registration: {error}"
            )
            raise

    async def register_doctor(
        self,
        request: DoctorRegisterRequest
    ) -> ApiResponse[UserResponse]:

        try:

            existing_user = await self.user_repository.get_by_email(
                email=request.email
            )

            if existing_user:
                logger.warning(
                    f"Doctor registration failed: "
                    f"email already exists ({request.email})"
                )
                raise UserAlreadyExistsException()

            if not PasswordManager.validate_password(
                password=request.password
            ):
                logger.warning(
                    f"Doctor registration failed: "
                    f"invalid password for ({request.email})"
                )
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

            logger.info(
                f"Doctor registered successfully: user_id={saved_user.id}"
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

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error during doctor registration: {error}"
            )
            raise

    async def login(
        self,
        request: LoginRequest
    ) -> ApiResponse[LoginResponse]:

        try:

            user = await self.user_repository.get_by_email(
                email=request.email
            )

            if not user:
                logger.warning(
                    f"Login failed: user not found ({request.email})"
                )
                raise UserNotFoundException()

            if not PasswordManager.verify_password(
                plain_password=request.password,
                hashed_password=user.password
            ):
                logger.warning(
                    f"Login failed: invalid credentials ({request.email})"
                )
                raise InvalidCredentialsException()

            access_token = JWTManager.create_access_token(
                user_id=str(user.id),
                email=user.email,
                role=user.role.value
            )

            logger.info(f"Login successful: user_id={user.id}")

            response = LoginResponse(
                access_token=access_token
            )

            return ApiResponse(
                success=True,
                message=LOGIN_SUCCESS,
                data=response
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error during login: {error}")
            raise

    async def logout(
        self,
        user: User
    ) -> ApiResponse[None]:

        try:

            logger.info(f"Logout successful: user_id={user.id}")

            return ApiResponse(
                success=True,
                message=LOGOUT_SUCCESS,
                data=None
            )

        except Exception as error:
            logger.error(f"Unexpected error during logout: {error}")
            raise

    async def change_password(
        self,
        user: User,
        request: ChangePasswordRequest
    ) -> ApiResponse[None]:

        try:

            if not PasswordManager.verify_password(
                plain_password=request.old_password,
                hashed_password=user.password
            ):
                logger.warning(
                    f"Change password failed: "
                    f"incorrect old password (user_id={user.id})"
                )
                raise IncorrectOldPasswordException()

            if not PasswordManager.validate_password(
                password=request.new_password
            ):
                logger.warning(
                    f"Change password failed: "
                    f"invalid new password (user_id={user.id})"
                )
                raise InvalidPasswordException()

            if PasswordManager.verify_password(
                plain_password=request.new_password,
                hashed_password=user.password
            ):
                logger.warning(
                    f"Change password failed: "
                    f"same as old password (user_id={user.id})"
                )
                raise SamePasswordException()

            user.password = PasswordManager.hash_password(
                password=request.new_password
            )

            await self.user_repository.update(user=user)

            logger.info(
                f"Password changed successfully: user_id={user.id}"
            )

            return ApiResponse(
                success=True,
                message=PASSWORD_CHANGED_SUCCESS,
                data=None
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error during password change: {error}"
            )
            raise

    async def reset_password(
        self,
        request: ResetPasswordRequest
    ) -> ApiResponse[None]:

        try:

            user = await self.user_repository.get_by_email(
                email=request.email
            )

            if not user:
                logger.warning(
                    f"Reset password failed: "
                    f"user not found ({request.email})"
                )
                raise UserNotFoundException()

            if not PasswordManager.validate_password(
                password=request.new_password
            ):
                logger.warning(
                    f"Reset password failed: "
                    f"invalid new password ({request.email})"
                )
                raise InvalidPasswordException()

            if PasswordManager.verify_password(
                plain_password=request.new_password,
                hashed_password=user.password
            ):
                logger.warning(
                    f"Reset password failed: "
                    f"same as old password ({request.email})"
                )
                raise SamePasswordException()

            user.password = PasswordManager.hash_password(
                password=request.new_password
            )

            await self.user_repository.update(user=user)

            logger.info(
                f"Password reset successfully: user_id={user.id}"
            )

            return ApiResponse(
                success=True,
                message=PASSWORD_RESET_SUCCESS,
                data=None
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error during password reset: {error}"
            )
            raise