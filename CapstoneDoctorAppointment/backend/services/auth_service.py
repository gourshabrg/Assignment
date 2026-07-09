from fastapi import HTTPException

from models.user_model import User
from models.doctor_profile_model import DoctorProfile

from repositories.user_repository import UserRepository
from repositories.doctor_profile_repository import (
    DoctorProfileRepository
)

from schemas.request.patient_register_request import (
    PatientRegisterRequest
)
from schemas.request.doctor_register_request import (
    DoctorRegisterRequest
)

from schemas.response.api_response import ApiResponse
from schemas.response.user_response import UserResponse

from utils.password import PasswordManager

from enums.role_enum import UserRole

from exceptions import (
    UserAlreadyExistsException,
    InvalidPasswordException
)

from constants import (
    PATIENT_REGISTER_SUCCESS,
    DOCTOR_REGISTER_SUCCESS
)

from logger.logger import get_logger

logger = get_logger(__name__)


class AuthService:
    """Registration business logic: validation, password hashing,
    and building the saved records.
    """

    def __init__(self):
        self.user_repository = UserRepository()
        self.doctor_repository = DoctorProfileRepository()

    def _build_user_response(self, user: User) -> UserResponse:
        """Turns a saved User document into the public response shape."""

        return UserResponse(
            id=str(user.id),
            full_name=user.full_name,
            email=user.email,
            phone=user.phone,
            role=user.role,
            is_active=user.is_active,
            created_at=user.created_at
        )

    async def register_patient(
        self,
        request: PatientRegisterRequest
    ) -> ApiResponse[UserResponse]:
        """Creates a new PATIENT account."""

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

            return ApiResponse(
                success=True,
                message=PATIENT_REGISTER_SUCCESS,
                data=self._build_user_response(saved_user)
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
        """Creates a new DOCTOR account plus its DoctorProfile.

        The account starts inactive (is_active=False) until an admin
        approves it -- doctors can't log in until then.
        """

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
                is_active=False
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

            return ApiResponse(
                success=True,
                message=DOCTOR_REGISTER_SUCCESS,
                data=self._build_user_response(saved_user)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error during doctor registration: {error}"
            )
            raise
