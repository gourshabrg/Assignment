from datetime import date, timedelta
import pytest
from enums.appointment_status_enum import AppointmentStatus
from models.appointment_model import Appointment
from repositories.appointment_repository import AppointmentRepository
from tests.conftest import PATIENT_ID, DOCTOR_ID, SLOT_ID


@pytest.fixture
def repository() -> AppointmentRepository:
    return AppointmentRepository()


def _appointment(status=AppointmentStatus.BOOKED, patient_id=PATIENT_ID):
    return Appointment(
        patient_id=patient_id,
        doctor_id=DOCTOR_ID,
        slot_id=SLOT_ID,
        appointment_date=date.today() + timedelta(days=10),
        start_time="10:00:00",
        end_time="10:30:00",
        status=status
    )


class TestCreateAndGet:

    async def test_create_then_get_by_id(self, repository):
        created = await repository.create(appointment=_appointment())

        found = await repository.get_by_id(appointment_id=str(created.id))

        assert found.doctor_id == DOCTOR_ID

    async def test_get_by_patient(self, repository):
        await repository.create(appointment=_appointment())
        await repository.create(appointment=_appointment(patient_id="other"))

        appointments = await repository.get_by_patient(patient_id=PATIENT_ID)

        assert len(appointments) == 1

    async def test_get_by_doctor(self, repository):
        await repository.create(appointment=_appointment())

        appointments = await repository.get_by_doctor(doctor_id=DOCTOR_ID)

        assert len(appointments) == 1


class TestUpdate:

    async def test_update_persists_status(self, repository):
        created = await repository.create(appointment=_appointment())

        created.status = AppointmentStatus.CANCELLED
        await repository.update(appointment=created)

        found = await repository.get_by_id(appointment_id=str(created.id))

        assert found.status == AppointmentStatus.CANCELLED


class TestCounts:

    async def test_count_all(self, repository):
        await repository.create(appointment=_appointment())
        await repository.create(appointment=_appointment())

        assert await repository.count_all() == 2

    async def test_count_by_status(self, repository):
        await repository.create(appointment=_appointment())
        await repository.create(
            appointment=_appointment(status=AppointmentStatus.COMPLETED)
        )

        count = await repository.count_by_status(
            status=AppointmentStatus.COMPLETED
        )

        assert count == 1


class TestStatusAndRecent:

    async def test_get_by_status(self, repository):
        await repository.create(
            appointment=_appointment(
                status=AppointmentStatus.CANCELLATION_REQUESTED
            )
        )
        await repository.create(appointment=_appointment())

        pending = await repository.get_by_status(
            status=AppointmentStatus.CANCELLATION_REQUESTED
        )

        assert len(pending) == 1

    async def test_get_recent_respects_limit(self, repository):
        for _ in range(3):
            await repository.create(appointment=_appointment())

        recent = await repository.get_recent(limit=2)

        assert len(recent) == 2
