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
