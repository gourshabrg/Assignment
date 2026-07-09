from models.user_model import User
from repositories.user_repository import UserRepository
from repositories.doctor_profile_repository import DoctorProfileRepository
from repositories.appointment_repository import AppointmentRepository
from schemas.response.api_response import ApiResponse
from schemas.response.admin_doctor_response import AdminDoctorResponse
from schemas.response.admin_appointment_response import (
    AdminAppointmentResponse
)
from schemas.response.dashboard_response import DashboardResponse
from enums.role_enum import UserRole
from enums.appointment_status_enum import AppointmentStatus
from exceptions import UserNotFoundException, DoctorProfileNotFoundException
from constants import (
    DOCTORS_FETCHED,
    DOCTOR_VERIFIED,
    DOCTOR_REJECTED,
    DASHBOARD_FETCHED
)
from logger.logger import get_logger

logger = get_logger(__name__)


class AdminService:
    """Business logic for admin managing doctors and viewing platform
    stats. Every method here requires the ADMIN role, checked at the
    router level.
    """

    def __init__(self):
        self.user_repository = UserRepository()
        self.doctor_repository = DoctorProfileRepository()
        self.appointment_repository = AppointmentRepository()

    async def list_doctors(self) -> ApiResponse[list[AdminDoctorResponse]]:
        """Lists every doctor account, active or still pending."""

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
                    is_verified=doctor.is_active
                )
            )

        return ApiResponse(
            success=True,
            message=DOCTORS_FETCHED,
            data=response
        )

    async def _set_doctor_active_state(
        self,
        doctor_id: str,
        is_active: bool
    ) -> User:

        doctor = await self.user_repository.get_by_id(user_id=doctor_id)

        if not doctor or doctor.role != UserRole.DOCTOR:
            logger.warning(f"Doctor not found: doctor_id={doctor_id}")
            raise UserNotFoundException()

        profile = await self.doctor_repository.get_by_user_id(
            user_id=doctor_id
        )

        if not profile:
            logger.warning(f"Doctor profile not found: doctor_id={doctor_id}")
            raise DoctorProfileNotFoundException()

        doctor.is_active = is_active

        return await self.user_repository.update(user=doctor)

    async def verify_doctor(self, doctor_id: str) -> ApiResponse[None]:
        """Approves a pending doctor, letting them log in and appear
        in patient search.
        """

        await self._set_doctor_active_state(
            doctor_id=doctor_id,
            is_active=True
        )

        logger.info(f"Doctor verified: doctor_id={doctor_id}")

        return ApiResponse(success=True, message=DOCTOR_VERIFIED, data=None)

    async def reject_doctor(self, doctor_id: str) -> ApiResponse[None]:
        """Deactivates a doctor -- blocks login and hides them from
        patient search.
        """

        await self._set_doctor_active_state(
            doctor_id=doctor_id,
            is_active=False
        )

        logger.info(f"Doctor rejected: doctor_id={doctor_id}")

        return ApiResponse(success=True, message=DOCTOR_REJECTED, data=None)

    async def get_dashboard(self) -> ApiResponse[DashboardResponse]:
        """Platform stats plus the doctor list and recent appointments,
        each with real names, not just counts.
        """

        total_doctors = await self.user_repository.count_by_role(
            role=UserRole.DOCTOR
        )
        total_patients = await self.user_repository.count_by_role(
            role=UserRole.PATIENT
        )
        total_appointments = await self.appointment_repository.count_all()
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

        doctors_response = (await self.list_doctors()).data

        recent_appointments = await self.appointment_repository.get_recent(
            limit=20
        )

        user_ids = list({
            user_id
            for appointment in recent_appointments
            for user_id in (appointment.patient_id, appointment.doctor_id)
        })
        users_by_id = {
            str(user.id): user
            for user in await self.user_repository.get_by_ids(
                user_ids=user_ids
            )
        }

        recent_appointments_response = [
            AdminAppointmentResponse(
                id=str(appointment.id),
                patient_name=users_by_id[appointment.patient_id].full_name,
                doctor_name=users_by_id[appointment.doctor_id].full_name,
                appointment_date=appointment.appointment_date,
                status=appointment.status
            )
            for appointment in recent_appointments
            if appointment.patient_id in users_by_id
            and appointment.doctor_id in users_by_id
        ]

        response = DashboardResponse(
            total_doctors=total_doctors,
            total_patients=total_patients,
            total_appointments=total_appointments,
            completed_appointments=completed_appointments,
            cancelled_appointments=cancelled_appointments,
            doctors=doctors_response,
            recent_appointments=recent_appointments_response
        )

        return ApiResponse(
            success=True,
            message=DASHBOARD_FETCHED,
            data=response
        )
