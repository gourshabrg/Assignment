from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie

from shared.config.settings import settings
from shared.logger.logger import get_logger

from user_service.models.user_model import User
from user_service.models.doctor_profile_model import DoctorProfile
from user_service.models.availability_slot_model import AvailabilitySlot
from user_service.models.appointment_model import Appointment

logger = get_logger(__name__)


class MongoDatabase:

    client = None
    database = None

    @classmethod
    async def connect(cls):

        cls.client = AsyncIOMotorClient(
            settings.mongo_uri
        )

        cls.database = cls.client.get_database(
            settings.database_name
        )

        await init_beanie(
            database=cls.database,
            document_models=[
                User,
                DoctorProfile,
                AvailabilitySlot,
                Appointment
            ]
        )

        logger.info("MongoDB connected successfully")

    @classmethod
    async def close(cls):

        if cls.client:
            cls.client.close()

        logger.info("MongoDB connection closed")
