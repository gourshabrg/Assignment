from datetime import datetime, timedelta

from fastapi import HTTPException

from user_service.models.user_model import User
from user_service.models.appointment_model import Appointment

from user_service.repositories.appointment_repository import (
    AppointmentRepository
)
from user_service.repositories.availability_slot_repository import (
    AvailabilitySlotRepository
)

from user_service.schemas.request.book_appointment_request import (
    BookAppointmentRequest
)
from user_service.schemas.request.update_appointment_status_request import (
    UpdateAppointmentStatusRequest
)
from user_service.schemas.response.api_response import ApiResponse
from user_service.schemas.response.appointment_response import (
    AppointmentResponse
)

from shared.enums.appointment_status_enum import AppointmentStatus

from shared.exceptions import (
    SlotNotFoundException,
    SlotUnavailableException,
    PastSlotDateException,
    AppointmentNotFoundException,
    AppointmentNotCancellableException,
    CancellationWindowExpiredException,
    InvalidStatusUpdateException,
    StatusUpdateTooEarlyException,
    AppointmentNotUpdatableException,
    AccessDeniedException
)

from shared.constants import (
    APPOINTMENT_BOOKED,
    APPOINTMENTS_FETCHED,
    APPOINTMENT_CANCELLED,
    APPOINTMENT_STATUS_UPDATED
)

from shared.logger.logger import get_logger
from shared.utils.time_utils import str_to_time

logger = get_logger(__name__)

CANCELLATION_WINDOW = timedelta(hours=2)


class AppointmentService:

    def __init__(self):
        self.appointment_repository = AppointmentRepository()
        self.slot_repository = AvailabilitySlotRepository()

    def _build_response(
        self,
        appointment: Appointment
    ) -> AppointmentResponse:

        return AppointmentResponse(
            id=str(appointment.id),
            patient_id=appointment.patient_id,
            doctor_id=appointment.doctor_id,
            slot_id=appointment.slot_id,
            appointment_date=appointment.appointment_date,
            start_time=str_to_time(appointment.start_time),
            end_time=str_to_time(appointment.end_time),
            status=appointment.status,
            created_at=appointment.created_at
        )

    async def book_appointment(
        self,
        current_user: User,
        request: BookAppointmentRequest
    ) -> ApiResponse[AppointmentResponse]:

        try:

            slot = await self.slot_repository.get_by_id(
                slot_id=request.slot_id
            )

            if not slot:
                logger.warning(
                    f"Booking failed: slot not found "
                    f"slot_id={request.slot_id}"
                )
                raise SlotNotFoundException()

            if slot.is_booked:
                logger.warning(
                    f"Booking failed: slot already booked "
                    f"slot_id={slot.id}"
                )
                raise SlotUnavailableException()

            if slot.slot_date < datetime.utcnow().date():
                logger.warning(
                    f"Booking failed: slot date in the past "
                    f"slot_id={slot.id}"
                )
                raise PastSlotDateException()

            booked = await self.slot_repository.book_if_available(
                slot_id=str(slot.id)
            )

            if not booked:
                logger.warning(
                    f"Booking failed: slot taken by a concurrent "
                    f"request slot_id={slot.id}"
                )
                raise SlotUnavailableException()

            appointment = Appointment(
                patient_id=str(current_user.id),
                doctor_id=slot.doctor_id,
                slot_id=str(slot.id),
                appointment_date=slot.slot_date,
                start_time=slot.start_time,
                end_time=slot.end_time
            )

            saved_appointment = await self.appointment_repository.create(
                appointment=appointment
            )

            logger.info(
                f"Appointment booked: appointment_id={saved_appointment.id} "
                f"patient_id={current_user.id} slot_id={slot.id}"
            )

            return ApiResponse(
                success=True,
                message=APPOINTMENT_BOOKED,
                data=self._build_response(saved_appointment)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error booking appointment: {error}")
            raise

    async def get_my_appointments(
        self,
        current_user: User
    ) -> ApiResponse[list[AppointmentResponse]]:

        try:

            appointments = await self.appointment_repository.get_by_patient(
                patient_id=str(current_user.id)
            )

            return ApiResponse(
                success=True,
                message=APPOINTMENTS_FETCHED,
                data=[
                    self._build_response(appointment)
                    for appointment in appointments
                ]
            )

        except Exception as error:
            logger.error(f"Unexpected error fetching appointments: {error}")
            raise

    async def get_doctor_appointments(
        self,
        current_user: User
    ) -> ApiResponse[list[AppointmentResponse]]:

        try:

            appointments = await self.appointment_repository.get_by_doctor(
                doctor_id=str(current_user.id)
            )

            return ApiResponse(
                success=True,
                message=APPOINTMENTS_FETCHED,
                data=[
                    self._build_response(appointment)
                    for appointment in appointments
                ]
            )

        except Exception as error:
            logger.error(f"Unexpected error fetching appointments: {error}")
            raise

    async def cancel_appointment(
        self,
        current_user: User,
        appointment_id: str
    ) -> ApiResponse[AppointmentResponse]:

        try:

            appointment = await self.appointment_repository.get_by_id(
                appointment_id=appointment_id
            )

            if not appointment:
                logger.warning(
                    f"Cancel failed: appointment not found "
                    f"appointment_id={appointment_id}"
                )
                raise AppointmentNotFoundException()

            if appointment.patient_id != str(current_user.id):
                logger.warning(
                    f"Cancel failed: patient_id={current_user.id} "
                    f"does not own appointment_id={appointment_id}"
                )
                raise AccessDeniedException()

            if appointment.status != AppointmentStatus.BOOKED:
                logger.warning(
                    f"Cancel failed: status={appointment.status} "
                    f"appointment_id={appointment_id}"
                )
                raise AppointmentNotCancellableException()

            appointment_start = datetime.combine(
                appointment.appointment_date,
                str_to_time(appointment.start_time)
            )

            if appointment_start - datetime.utcnow() < CANCELLATION_WINDOW:
                logger.warning(
                    f"Cancel failed: within cancellation window "
                    f"appointment_id={appointment_id}"
                )
                raise CancellationWindowExpiredException()

            appointment.status = AppointmentStatus.CANCELLED
            appointment.updated_at = datetime.utcnow()

            updated_appointment = await self.appointment_repository.update(
                appointment=appointment
            )

            slot = await self.slot_repository.get_by_id(
                slot_id=appointment.slot_id
            )

            if slot:
                slot.is_booked = False
                slot.updated_at = datetime.utcnow()
                await self.slot_repository.update(slot=slot)

            logger.info(f"Appointment cancelled: appointment_id={appointment_id}")

            return ApiResponse(
                success=True,
                message=APPOINTMENT_CANCELLED,
                data=self._build_response(updated_appointment)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error cancelling appointment: {error}")
            raise

    async def update_status(
        self,
        current_user: User,
        appointment_id: str,
        request: UpdateAppointmentStatusRequest
    ) -> ApiResponse[AppointmentResponse]:

        try:

            if request.status not in (
                AppointmentStatus.COMPLETED,
                AppointmentStatus.NO_SHOW
            ):
                raise InvalidStatusUpdateException()

            appointment = await self.appointment_repository.get_by_id(
                appointment_id=appointment_id
            )

            if not appointment:
                logger.warning(
                    f"Status update failed: appointment not found "
                    f"appointment_id={appointment_id}"
                )
                raise AppointmentNotFoundException()

            if appointment.doctor_id != str(current_user.id):
                logger.warning(
                    f"Status update failed: doctor_id={current_user.id} "
                    f"does not own appointment_id={appointment_id}"
                )
                raise AccessDeniedException()

            if appointment.status != AppointmentStatus.BOOKED:
                logger.warning(
                    f"Status update failed: status={appointment.status} "
                    f"appointment_id={appointment_id}"
                )
                raise AppointmentNotUpdatableException()

            appointment_end = datetime.combine(
                appointment.appointment_date,
                str_to_time(appointment.end_time)
            )

            if datetime.utcnow() < appointment_end:
                logger.warning(
                    f"Status update failed: too early "
                    f"appointment_id={appointment_id}"
                )
                raise StatusUpdateTooEarlyException()

            appointment.status = request.status
            appointment.updated_at = datetime.utcnow()

            updated_appointment = await self.appointment_repository.update(
                appointment=appointment
            )

            logger.info(
                f"Appointment status updated: appointment_id={appointment_id} "
                f"status={request.status}"
            )

            return ApiResponse(
                success=True,
                message=APPOINTMENT_STATUS_UPDATED,
                data=self._build_response(updated_appointment)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error updating appointment status: {error}"
            )
            raise
