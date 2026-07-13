from typing import Optional

from beanie.operators import RegEx

from motor.motor_asyncio import AsyncIOMotorClientSession

from models.doctor_profile_model import DoctorProfile
from enums.specialization_enum import Specialization


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

    async def search(
        self,
        specialization: Specialization | None = None,
        location: str | None = None,
        min_experience: int | None = None,
        max_fee: float | None = None
    ) -> list[DoctorProfile]:
        """Filters doctor profiles by the given optional criteria."""

        conditions = []

        if specialization:
            conditions.append(
                DoctorProfile.specialization == specialization
            )

        if location:
            conditions.append(
                RegEx(DoctorProfile.clinic_address, location, "i")
            )

        if min_experience is not None:
            conditions.append(
                DoctorProfile.experience >= min_experience
            )

        if max_fee is not None:
            conditions.append(
                DoctorProfile.consultation_fee <= max_fee
            )

        return await DoctorProfile.find(*conditions).to_list()
