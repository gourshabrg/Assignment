from unittest.mock import AsyncMock
import pytest
from exceptions import (
    UserNotFoundException,
    DoctorProfileNotFoundException
)
from services.admin_service import AdminService
from tests.conftest import DOCTOR_ID, PATIENT_ID


@pytest.fixture
def service() -> AdminService:
    admin_service = AdminService()
    admin_service.user_repository = AsyncMock()
    admin_service.doctor_repository = AsyncMock()
    admin_service.appointment_repository = AsyncMock()

    return admin_service


class TestListDoctors:

    async def test_lists_verified_doctor(self, service, doctor, doctor_profile):
        service.user_repository.get_by_role.return_value = [doctor]
        service.doctor_repository.get_by_user_id.return_value = doctor_profile

        response = await service.list_doctors()

        assert len(response.data) == 1
        assert response.data[0].full_name == "Anita Sharma"
        assert response.data[0].is_verified is True

    async def test_lists_pending_doctor(
        self, service, pending_doctor, doctor_profile
    ):
        service.user_repository.get_by_role.return_value = [pending_doctor]
        service.doctor_repository.get_by_user_id.return_value = doctor_profile

        response = await service.list_doctors()

        assert response.data[0].is_verified is False


class TestVerifyDoctor:

    async def test_activates_the_doctor(
        self, service, pending_doctor, doctor_profile
    ):
        service.user_repository.get_by_id.return_value = pending_doctor
        service.doctor_repository.get_by_user_id.return_value = doctor_profile

        response = await service.verify_doctor(doctor_id=DOCTOR_ID)

        assert response.success is True
        assert pending_doctor.is_active is True
        service.user_repository.update.assert_awaited_once()

    async def test_rejects_unknown_doctor(self, service):
        service.user_repository.get_by_id.return_value = None

        with pytest.raises(UserNotFoundException):
            await service.verify_doctor(doctor_id=DOCTOR_ID)

    async def test_rejects_non_doctor_account(self, service, patient):
        service.user_repository.get_by_id.return_value = patient

        with pytest.raises(UserNotFoundException):
            await service.verify_doctor(doctor_id=PATIENT_ID)

    async def test_rejects_doctor_without_profile(self, service, pending_doctor):
        service.user_repository.get_by_id.return_value = pending_doctor
        service.doctor_repository.get_by_user_id.return_value = None

        with pytest.raises(DoctorProfileNotFoundException):
            await service.verify_doctor(doctor_id=DOCTOR_ID)


class TestRejectDoctor:

    async def test_deactivates_the_doctor(
        self, service, doctor, doctor_profile
    ):
        service.user_repository.get_by_id.return_value = doctor
        service.doctor_repository.get_by_user_id.return_value = doctor_profile

        response = await service.reject_doctor(doctor_id=DOCTOR_ID)

        assert response.success is True
        assert doctor.is_active is False

    async def test_rejects_non_doctor_account(self, service, patient):
        service.user_repository.get_by_id.return_value = patient

        with pytest.raises(UserNotFoundException):
            await service.reject_doctor(doctor_id=PATIENT_ID)


class TestGetDashboard:

    async def test_returns_platform_stats(
        self, service, doctor, patient, doctor_profile, appointment
    ):
        service.user_repository.count_by_role.side_effect = [3, 7]
        service.appointment_repository.count_all.return_value = 10
        service.appointment_repository.count_by_status.side_effect = [4, 2]
        service.user_repository.get_by_role.return_value = [doctor]
        service.doctor_repository.get_by_user_id.return_value = doctor_profile
        service.appointment_repository.get_recent.return_value = [appointment]
        service.user_repository.get_by_ids.return_value = [doctor, patient]

        response = await service.get_dashboard()

        assert response.data.total_doctors == 3
        assert response.data.total_patients == 7
        assert response.data.total_appointments == 10
        assert response.data.completed_appointments == 4
        assert response.data.cancelled_appointments == 2
        assert len(response.data.doctors) == 1
        assert len(response.data.recent_appointments) == 1
        assert response.data.recent_appointments[0].doctor_name == "Anita Sharma"
        assert response.data.recent_appointments[0].patient_name == "Ravi Kumar"
