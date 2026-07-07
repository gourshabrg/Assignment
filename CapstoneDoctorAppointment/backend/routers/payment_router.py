from fastapi import APIRouter, Depends, status
from models.user_model import User
from services.payment_service import PaymentService
from security.role_checker import patient_required

router = APIRouter(
    prefix="/appointments",
    tags=["Payments"]
)

payment_service = PaymentService()


@router.post(
    "/{appointment_id}/pay",
    status_code=status.HTTP_201_CREATED
)
async def pay_for_appointment(
    appointment_id: str,
    current_user: User = Depends(
        patient_required
    )
):
    """Patient pays (simulated) for their own booked appointment."""

    return await payment_service.pay_for_appointment(
        current_user=current_user,
        appointment_id=appointment_id
    )
