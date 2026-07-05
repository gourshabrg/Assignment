from fastapi import HTTPException

from user_service.models.user_model import User

from user_service.repositories.user_repository import UserRepository
from user_service.repositories.doctor_profile_repository import (
    DoctorProfileRepository
)
from user_service.repositories.appointment_repository import (
    AppointmentRepository
)

from user_service.schemas.response.api_response import ApiResponse
from user_service.schemas.response.admin_doctor_response import (
    AdminDoctorResponse
)
from user_service.schemas.response.dashboard_response import (
    DashboardResponse
)

from shared.enums.role_enum import UserRole
from shared.enums.appointment_status_enum import AppointmentStatus

from shared.exceptions import (
    UserNotFoundException,
    DoctorProfileNotFoundException
)

from shared.constants import (
    DOCTORS_FETCHED,
    DOCTOR_ACTIVATED,
    DOCTOR_DEACTIVATED,
    DASHBOARD_FETCHED
)

from shared.logger.logger import get_logger

logger = get_logger(__name__)


class AdminService:

    def __init__(self):
        self.user_repository = UserRepository()
        self.doctor_repository = DoctorProfileRepository()
        self.appointment_repository = AppointmentRepository()

    async def list_doctors(
        self
    ) -> ApiResponse[list[AdminDoctorResponse]]:

        try:

            doctors = await self.user_repository.get_by_role(
                role=UserRole.DOCTOR
            )

            response = []

            for doctor in doctors:

                profile = await self.doctor_repository.get_by_user_id(
                    user_id=str(doctor.id)
                )

                if not profile:
                    continue

                response.append(
                    AdminDoctorResponse(
                        doctor_id=str(doctor.id),
                        full_name=doctor.full_name,
                        email=doctor.email,
                        phone=doctor.phone,
                        specialization=profile.specialization,
                        qualification=profile.qualification,
                        consultation_fee=profile.consultation_fee,
                        is_active=doctor.is_active
                    )
                )

            return ApiResponse(
                success=True,
                message=DOCTORS_FETCHED,
                data=response
            )

        except Exception as error:
            logger.error(f"Unexpected error listing doctors: {error}")
            raise

    async def _set_doctor_active_state(
        self,
        doctor_id: str,
        is_active: bool
    ) -> User:

        doctor = await self.user_repository.get_by_id(
            user_id=doctor_id
        )

        if not doctor or doctor.role != UserRole.DOCTOR:
            logger.warning(f"Doctor not found: doctor_id={doctor_id}")
            raise UserNotFoundException()

        profile = await self.doctor_repository.get_by_user_id(
            user_id=doctor_id
        )

        if not profile:
            logger.warning(
                f"Doctor profile not found: doctor_id={doctor_id}"
            )
            raise DoctorProfileNotFoundException()

        doctor.is_active = is_active

        return await self.user_repository.update(user=doctor)

    async def activate_doctor(
        self,
        doctor_id: str
    ) -> ApiResponse[None]:

        try:

            await self._set_doctor_active_state(
                doctor_id=doctor_id,
                is_active=True
            )

            logger.info(f"Doctor activated: doctor_id={doctor_id}")

            return ApiResponse(
                success=True,
                message=DOCTOR_ACTIVATED,
                data=None
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error activating doctor: {error}")
            raise

    async def deactivate_doctor(
        self,
        doctor_id: str
    ) -> ApiResponse[None]:

        try:

            await self._set_doctor_active_state(
                doctor_id=doctor_id,
                is_active=False
            )

            logger.info(f"Doctor deactivated: doctor_id={doctor_id}")

            return ApiResponse(
                success=True,
                message=DOCTOR_DEACTIVATED,
                data=None
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error deactivating doctor: {error}")
            raise

    async def get_dashboard(
        self
    ) -> ApiResponse[DashboardResponse]:

        try:

            total_doctors = await self.user_repository.count_by_role(
                role=UserRole.DOCTOR
            )

            total_patients = await self.user_repository.count_by_role(
                role=UserRole.PATIENT
            )

            total_appointments = (
                await self.appointment_repository.count_all()
            )

            completed_appointments = (
                await self.appointment_repository.count_by_status(
                    status=AppointmentStatus.COMPLETED
                )
            )

            cancelled_appointments = (
                await self.appointment_repository.count_by_status(
                    status=AppointmentStatus.CANCELLED
                )
            )

            response = DashboardResponse(
                total_doctors=total_doctors,
                total_patients=total_patients,
                total_appointments=total_appointments,
                completed_appointments=completed_appointments,
                cancelled_appointments=cancelled_appointments
            )

            return ApiResponse(
                success=True,
                message=DASHBOARD_FETCHED,
                data=response
            )

        except Exception as error:
            logger.error(f"Unexpected error fetching dashboard: {error}")
            raise
