from unittest.mock import AsyncMock
import pytest
from beanie import PydanticObjectId
from enums.appointment_status_enum import AppointmentStatus
from models.payment_model import Payment
from exceptions import (
    AppointmentNotFoundException,
    AccessDeniedException,
    PaymentNotAllowedException,
    PaymentAlreadyExistsException,
    DoctorProfileNotFoundException
)
from services.payment_service import PaymentService
from tests.conftest import APPOINTMENT_ID


@pytest.fixture
def service() -> PaymentService:
    payment_service = PaymentService()
    payment_service.appointment_repository = AsyncMock()
    payment_service.doctor_repository = AsyncMock()
    payment_service.payment_repository = AsyncMock()

    return payment_service


@pytest.fixture
def pending_appointment(appointment):
    appointment.status = AppointmentStatus.PENDING_PAYMENT

    return appointment


@pytest.fixture
def saved_payment(patient):
    payment = Payment(
        appointment_id=APPOINTMENT_ID,
        patient_id=str(patient.id),
        amount=700
    )
    payment.id = PydanticObjectId("6a66666666666666666666f6")

    return payment


class TestPayForAppointment:

    async def test_payment_confirms_the_appointment(
        self, service, patient, pending_appointment, doctor_profile, saved_payment
    ):
        service.appointment_repository.get_by_id.return_value = (
            pending_appointment
        )
        service.appointment_repository.update.return_value = pending_appointment
        service.payment_repository.get_by_appointment_id.return_value = None
        service.doctor_repository.get_by_user_id.return_value = doctor_profile
        service.payment_repository.create.return_value = saved_payment

        response = await service.pay_for_appointment(
            current_user=patient,
            appointment_id=APPOINTMENT_ID
        )

        assert response.success is True
        assert pending_appointment.status == AppointmentStatus.BOOKED
        service.payment_repository.create.assert_awaited_once()

    async def test_rejects_unknown_appointment(self, service, patient):
        service.appointment_repository.get_by_id.return_value = None

        with pytest.raises(AppointmentNotFoundException):
            await service.pay_for_appointment(
                current_user=patient,
                appointment_id=APPOINTMENT_ID
            )

    async def test_rejects_other_patients_appointment(
        self, service, doctor, pending_appointment
    ):
        service.appointment_repository.get_by_id.return_value = (
            pending_appointment
        )

        with pytest.raises(AccessDeniedException):
            await service.pay_for_appointment(
                current_user=doctor,
                appointment_id=APPOINTMENT_ID
            )

    async def test_rejects_appointment_not_awaiting_payment(
        self, service, patient, appointment
    ):
        appointment.status = AppointmentStatus.BOOKED
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(PaymentNotAllowedException):
            await service.pay_for_appointment(
                current_user=patient,
                appointment_id=APPOINTMENT_ID
            )

    async def test_rejects_duplicate_payment(
        self, service, patient, pending_appointment
    ):
        service.appointment_repository.get_by_id.return_value = (
            pending_appointment
        )
        service.payment_repository.get_by_appointment_id.return_value = object()

        with pytest.raises(PaymentAlreadyExistsException):
            await service.pay_for_appointment(
                current_user=patient,
                appointment_id=APPOINTMENT_ID
            )

    async def test_rejects_missing_doctor_profile(
        self, service, patient, pending_appointment
    ):
        service.appointment_repository.get_by_id.return_value = (
            pending_appointment
        )
        service.payment_repository.get_by_appointment_id.return_value = None
        service.doctor_repository.get_by_user_id.return_value = None

        with pytest.raises(DoctorProfileNotFoundException):
            await service.pay_for_appointment(
                current_user=patient,
                appointment_id=APPOINTMENT_ID
            )
