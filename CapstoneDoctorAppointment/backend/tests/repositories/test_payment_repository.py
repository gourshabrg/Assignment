import pytest
from models.payment_model import Payment
from repositories.payment_repository import PaymentRepository
from tests.conftest import APPOINTMENT_ID, PATIENT_ID


@pytest.fixture
def repository() -> PaymentRepository:
    return PaymentRepository()


class TestCreateAndGet:

    async def test_create_then_get_by_appointment_id(self, repository):
        await repository.create(
            payment=Payment(
                appointment_id=APPOINTMENT_ID,
                patient_id=PATIENT_ID,
                amount=700
            )
        )

        found = await repository.get_by_appointment_id(
            appointment_id=APPOINTMENT_ID
        )

        assert found.amount == 700
        assert found.status == "SUCCESS"

    async def test_get_returns_none_when_missing(self, repository):
        found = await repository.get_by_appointment_id(
            appointment_id="missing"
        )

        assert found is None
