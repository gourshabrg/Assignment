from fastapi import HTTPException
from enums.appointment_status_enum import AppointmentStatus
from models.user_model import User
from models.payment_model import Payment
from repositories.appointment_repository import AppointmentRepository
from repositories.doctor_profile_repository import DoctorProfileRepository
from repositories.payment_repository import PaymentRepository
from schemas.response.api_response import ApiResponse
from schemas.response.payment_response import PaymentResponse
from exceptions import (
    AppointmentNotFoundException,
    PaymentAlreadyExistsException,
    PaymentNotAllowedException,
    DoctorProfileNotFoundException,
    AccessDeniedException
)
from constants import PAYMENT_SUCCESS
from logger.logger import get_logger

logger = get_logger(__name__)


class PaymentService:
    """Business logic for appointment payments."""

    def __init__(self):
        self.appointment_repository = AppointmentRepository()
        self.doctor_repository = DoctorProfileRepository()
        self.payment_repository = PaymentRepository()

    def _build_response(self, payment: Payment) -> PaymentResponse:

        return PaymentResponse(
            id=str(payment.id),
            appointment_id=payment.appointment_id,
            patient_id=payment.patient_id,
            amount=payment.amount,
            status=payment.status,
            created_at=payment.created_at
        )

    async def pay_for_appointment(
        self,
        current_user: User,
        appointment_id: str
    ) -> ApiResponse[PaymentResponse]:

        try:

            appointment = await self.appointment_repository.get_by_id(
                appointment_id=appointment_id
            )

            if not appointment:
                logger.warning(
                    f"Payment failed: appointment not found "
                    f"appointment_id={appointment_id}"
                )
                raise AppointmentNotFoundException()

            if appointment.patient_id != str(current_user.id):
                logger.warning(
                    f"Payment failed: patient_id={current_user.id} "
                    f"does not own appointment_id={appointment_id}"
                )
                raise AccessDeniedException()

            if appointment.status != AppointmentStatus.PENDING_PAYMENT:
                logger.warning(
                    f"Payment failed: status={appointment.status} "
                    f"appointment_id={appointment_id}"
                )
                raise PaymentNotAllowedException()

            existing_payment = (
                await self.payment_repository.get_by_appointment_id(
                    appointment_id=appointment_id
                )
            )

            if existing_payment:
                logger.warning(
                    f"Payment failed: already paid "
                    f"appointment_id={appointment_id}"
                )
                raise PaymentAlreadyExistsException()

            doctor_profile = await self.doctor_repository.get_by_user_id(
                user_id=appointment.doctor_id
            )

            if not doctor_profile:
                logger.warning(
                    f"Payment failed: doctor profile not found "
                    f"doctor_id={appointment.doctor_id}"
                )
                raise DoctorProfileNotFoundException()

            payment = Payment(
                appointment_id=appointment_id,
                patient_id=str(current_user.id),
                amount=doctor_profile.consultation_fee
            )

            saved_payment = await self.payment_repository.create(
                payment=payment
            )

            appointment.status = AppointmentStatus.BOOKED
            await self.appointment_repository.update(
                appointment=appointment
            )

            logger.info(
                f"Payment completed: payment_id={saved_payment.id} "
                f"appointment_id={appointment_id}"
            )

            return ApiResponse(
                success=True,
                message=PAYMENT_SUCCESS,
                data=self._build_response(saved_payment)
            )

        except HTTPException:
            raise

        except Exception as error:
            logger.error(f"Unexpected error processing payment: {error}")
            raise
