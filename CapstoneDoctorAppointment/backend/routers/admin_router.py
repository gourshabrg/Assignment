from fastapi import APIRouter, Depends, status
from services.admin_service import AdminService
from security.role_checker import admin_required

router = APIRouter(
    prefix="/admin",
    tags=["Admin"],
    dependencies=[Depends(admin_required)]
)

admin_service = AdminService()

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
