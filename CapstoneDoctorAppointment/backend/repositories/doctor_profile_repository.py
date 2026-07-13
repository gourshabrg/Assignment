from typing import Optional

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.doctor_profile_model import DoctorProfile


class DoctorProfileRepository:
    """Direct MongoDB access for the DoctorProfile document."""

    async def create(
        self,
        profile: DoctorProfile,
        session: AsyncIOMotorClientSession | None = None
    ) -> DoctorProfile:

        await profile.insert(session=session)

        return profile

    async def get_by_user_id(
        self,
        user_id: str
    ) -> Optional[DoctorProfile]:

        return await DoctorProfile.find_one(
            DoctorProfile.user_id == user_id
        )

    async def update(
        self,
        profile: DoctorProfile,
        session: AsyncIOMotorClientSession | None = None
    ) -> DoctorProfile:

        await profile.save(session=session)

        return profile
