from fastapi import APIRouter, Depends, status

from models.user_model import User
from schemas.request.book_appointment_request import (
    BookAppointmentRequest
)
from schemas.request.update_appointment_status_request import (
    UpdateAppointmentStatusRequest
)
from schemas.request.cancel_appointment_request import (
    CancelAppointmentRequest
)
from services.appointment_service import AppointmentService
from security.role_checker import doctor_required, patient_required


router = APIRouter(
    prefix="/appointments",
    tags=["Appointments"]
)

appointment_service = AppointmentService()


@router.post(
    "/book",
    status_code=status.HTTP_201_CREATED
)
async def book_appointment(
    request: BookAppointmentRequest,
    current_user: User = Depends(
        patient_required
    )
):
    """Patient books an available slot."""

    return await appointment_service.book_appointment(
        current_user=current_user,
        request=request
    )


@router.get(
    "/patient",
    status_code=status.HTTP_200_OK
)
async def get_my_appointments(
    current_user: User = Depends(
        patient_required
    )
):
    """Patient views their own appointments."""

    return await appointment_service.get_my_appointments(
        current_user=current_user
    )


@router.get(
    "/doctor",
    status_code=status.HTTP_200_OK
)
async def get_doctor_appointments(
    current_user: User = Depends(
        doctor_required
    )
):
    """Doctor views their own appointments."""

    return await appointment_service.get_doctor_appointments(
        current_user=current_user
    )


@router.post(
    "/{appointment_id}/cancel",
    status_code=status.HTTP_200_OK
)
async def cancel_appointment(
    appointment_id: str,
    current_user: User = Depends(
        patient_required
    )
):
    """Patient cancels their own appointment."""

    return await appointment_service.cancel_appointment(
        current_user=current_user,
        appointment_id=appointment_id
    )


@router.post(
    "/{appointment_id}/request-cancellation",
    status_code=status.HTTP_200_OK
)
async def request_cancellation(
    appointment_id: str,
    request: CancelAppointmentRequest,
    current_user: User = Depends(
        doctor_required
    )
):
    """Doctor requests cancellation with a reason, pending admin approval."""

    return await appointment_service.request_cancellation(
        current_user=current_user,
        appointment_id=appointment_id,
        request=request
    )


@router.patch(
    "/{appointment_id}/status",
    status_code=status.HTTP_200_OK
)
async def update_appointment_status(
    appointment_id: str,
    request: UpdateAppointmentStatusRequest,
    current_user: User = Depends(
        doctor_required
    )
):
    """Doctor marks their own appointment COMPLETED or NOT_ATTENDED."""

    return await appointment_service.update_status(
        current_user=current_user,
        appointment_id=appointment_id,
        request=request
    )
