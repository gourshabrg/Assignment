from fastapi import APIRouter, Depends, status

from user_service.models.user_model import User

from user_service.schemas.request.book_appointment_request import (
    BookAppointmentRequest
)

from user_service.services.appointment_service import AppointmentService

from shared.security.role_checker import patient_required


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

    return await appointment_service.book_appointment(
        current_user=current_user,
        request=request
    )
