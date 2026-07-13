import pytest
from fastapi.testclient import TestClient

from main import app
from security.current_user import get_current_user
from security.role_checker import (
    patient_required,
    doctor_required,
    admin_required
)


@pytest.fixture
def client(patient, doctor, admin):
    """Test client with the auth guards satisfied."""

    app.dependency_overrides[get_current_user] = lambda: patient
    app.dependency_overrides[patient_required] = lambda: patient
    app.dependency_overrides[doctor_required] = lambda: doctor
    app.dependency_overrides[admin_required] = lambda: admin

    yield TestClient(app, raise_server_exceptions=False)

    app.dependency_overrides.clear()
