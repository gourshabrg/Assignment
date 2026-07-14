from datetime import date
import pytest
from enums.gender_enum import Gender
from enums.role_enum import UserRole
from models.user_model import User
from repositories.user_repository import UserRepository


@pytest.fixture
def repository() -> UserRepository:
    return UserRepository()


def _user(email: str, role: UserRole, is_active: bool = True) -> User:
    return User(
        full_name="Test User",
        email=email,
        password="hashed",
        phone="9876543210",
        gender=Gender.MALE,
        dob=date(1995, 1, 1),
        role=role,
        is_active=is_active
    )


class TestCreateAndGet:

    async def test_create_then_get_by_email(self, repository):
        created = await repository.create(
            user=_user("a@gmail.com", UserRole.PATIENT)
        )

        found = await repository.get_by_email(email="a@gmail.com")

        assert found.id == created.id

    async def test_get_by_email_returns_none_when_missing(self, repository):
        assert await repository.get_by_email(email="none@gmail.com") is None

    async def test_get_by_id(self, repository):
        created = await repository.create(
            user=_user("b@gmail.com", UserRole.PATIENT)
        )

        found = await repository.get_by_id(user_id=str(created.id))

        assert found.email == "b@gmail.com"


class TestUpdate:

    async def test_update_persists_changes(self, repository):
        created = await repository.create(
            user=_user("c@gmail.com", UserRole.DOCTOR, is_active=False)
        )

        created.is_active = True
        await repository.update(user=created)

        found = await repository.get_by_id(user_id=str(created.id))

        assert found.is_active is True


class TestQueriesByRole:

    async def test_get_by_role(self, repository):
        await repository.create(user=_user("d@gmail.com", UserRole.DOCTOR))
        await repository.create(user=_user("e@gmail.com", UserRole.PATIENT))

        doctors = await repository.get_by_role(role=UserRole.DOCTOR)

        assert len(doctors) == 1
        assert doctors[0].email == "d@gmail.com"

    async def test_count_by_role(self, repository):
        await repository.create(user=_user("f@gmail.com", UserRole.PATIENT))
        await repository.create(user=_user("g@gmail.com", UserRole.PATIENT))

        assert await repository.count_by_role(role=UserRole.PATIENT) == 2


class TestBulkLookups:

    async def test_get_by_ids(self, repository):
        first = await repository.create(
            user=_user("h@gmail.com", UserRole.PATIENT)
        )
        second = await repository.create(
            user=_user("i@gmail.com", UserRole.DOCTOR)
        )

        users = await repository.get_by_ids(
            user_ids=[str(first.id), str(second.id)]
        )

        assert len(users) == 2

    async def test_get_active_doctors_by_ids_skips_inactive(self, repository):
        active = await repository.create(
            user=_user("j@gmail.com", UserRole.DOCTOR)
        )
        inactive = await repository.create(
            user=_user("k@gmail.com", UserRole.DOCTOR, is_active=False)
        )

        doctors = await repository.get_active_doctors_by_ids(
            user_ids=[str(active.id), str(inactive.id)]
        )

        assert len(doctors) == 1
        assert doctors[0].email == "j@gmail.com"
