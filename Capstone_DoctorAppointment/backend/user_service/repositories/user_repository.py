from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from user_service.models.user_model import User


class UserRepository:

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

    async def update(
        self,
        user: User,
        session: AsyncIOMotorClientSession | None = None
    ) -> User:

        await user.save(session=session)

        return user

    async def delete(
        self,
        user: User,
        session: AsyncIOMotorClientSession | None = None
    ):

        await user.delete(session=session)