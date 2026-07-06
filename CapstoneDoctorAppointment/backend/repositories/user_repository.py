from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.user_model import User


class UserRepository:
    """Direct MongoDB access for the User document. No business rules
    here -- that belongs in the service layer.
    """

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
