from fastapi import APIRouter, Depends, status
from services.admin_service import AdminService
from services.appointment_service import AppointmentService
from security.role_checker import admin_required

router = APIRouter(
    prefix="/admin",
    tags=["Admin"],
    dependencies=[Depends(admin_required)]
)

admin_service = AdminService()
appointment_service = AppointmentService()

@router.get("/doctors", status_code=status.HTTP_200_OK)
async def list_doctors():
    """Lists every doctor account, active or pending."""

    return await admin_service.list_doctors()

@router.patch(
    "/doctors/{doctor_id}/verify",
    status_code=status.HTTP_200_OK
)
async def verify_doctor(doctor_id: str):
    """Approves a pending doctor's profile."""

    return await admin_service.verify_doctor(doctor_id=doctor_id)

@router.patch(
    "/doctors/{doctor_id}/reject",
    status_code=status.HTTP_200_OK
)
async def reject_doctor(doctor_id: str):
    """Rejects/deactivates a doctor's profile."""

    return await admin_service.reject_doctor(doctor_id=doctor_id)

@router.get("/dashboard", status_code=status.HTTP_200_OK)
async def get_dashboard():
    """Platform stats, doctor list, and recent appointments."""

    return await admin_service.get_dashboard()

@router.get("/cancellation-requests", status_code=status.HTTP_200_OK)
async def list_cancellation_requests():
    """Lists appointments pending cancellation approval."""

    return await appointment_service.list_cancellation_requests()

@router.patch(
    "/cancellation-requests/{appointment_id}/approve",
    status_code=status.HTTP_200_OK
)
async def approve_cancellation(appointment_id: str):
    """Approves a doctor's cancellation request."""

    return await appointment_service.approve_cancellation(
        appointment_id=appointment_id
    )

@router.patch(
    "/cancellation-requests/{appointment_id}/reject",
    status_code=status.HTTP_200_OK
)
async def reject_cancellation(appointment_id: str):
    """Rejects a doctor's cancellation request."""

    return await appointment_service.reject_cancellation(
        appointment_id=appointment_id
    )
