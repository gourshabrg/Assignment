from datetime import datetime

from fastapi import HTTPException

from user_service.models.user_model import User

from user_service.repositories.doctor_profile_repository import (
    DoctorProfileRepository
)
from user_service.repositories.user_repository import UserRepository
from user_service.repositories.availability_slot_repository import (
    AvailabilitySlotRepository
)

from user_service.schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)

from user_service.schemas.response.api_response import ApiResponse
from user_service.schemas.response.doctor_profile_response import (
    DoctorProfileResponse
)
from user_service.schemas.response.doctor_search_response import (
    DoctorSearchResponse,
    DoctorDetailResponse
)
from user_service.schemas.response.availability_slot_response import (
    AvailabilitySlotResponse
)

from shared.exceptions import (
    DoctorProfileNotFoundException,
    NoFieldsToUpdateException
)

from shared.constants import (
    DOCTOR_PROFILE_FETCHED,
    DOCTOR_PROFILE_UPDATED,
    DOCTORS_FETCHED,
    DOCTOR_DETAILS_FETCHED
)

from shared.logger.logger import get_logger
from shared.utils.time_utils import str_to_time

logger = get_logger(__name__)


class DoctorService:

    def __init__(self):
        self.doctor_repository = DoctorProfileRepository()
        self.user_repository = UserRepository()
        self.slot_repository = AvailabilitySlotRepository()

    def _build_response(
        self,
        user: User,
        profile
    ) -> DoctorProfileResponse:

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

        try:

            profile = await self.doctor_repository.get_by_user_id(
                user_id=str(current_user.id)
            )

            if not profile:
                logger.warning(
                    f"Doctor profile not found: "
                    f"user_id={current_user.id}"
                )
                raise DoctorProfileNotFoundException()

            response = self._build_response(
                user=current_user,
                profile=profile
            )

            return ApiResponse(
                success=True,
                message=DOCTOR_PROFILE_FETCHED,
                data=response
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

            response = self._build_response(
                user=current_user,
                profile=updated_profile
            )

            return ApiResponse(
                success=True,
                message=DOCTOR_PROFILE_UPDATED,
                data=response
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error updating doctor profile: {error}"
            )
            raise

    async def search_doctors(
        self,
        name: str | None = None,
        specialization: str | None = None,
        location: str | None = None,
        min_experience: int | None = None,
        max_fee: float | None = None
    ) -> ApiResponse[list[DoctorSearchResponse]]:

        try:

            profiles = await self.doctor_repository.search(
                specialization=specialization,
                location=location,
                min_experience=min_experience,
                max_fee=max_fee
            )

            profiles_by_user_id = {
                profile.user_id: profile for profile in profiles
            }

            if not profiles_by_user_id:
                return ApiResponse(
                    success=True,
                    message=DOCTORS_FETCHED,
                    data=[]
                )

            users = await self.user_repository.get_active_doctors_by_ids(
                user_ids=list(profiles_by_user_id.keys()),
                name=name
            )

            response = [
                DoctorSearchResponse(
                    doctor_id=str(user.id),
                    full_name=user.full_name,
                    qualification=profiles_by_user_id[str(user.id)]
                    .qualification,
                    specialization=profiles_by_user_id[str(user.id)]
                    .specialization,
                    experience=profiles_by_user_id[str(user.id)]
                    .experience,
                    consultation_fee=profiles_by_user_id[str(user.id)]
                    .consultation_fee,
                    clinic_address=profiles_by_user_id[str(user.id)]
                    .clinic_address
                )
                for user in users
            ]

            logger.info(f"Doctor search returned {len(response)} results")

            return ApiResponse(
                success=True,
                message=DOCTORS_FETCHED,
                data=response
            )

        except Exception as error:
            logger.error(f"Unexpected error searching doctors: {error}")
            raise

    async def get_doctor_by_id(
        self,
        doctor_id: str
    ) -> ApiResponse[DoctorDetailResponse]:

        try:

            profile = await self.doctor_repository.get_by_user_id(
                user_id=doctor_id
            )

            if not profile:
                logger.warning(f"Doctor not found: doctor_id={doctor_id}")
                raise DoctorProfileNotFoundException()

            users = await self.user_repository.get_active_doctors_by_ids(
                user_ids=[doctor_id]
            )

            if not users:
                logger.warning(
                    f"Doctor not found or inactive: doctor_id={doctor_id}"
                )
                raise DoctorProfileNotFoundException()

            user = users[0]

            available_slots = (
                await self.slot_repository.get_available_by_doctor(
                    doctor_id=doctor_id,
                    from_date=datetime.utcnow().date()
                )
            )

            response = DoctorDetailResponse(
                doctor_id=doctor_id,
                full_name=user.full_name,
                qualification=profile.qualification,
                specialization=profile.specialization,
                experience=profile.experience,
                consultation_fee=profile.consultation_fee,
                clinic_address=profile.clinic_address,
                available_slots=[
                    AvailabilitySlotResponse(
                        id=str(slot.id),
                        doctor_id=slot.doctor_id,
                        slot_date=slot.slot_date,
                        start_time=str_to_time(slot.start_time),
                        end_time=str_to_time(slot.end_time),
                        is_booked=slot.is_booked,
                        created_at=slot.created_at,
                        updated_at=slot.updated_at
                    )
                    for slot in available_slots
                ]
            )

            return ApiResponse(
                success=True,
                message=DOCTOR_DETAILS_FETCHED,
                data=response
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error fetching doctor details: {error}"
            )
            raise
