from fastapi import APIRouter, Depends, status

from user_service.services.admin_service import AdminService

from shared.security.role_checker import admin_required


router = APIRouter(
    prefix="/admin",
    tags=["Admin"],
    dependencies=[Depends(admin_required)]
)

admin_service = AdminService()


@router.get(
    "/doctors",
    status_code=status.HTTP_200_OK
)
async def list_doctors():

    return await admin_service.list_doctors()


@router.patch(
    "/doctors/{doctor_id}/activate",
    status_code=status.HTTP_200_OK
)
async def activate_doctor(doctor_id: str):

    return await admin_service.activate_doctor(
        doctor_id=doctor_id
    )


@router.patch(
    "/doctors/{doctor_id}/deactivate",
    status_code=status.HTTP_200_OK
)
async def deactivate_doctor(doctor_id: str):

    return await admin_service.deactivate_doctor(
        doctor_id=doctor_id
    )


@router.get(
    "/dashboard",
    status_code=status.HTTP_200_OK
)
async def get_dashboard():

    return await admin_service.get_dashboard()
