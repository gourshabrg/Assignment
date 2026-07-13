from datetime import date
from unittest.mock import AsyncMock
import pytest
from enums.gender_enum import Gender
from enums.role_enum import UserRole
from enums.specialization_enum import Specialization
from exceptions import (
    UserAlreadyExistsException,
    InvalidPasswordException,
    UserNotFoundException,
    InvalidCredentialsException,
    IncorrectOldPasswordException,
    SamePasswordException,
    AccountPendingApprovalException
)
from schemas.request.patient_register_request import PatientRegisterRequest
from schemas.request.doctor_register_request import DoctorRegisterRequest
from schemas.request.login_request import LoginRequest
from schemas.request.change_password_request import ChangePasswordRequest
from schemas.request.reset_password_request import ResetPasswordRequest
from services.auth_service import AuthService
from utils.password import PasswordManager


@pytest.fixture
def service() -> AuthService:
    auth_service = AuthService()
    auth_service.user_repository = AsyncMock()
    auth_service.doctor_repository = AsyncMock()

    return auth_service


@pytest.fixture
def patient_request() -> PatientRegisterRequest:
    return PatientRegisterRequest(
        full_name="Ravi Kumar",
        email="ravi@gmail.com",
        password="Passw0rd!",
        phone="9876543210",
        gender=Gender.MALE,
        dob=date(1995, 1, 1)
    )


@pytest.fixture
def doctor_request() -> DoctorRegisterRequest:
    return DoctorRegisterRequest(
        full_name="Anita Sharma",
        email="anita@gmail.com",
        password="Passw0rd!",
        phone="9812345678",
        qualification="MBBS",
        specialization=Specialization.CARDIOLOGIST,
        experience=8,
        license_number="LIC-1",
        consultation_fee=700,
        clinic_address="Pune"
    )


class TestRegisterPatient:

    async def test_creates_active_patient(self, service, patient_request, patient):
        service.user_repository.get_by_email.return_value = None
        service.user_repository.create.return_value = patient

        response = await service.register_patient(request=patient_request)

        assert response.success is True
        assert response.data.role == UserRole.PATIENT

        created = service.user_repository.create.call_args.kwargs["user"]
        assert created.is_active is True
        assert created.password != "Passw0rd!"

    async def test_rejects_duplicate_email(self, service, patient_request, patient):
        service.user_repository.get_by_email.return_value = patient

        with pytest.raises(UserAlreadyExistsException):
            await service.register_patient(request=patient_request)

        service.user_repository.create.assert_not_called()

    async def test_rejects_weak_password(self, service, patient_request, mocker):
        service.user_repository.get_by_email.return_value = None
        mocker.patch.object(
            PasswordManager, "validate_password", return_value=False
        )

        with pytest.raises(InvalidPasswordException):
            await service.register_patient(request=patient_request)


class TestRegisterDoctor:

    async def test_creates_doctor_pending_approval(
        self, service, doctor_request, doctor
    ):
        service.user_repository.get_by_email.return_value = None
        service.user_repository.create.return_value = doctor

        response = await service.register_doctor(request=doctor_request)

        assert response.success is True

        created = service.user_repository.create.call_args.kwargs["user"]
        assert created.is_active is False
        service.doctor_repository.create.assert_awaited_once()

    async def test_rejects_duplicate_email(self, service, doctor_request, doctor):
        service.user_repository.get_by_email.return_value = doctor

        with pytest.raises(UserAlreadyExistsException):
            await service.register_doctor(request=doctor_request)


class TestLogin:
    """SRS test case: Login Authentication."""

    async def test_returns_token_for_valid_credentials(
        self, service, patient, mocker
    ):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")
        service.user_repository.get_by_email.return_value = patient

        response = await service.login(
            request=LoginRequest(email="ravi@gmail.com", password="Passw0rd!")
        )

        assert response.success is True
        assert response.data.access_token

    async def test_rejects_unknown_email(self, service):
        service.user_repository.get_by_email.return_value = None

        with pytest.raises(UserNotFoundException):
            await service.login(
                request=LoginRequest(email="no@gmail.com", password="Passw0rd!")
            )

    async def test_rejects_wrong_password(self, service, patient):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")
        service.user_repository.get_by_email.return_value = patient

        with pytest.raises(InvalidCredentialsException):
            await service.login(
                request=LoginRequest(email="ravi@gmail.com", password="Wrong0rd!")
            )

    async def test_blocks_doctor_pending_approval(self, service, pending_doctor):
        pending_doctor.password = PasswordManager.hash_password(
            password="Passw0rd!"
        )
        service.user_repository.get_by_email.return_value = pending_doctor

        with pytest.raises(AccountPendingApprovalException):
            await service.login(
                request=LoginRequest(email="new@gmail.com", password="Passw0rd!")
            )


class TestGetProfileAndLogout:

    async def test_get_profile_returns_current_user(self, service, patient):
        response = await service.get_profile(user=patient)

        assert response.data.email == patient.email

    async def test_logout_succeeds(self, service, patient):
        response = await service.logout(user=patient)

        assert response.success is True


class TestChangePassword:

    async def test_changes_password(self, service, patient):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")

        response = await service.change_password(
            user=patient,
            request=ChangePasswordRequest(
                old_password="Passw0rd!",
                new_password="NewPass1!"
            )
        )

        assert response.success is True
        service.user_repository.update.assert_awaited_once()

    async def test_rejects_wrong_old_password(self, service, patient):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")

        with pytest.raises(IncorrectOldPasswordException):
            await service.change_password(
                user=patient,
                request=ChangePasswordRequest(
                    old_password="Wrong0rd!",
                    new_password="NewPass1!"
                )
            )

    async def test_rejects_same_password(self, service, patient):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")

        with pytest.raises(SamePasswordException):
            await service.change_password(
                user=patient,
                request=ChangePasswordRequest(
                    old_password="Passw0rd!",
                    new_password="Passw0rd!"
                )
            )


class TestResetPassword:

    async def test_resets_password(self, service, patient):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")
        service.user_repository.get_by_email.return_value = patient

        response = await service.reset_password(
            request=ResetPasswordRequest(
                email="ravi@gmail.com",
                new_password="NewPass1!"
            )
        )

        assert response.success is True
        service.user_repository.update.assert_awaited_once()

    async def test_rejects_unknown_email(self, service):
        service.user_repository.get_by_email.return_value = None

        with pytest.raises(UserNotFoundException):
            await service.reset_password(
                request=ResetPasswordRequest(
                    email="no@gmail.com",
                    new_password="NewPass1!"
                )
            )

    async def test_rejects_same_password(self, service, patient):
        patient.password = PasswordManager.hash_password(password="Passw0rd!")
        service.user_repository.get_by_email.return_value = patient

        with pytest.raises(SamePasswordException):
            await service.reset_password(
                request=ResetPasswordRequest(
                    email="ravi@gmail.com",
                    new_password="Passw0rd!"
                )
            )
