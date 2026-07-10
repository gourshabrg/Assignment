from typing import Optional

from beanie import PydanticObjectId
from beanie.operators import In, RegEx

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.user_model import User


class UserRepository:
    """Direct MongoDB access for the User document."""

    async def get_by_email(
        self,
        email: str
    ) -> Optional[User]:

        return await User.find_one(
            User.email == email
        )

    async def create(
        self,
        user: User,
        session: AsyncIOMotorClientSession | None = None
    ) -> User:

        await user.insert(session=session)

        return user

    async def get_by_id(
        self,
        user_id: str
    ) -> Optional[User]:

        return await User.get(user_id)

    async def get_active_doctors_by_ids(
        self,
        user_ids: list[str],
        name: str | None = None
    ) -> list[User]:
        """Fetches active doctors by id, optionally filtered by name."""

        object_ids = [
            PydanticObjectId(user_id) for user_id in user_ids
        ]

        conditions = [
            In(User.id, object_ids),
            User.is_active == True  # noqa: E712
        ]

        if name:
            conditions.append(
                RegEx(User.full_name, name, "i")
            )

        return await User.find(*conditions).to_list()

    async def update(
        self,
        user: User,
        session: AsyncIOMotorClientSession | None = None
    ) -> User:

        await user.save(session=session)

        return user
