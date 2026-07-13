from datetime import datetime

from fastapi import HTTPException

from models.user_model import User

from repositories.doctor_profile_repository import DoctorProfileRepository

from schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)
from schemas.response.api_response import ApiResponse
from schemas.response.doctor_profile_response import (
    DoctorProfileResponse
)

from exceptions import (
    DoctorProfileNotFoundException,
    NoFieldsToUpdateException
)

from constants import (
    DOCTOR_PROFILE_FETCHED,
    DOCTOR_PROFILE_UPDATED
)

from logger.logger import get_logger

logger = get_logger(__name__)


class DoctorService:
    """Business logic for a doctor viewing/updating their own profile."""

    def __init__(self):
        self.doctor_repository = DoctorProfileRepository()

    def _build_response(self, user: User, profile) -> DoctorProfileResponse:
        """Merges the User and DoctorProfile documents into one response."""

        return DoctorProfileResponse(
            user_id=str(user.id),
            full_name=user.full_name,
            email=user.email,
            phone=user.phone,
            qualification=profile.qualification,
            specialization=profile.specialization,
            experience=profile.experience,
            license_number=profile.license_number,
            consultation_fee=profile.consultation_fee,
            clinic_address=profile.clinic_address,
            is_active=user.is_active,
            created_at=profile.created_at,
            updated_at=profile.updated_at
        )

    async def get_my_profile(
        self,
        current_user: User
    ) -> ApiResponse[DoctorProfileResponse]:
        """Returns the logged-in doctor's own profile."""

        try:

            profile = await self.doctor_repository.get_by_user_id(
                user_id=str(current_user.id)
            )

            if not profile:
                logger.warning(
                    f"Doctor profile not found: user_id={current_user.id}"
                )
                raise DoctorProfileNotFoundException()

            return ApiResponse(
                success=True,
                message=DOCTOR_PROFILE_FETCHED,
                data=self._build_response(current_user, profile)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error fetching doctor profile: {error}"
            )
            raise

    async def update_my_profile(
        self,
        current_user: User,
        request: DoctorProfileUpdateRequest
    ) -> ApiResponse[DoctorProfileResponse]:
        """Updates the logged-in doctor's profile."""

        try:

            profile = await self.doctor_repository.get_by_user_id(
                user_id=str(current_user.id)
            )

            if not profile:
                logger.warning(
                    f"Doctor profile update failed: profile not found "
                    f"(user_id={current_user.id})"
                )
                raise DoctorProfileNotFoundException()

            update_fields = request.model_dump(exclude_none=True)

            if not update_fields:
                logger.warning(
                    f"Doctor profile update failed: no fields provided "
                    f"(user_id={current_user.id})"
                )
                raise NoFieldsToUpdateException()

            for field, value in update_fields.items():
                setattr(profile, field, value)

            profile.updated_at = datetime.utcnow()

            updated_profile = await self.doctor_repository.update(
                profile=profile
            )

            logger.info(
                f"Doctor profile updated: user_id={current_user.id}"
            )

            return ApiResponse(
                success=True,
                message=DOCTOR_PROFILE_UPDATED,
                data=self._build_response(current_user, updated_profile)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error updating doctor profile: {error}"
            )
            raise
