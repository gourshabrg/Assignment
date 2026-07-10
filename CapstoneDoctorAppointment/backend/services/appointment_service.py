from datetime import datetime, timedelta

from fastapi import HTTPException

from enums.appointment_status_enum import AppointmentStatus
from models.user_model import User
from models.appointment_model import Appointment
from repositories.appointment_repository import AppointmentRepository
from repositories.availability_slot_repository import (
    AvailabilitySlotRepository
)
from repositories.user_repository import UserRepository
from schemas.request.book_appointment_request import (
    BookAppointmentRequest
)
from schemas.request.update_appointment_status_request import (
    UpdateAppointmentStatusRequest
)
from schemas.request.cancel_appointment_request import (
    CancelAppointmentRequest
)
from schemas.response.api_response import ApiResponse
from schemas.response.appointment_response import AppointmentResponse
from exceptions import (
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
from constants import (
    APPOINTMENT_BOOKED,
    APPOINTMENTS_FETCHED,
    APPOINTMENT_CANCELLED,
    CANCELLATION_REQUESTED,
    APPOINTMENT_STATUS_UPDATED
)
from logger.logger import get_logger
from utils.time_utils import str_to_time

logger = get_logger(__name__)

CANCELLATION_WINDOW = timedelta(hours=2)


class AppointmentService:
    """Business logic for a patient booking an appointment."""

    def __init__(self):
        self.appointment_repository = AppointmentRepository()
        self.slot_repository = AvailabilitySlotRepository()
        self.user_repository = UserRepository()

    async def _get_user_names(
        self,
        user_ids: set[str]
    ) -> dict[str, str]:
        """Maps user ids to full names, fetching each id once."""

        names = {}

        for user_id in user_ids:
            user = await self.user_repository.get_by_id(user_id=user_id)

            if user:
                names[user_id] = user.full_name

        return names

    def _build_response(
        self,
        appointment: Appointment,
        names: dict[str, str] | None = None
    ) -> AppointmentResponse:

        names = names or {}

        return AppointmentResponse(
            id=str(appointment.id),
            patient_id=appointment.patient_id,
            patient_name=names.get(appointment.patient_id),
            doctor_id=appointment.doctor_id,
            doctor_name=names.get(appointment.doctor_id),
            slot_id=appointment.slot_id,
            appointment_date=appointment.appointment_date,
            start_time=str_to_time(appointment.start_time),
            end_time=str_to_time(appointment.end_time),
            status=appointment.status,
            cancellation_reason=appointment.cancellation_reason,
            created_at=appointment.created_at
        )

    async def book_appointment(
        self,
        current_user: User,
        request: BookAppointmentRequest
    ) -> ApiResponse[AppointmentResponse]:
        """Books an available slot for the logged-in patient."""

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

            names = await self._get_user_names(
                user_ids={
                    saved_appointment.patient_id,
                    saved_appointment.doctor_id
                }
            )

            return ApiResponse(
                success=True,
                message=APPOINTMENT_BOOKED,
                data=self._build_response(saved_appointment, names)
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
        """Patient views their own appointments, newest first."""

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
        """Doctor views their own appointments, newest first."""

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
        """Patient cancels their own appointment."""

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
                    f"Cancel failed: user_id={current_user.id} does not own "
                    f"appointment_id={appointment_id}"
                )
                raise AccessDeniedException()

            if appointment.status not in (
                AppointmentStatus.PENDING_PAYMENT,
                AppointmentStatus.BOOKED
            ):
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

            await self._free_slot(slot_id=appointment.slot_id)

            logger.info(
                f"Appointment cancelled: appointment_id={appointment_id} "
                f"cancelled_by=patient_id={current_user.id}"
            )

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

    async def _free_slot(self, slot_id: str) -> None:
        """Marks the slot bookable again after a cancellation."""

        slot = await self.slot_repository.get_by_id(slot_id=slot_id)

        if slot:
            slot.is_booked = False
            slot.updated_at = datetime.utcnow()
            await self.slot_repository.update(slot=slot)

    async def request_cancellation(
        self,
        current_user: User,
        appointment_id: str,
        request: CancelAppointmentRequest
    ) -> ApiResponse[AppointmentResponse]:
        """Doctor requests cancellation with a reason for admin approval."""

        try:

            appointment = await self.appointment_repository.get_by_id(
                appointment_id=appointment_id
            )

            if not appointment:
                logger.warning(
                    f"Cancellation request failed: appointment not found "
                    f"appointment_id={appointment_id}"
                )
                raise AppointmentNotFoundException()

            if appointment.doctor_id != str(current_user.id):
                logger.warning(
                    f"Cancellation request failed: doctor_id="
                    f"{current_user.id} does not own "
                    f"appointment_id={appointment_id}"
                )
                raise AccessDeniedException()

            if appointment.status not in (
                AppointmentStatus.PENDING_PAYMENT,
                AppointmentStatus.BOOKED
            ):
                logger.warning(
                    f"Cancellation request failed: status="
                    f"{appointment.status} appointment_id={appointment_id}"
                )
                raise AppointmentNotCancellableException()

            appointment.status = AppointmentStatus.CANCELLATION_REQUESTED
            appointment.cancellation_reason = request.reason
            appointment.updated_at = datetime.utcnow()

            updated_appointment = await self.appointment_repository.update(
                appointment=appointment
            )

            logger.info(
                f"Cancellation requested: appointment_id={appointment_id} "
                f"doctor_id={current_user.id}"
            )

            return ApiResponse(
                success=True,
                message=CANCELLATION_REQUESTED,
                data=self._build_response(updated_appointment)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(
                f"Unexpected error requesting cancellation: {error}"
            )
            raise

    async def update_status(
        self,
        current_user: User,
        appointment_id: str,
        request: UpdateAppointmentStatusRequest
    ) -> ApiResponse[AppointmentResponse]:
        """Doctor marks their appointment COMPLETED or NOT_ATTENDED."""

        try:

            if request.status not in (
                AppointmentStatus.COMPLETED,
                AppointmentStatus.NOT_ATTENDED
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
                f"Appointment status updated: appointment_id="
                f"{appointment_id} status={request.status}"
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
