from datetime import datetime

from fastapi import HTTPException

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
from schemas.response.api_response import ApiResponse
from schemas.response.appointment_response import AppointmentResponse

from exceptions import (
    SlotNotFoundException,
    SlotUnavailableException,
    PastSlotDateException
)

from constants import APPOINTMENT_BOOKED

from logger.logger import get_logger
from utils.time_utils import str_to_time

logger = get_logger(__name__)


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
