from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie

from config.settings import settings
from logger.logger import get_logger

from models.user_model import User
from models.doctor_profile_model import DoctorProfile

logger = get_logger(__name__)


class MongoDatabase:
    """Owns the single Mongo client/connection for the app lifetime."""

    client = None
    database = None

    @classmethod
    async def connect(cls):
        """Opens the Mongo connection and registers all Beanie documents."""

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
                DoctorProfile
            ]
        )

        logger.info("MongoDB connected successfully")

    @classmethod
    async def close(cls):
        """Closes the Mongo connection on app shutdown."""

        if cls.client:
            cls.client.close()

        logger.info("MongoDB connection closed")
