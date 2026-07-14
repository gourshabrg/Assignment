from datetime import date, datetime, timedelta
from unittest.mock import AsyncMock
import pytest
from enums.appointment_status_enum import AppointmentStatus
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
    AccessDeniedException,
    NoPendingCancellationException
)
from schemas.request.book_appointment_request import BookAppointmentRequest
from schemas.request.cancel_appointment_request import CancelAppointmentRequest
from schemas.request.update_appointment_status_request import (
    UpdateAppointmentStatusRequest
)
from services.appointment_service import AppointmentService
from tests.conftest import DOCTOR_ID, SLOT_ID


@pytest.fixture
def service(patient, doctor) -> AppointmentService:
    appointment_service = AppointmentService()
    appointment_service.appointment_repository = AsyncMock()
    appointment_service.slot_repository = AsyncMock()
    appointment_service.user_repository = AsyncMock()

    appointment_service.user_repository.get_by_id.side_effect = (
        lambda user_id: doctor if user_id == str(doctor.id) else patient
    )

    return appointment_service


class TestBookAppointment:
    """test case: Appointment Booking."""

    async def test_books_available_slot_as_pending_payment(
        self, service, patient, future_slot, appointment
    ):
        service.slot_repository.get_by_id.return_value = future_slot
        service.slot_repository.book_if_available.return_value = True
        service.appointment_repository.create.return_value = appointment

        response = await service.book_appointment(
            current_user=patient,
            request=BookAppointmentRequest(slot_id=SLOT_ID)
        )

        assert response.success is True
        assert response.data.doctor_name == "Anita Sharma"

        created = service.appointment_repository.create.call_args.kwargs[
            "appointment"
        ]
        assert created.status == AppointmentStatus.PENDING_PAYMENT

    async def test_rejects_unknown_slot(self, service, patient):
        service.slot_repository.get_by_id.return_value = None

        with pytest.raises(SlotNotFoundException):
            await service.book_appointment(
                current_user=patient,
                request=BookAppointmentRequest(slot_id=SLOT_ID)
            )

    async def test_rejects_already_booked_slot(
        self, service, patient, booked_slot
    ):
        service.slot_repository.get_by_id.return_value = booked_slot

        with pytest.raises(SlotUnavailableException):
            await service.book_appointment(
                current_user=patient,
                request=BookAppointmentRequest(slot_id=SLOT_ID)
            )

    async def test_rejects_past_slot_date(self, service, patient, future_slot):
        future_slot.slot_date = date.today() - timedelta(days=1)
        service.slot_repository.get_by_id.return_value = future_slot

        with pytest.raises(PastSlotDateException):
            await service.book_appointment(
                current_user=patient,
                request=BookAppointmentRequest(slot_id=SLOT_ID)
            )


class TestDoubleBookingPrevention:
    """SRS test case: Double Booking Prevention."""

    async def test_rejects_when_slot_taken_concurrently(
        self, service, patient, future_slot
    ):
        service.slot_repository.get_by_id.return_value = future_slot
        service.slot_repository.book_if_available.return_value = False

        with pytest.raises(SlotUnavailableException):
            await service.book_appointment(
                current_user=patient,
                request=BookAppointmentRequest(slot_id=SLOT_ID)
            )

        service.appointment_repository.create.assert_not_called()

    async def test_appointment_only_created_after_slot_is_locked(
        self, service, patient, future_slot, appointment
    ):
        service.slot_repository.get_by_id.return_value = future_slot
        service.slot_repository.book_if_available.return_value = True
        service.appointment_repository.create.return_value = appointment

        await service.book_appointment(
            current_user=patient,
            request=BookAppointmentRequest(slot_id=SLOT_ID)
        )

        service.slot_repository.book_if_available.assert_awaited_once()
        service.appointment_repository.create.assert_awaited_once()


class TestListAppointments:

    async def test_patient_sees_own_appointments(
        self, service, patient, appointment
    ):
        service.appointment_repository.get_by_patient.return_value = [appointment]

        response = await service.get_my_appointments(current_user=patient)

        assert len(response.data) == 1
        assert response.data[0].doctor_name == "Anita Sharma"

    async def test_doctor_sees_own_appointments(
        self, service, doctor, appointment
    ):
        service.appointment_repository.get_by_doctor.return_value = [appointment]

        response = await service.get_doctor_appointments(current_user=doctor)

        assert len(response.data) == 1
        assert response.data[0].patient_name == "Ravi Kumar"


class TestCancelAppointment:
    """SRS test case: Appointment Cancellation."""

    async def test_patient_cancels_and_slot_is_freed(
        self, service, patient, appointment, future_slot
    ):
        service.appointment_repository.get_by_id.return_value = appointment
        service.appointment_repository.update.return_value = appointment
        service.slot_repository.get_by_id.return_value = future_slot

        response = await service.cancel_appointment(
            current_user=patient,
            appointment_id=str(appointment.id)
        )

        assert response.success is True
        assert appointment.status == AppointmentStatus.CANCELLED
        service.slot_repository.update.assert_awaited_once()

    async def test_rejects_unknown_appointment(self, service, patient):
        service.appointment_repository.get_by_id.return_value = None

        with pytest.raises(AppointmentNotFoundException):
            await service.cancel_appointment(
                current_user=patient,
                appointment_id="missing"
            )

    async def test_rejects_other_patients_appointment(
        self, service, doctor, appointment
    ):
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(AccessDeniedException):
            await service.cancel_appointment(
                current_user=doctor,
                appointment_id=str(appointment.id)
            )

    async def test_rejects_already_completed_appointment(
        self, service, patient, appointment
    ):
        appointment.status = AppointmentStatus.COMPLETED
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(AppointmentNotCancellableException):
            await service.cancel_appointment(
                current_user=patient,
                appointment_id=str(appointment.id)
            )

    async def test_rejects_inside_two_hour_window(
        self, service, patient, appointment
    ):
        soon = datetime.utcnow() + timedelta(minutes=30)
        appointment.appointment_date = soon.date()
        appointment.start_time = soon.strftime("%H:%M:%S")
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(CancellationWindowExpiredException):
            await service.cancel_appointment(
                current_user=patient,
                appointment_id=str(appointment.id)
            )


class TestRequestCancellation:

    async def test_doctor_requests_cancellation_with_reason(
        self, service, doctor, appointment
    ):
        service.appointment_repository.get_by_id.return_value = appointment
        service.appointment_repository.update.return_value = appointment

        response = await service.request_cancellation(
            current_user=doctor,
            appointment_id=str(appointment.id),
            request=CancelAppointmentRequest(reason="Emergency surgery")
        )

        assert response.success is True
        assert appointment.status == AppointmentStatus.CANCELLATION_REQUESTED
        assert appointment.cancellation_reason == "Emergency surgery"

    async def test_rejects_other_doctors_appointment(
        self, service, patient, appointment
    ):
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(AccessDeniedException):
            await service.request_cancellation(
                current_user=patient,
                appointment_id=str(appointment.id),
                request=CancelAppointmentRequest(reason="Emergency surgery")
            )


class TestApproveRejectCancellation:

    async def test_approve_cancels_and_frees_slot(
        self, service, appointment, future_slot
    ):
        appointment.status = AppointmentStatus.CANCELLATION_REQUESTED
        service.appointment_repository.get_by_id.return_value = appointment
        service.appointment_repository.update.return_value = appointment
        service.slot_repository.get_by_id.return_value = future_slot

        response = await service.approve_cancellation(
            appointment_id=str(appointment.id)
        )

        assert response.success is True
        assert appointment.status == AppointmentStatus.CANCELLED
        service.slot_repository.update.assert_awaited_once()

    async def test_reject_returns_appointment_to_booked(
        self, service, appointment
    ):
        appointment.status = AppointmentStatus.CANCELLATION_REQUESTED
        service.appointment_repository.get_by_id.return_value = appointment
        service.appointment_repository.update.return_value = appointment

        response = await service.reject_cancellation(
            appointment_id=str(appointment.id)
        )

        assert response.success is True
        assert appointment.status == AppointmentStatus.BOOKED

    async def test_rejects_when_no_pending_request(self, service, appointment):
        appointment.status = AppointmentStatus.BOOKED
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(NoPendingCancellationException):
            await service.approve_cancellation(
                appointment_id=str(appointment.id)
            )

    async def test_lists_pending_requests(self, service, appointment):
        appointment.status = AppointmentStatus.CANCELLATION_REQUESTED
        service.appointment_repository.get_by_status.return_value = [appointment]

        response = await service.list_cancellation_requests()

        assert len(response.data) == 1
        assert response.data[0].doctor_name == "Anita Sharma"


class TestUpdateStatus:

    async def test_marks_completed_after_appointment_time(
        self, service, doctor, appointment
    ):
        past = datetime.utcnow() - timedelta(hours=2)
        appointment.appointment_date = past.date()
        appointment.start_time = past.strftime("%H:%M:%S")
        appointment.end_time = (
            past + timedelta(minutes=30)
        ).strftime("%H:%M:%S")
        service.appointment_repository.get_by_id.return_value = appointment
        service.appointment_repository.update.return_value = appointment

        response = await service.update_status(
            current_user=doctor,
            appointment_id=str(appointment.id),
            request=UpdateAppointmentStatusRequest(
                status=AppointmentStatus.COMPLETED
            )
        )

        assert response.success is True
        assert appointment.status == AppointmentStatus.COMPLETED

    async def test_rejects_invalid_target_status(
        self, service, doctor, appointment
    ):
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(InvalidStatusUpdateException):
            await service.update_status(
                current_user=doctor,
                appointment_id=str(appointment.id),
                request=UpdateAppointmentStatusRequest(
                    status=AppointmentStatus.CANCELLED
                )
            )

    async def test_rejects_before_appointment_time(
        self, service, doctor, appointment
    ):
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(StatusUpdateTooEarlyException):
            await service.update_status(
                current_user=doctor,
                appointment_id=str(appointment.id),
                request=UpdateAppointmentStatusRequest(
                    status=AppointmentStatus.NOT_ATTENDED
                )
            )

    async def test_rejects_appointment_not_booked(
        self, service, doctor, appointment
    ):
        appointment.status = AppointmentStatus.CANCELLED
        service.appointment_repository.get_by_id.return_value = appointment

        with pytest.raises(AppointmentNotUpdatableException):
            await service.update_status(
                current_user=doctor,
                appointment_id=str(appointment.id),
                request=UpdateAppointmentStatusRequest(
                    status=AppointmentStatus.COMPLETED
                )
            )
