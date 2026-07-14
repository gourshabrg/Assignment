from unittest.mock import AsyncMock
import pytest
from enums.specialization_enum import Specialization
from exceptions import (
    DoctorProfileNotFoundException,
    NoFieldsToUpdateException
)
from schemas.request.doctor_profile_update_request import (
    DoctorProfileUpdateRequest
)
from services.doctor_service import DoctorService
from tests.conftest import DOCTOR_ID


@pytest.fixture
def service() -> DoctorService:
    doctor_service = DoctorService()
    doctor_service.doctor_repository = AsyncMock()
    doctor_service.user_repository = AsyncMock()
    doctor_service.slot_repository = AsyncMock()

    return doctor_service


class TestGetMyProfile:

    async def test_returns_profile(self, service, doctor, doctor_profile):
        service.doctor_repository.get_by_user_id.return_value = doctor_profile

        response = await service.get_my_profile(current_user=doctor)

        assert response.success is True
        assert response.data.specialization == Specialization.CARDIOLOGIST

    async def test_rejects_missing_profile(self, service, doctor):
        service.doctor_repository.get_by_user_id.return_value = None

        with pytest.raises(DoctorProfileNotFoundException):
            await service.get_my_profile(current_user=doctor)


class TestUpdateMyProfile:

    async def test_updates_sent_fields_only(
        self, service, doctor, doctor_profile
    ):
        service.doctor_repository.get_by_user_id.return_value = doctor_profile
        service.doctor_repository.update.return_value = doctor_profile

        response = await service.update_my_profile(
            current_user=doctor,
            request=DoctorProfileUpdateRequest(consultation_fee=900)
        )

        assert response.success is True
        assert doctor_profile.consultation_fee == 900
        assert doctor_profile.qualification == "MBBS"

    async def test_rejects_empty_update(self, service, doctor, doctor_profile):
        service.doctor_repository.get_by_user_id.return_value = doctor_profile

        with pytest.raises(NoFieldsToUpdateException):
            await service.update_my_profile(
                current_user=doctor,
                request=DoctorProfileUpdateRequest()
            )

    async def test_rejects_missing_profile(self, service, doctor):
        service.doctor_repository.get_by_user_id.return_value = None

        with pytest.raises(DoctorProfileNotFoundException):
            await service.update_my_profile(
                current_user=doctor,
                request=DoctorProfileUpdateRequest(consultation_fee=900)
            )


class TestSearchDoctors:

    async def test_returns_matching_doctors(
        self, service, doctor, doctor_profile
    ):
        service.doctor_repository.search.return_value = [doctor_profile]
        service.user_repository.get_active_doctors_by_ids.return_value = [doctor]

        response = await service.search_doctors(
            specialization=Specialization.CARDIOLOGIST
        )

        assert len(response.data) == 1
        assert response.data[0].full_name == "Anita Sharma"

    async def test_returns_empty_when_no_profiles_match(self, service):
        service.doctor_repository.search.return_value = []

        response = await service.search_doctors()

        assert response.data == []


class TestGetDoctorById:

    async def test_returns_doctor_with_available_slots(
        self, service, doctor, doctor_profile, future_slot
    ):
        service.doctor_repository.get_by_user_id.return_value = doctor_profile
        service.user_repository.get_active_doctors_by_ids.return_value = [doctor]
        service.slot_repository.get_available_by_doctor.return_value = [
            future_slot
        ]

        response = await service.get_doctor_by_id(doctor_id=DOCTOR_ID)

        assert response.data.full_name == "Anita Sharma"
        assert len(response.data.available_slots) == 1

    async def test_rejects_missing_profile(self, service):
        service.doctor_repository.get_by_user_id.return_value = None

        with pytest.raises(DoctorProfileNotFoundException):
            await service.get_doctor_by_id(doctor_id=DOCTOR_ID)

    async def test_rejects_inactive_doctor(self, service, doctor_profile):
        service.doctor_repository.get_by_user_id.return_value = doctor_profile
        service.user_repository.get_active_doctors_by_ids.return_value = []

        with pytest.raises(DoctorProfileNotFoundException):
            await service.get_doctor_by_id(doctor_id=DOCTOR_ID)
