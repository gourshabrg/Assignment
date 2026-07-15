import pytest
from enums.specialization_enum import Specialization
from models.doctor_profile_model import DoctorProfile
from repositories.doctor_profile_repository import DoctorProfileRepository
from tests.conftest import DOCTOR_ID


@pytest.fixture
def repository() -> DoctorProfileRepository:
    return DoctorProfileRepository()


def _profile(
    user_id=DOCTOR_ID,
    specialization=Specialization.CARDIOLOGIST,
    experience=8,
    fee=700,
    address="Pune"
):
    return DoctorProfile(
        user_id=user_id,
        qualification="MBBS",
        specialization=specialization,
        experience=experience,
        license_number="LIC-1",
        consultation_fee=fee,
        clinic_address=address
    )


class TestCreateAndGet:

    async def test_create_then_get_by_user_id(self, repository):
        await repository.create(profile=_profile())

        found = await repository.get_by_user_id(user_id=DOCTOR_ID)

        assert found.qualification == "MBBS"

    async def test_get_returns_none_when_missing(self, repository):
        assert await repository.get_by_user_id(user_id="missing") is None


class TestUpdate:

    async def test_update_persists_changes(self, repository):
        created = await repository.create(profile=_profile())

        created.consultation_fee = 900
        await repository.update(profile=created)

        found = await repository.get_by_user_id(user_id=DOCTOR_ID)

        assert found.consultation_fee == 900


class TestSearch:

    async def test_search_without_filters_returns_all(self, repository):
        await repository.create(profile=_profile())
        await repository.create(profile=_profile(user_id="other"))

        assert len(await repository.search()) == 2

    async def test_search_by_specialization(self, repository):
        await repository.create(profile=_profile())
        await repository.create(
            profile=_profile(
                user_id="other",
                specialization=Specialization.DENTIST
            )
        )

        results = await repository.search(
            specialization=Specialization.DENTIST
        )

        assert len(results) == 1

    async def test_search_by_min_experience(self, repository):
        await repository.create(profile=_profile(experience=2))
        await repository.create(profile=_profile(user_id="o", experience=10))

        results = await repository.search(min_experience=5)

        assert len(results) == 1

    async def test_search_by_max_fee(self, repository):
        await repository.create(profile=_profile(fee=500))
        await repository.create(profile=_profile(user_id="o", fee=2000))

        results = await repository.search(max_fee=1000)

        assert len(results) == 1
