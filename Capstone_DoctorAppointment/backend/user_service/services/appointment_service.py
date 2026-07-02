from datetime import datetime

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
from user_service.schemas.response.api_response import ApiResponse
from user_service.schemas.response.appointment_response import (
    AppointmentResponse
)

from shared.exceptions import (
    SlotNotFoundException,
    SlotUnavailableException,
    PastSlotDateException
)

from shared.constants import APPOINTMENT_BOOKED

from shared.logger.logger import get_logger

logger = get_logger(__name__)


class AppointmentService:

    def __init__(self):
        self.appointment_repository = AppointmentRepository()
        self.slot_repository = AvailabilitySlotRepository()

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

            slot.is_booked = True
            slot.updated_at = datetime.utcnow()
            await self.slot_repository.update(slot=slot)

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

            response = AppointmentResponse(
                id=str(saved_appointment.id),
                patient_id=saved_appointment.patient_id,
                doctor_id=saved_appointment.doctor_id,
                slot_id=saved_appointment.slot_id,
                appointment_date=saved_appointment.appointment_date,
                start_time=saved_appointment.start_time,
                end_time=saved_appointment.end_time,
                status=saved_appointment.status,
                created_at=saved_appointment.created_at
            )

            return ApiResponse(
                success=True,
                message=APPOINTMENT_BOOKED,
                data=response
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error booking appointment: {error}")
            raise
