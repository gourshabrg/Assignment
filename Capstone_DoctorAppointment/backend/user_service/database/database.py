from motor.motor_asyncio import AsyncIOMotorClient
from beanie import init_beanie

from shared.config.settings import settings
from user_service.models.user_model import User
from user_service.models.doctor_profile_model import DoctorProfile


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
                DoctorProfile
            ]
        )

        print("MongoDB Connected Successfully")

    @classmethod
    async def close(cls):

        if cls.client:
            cls.client.close()

        print("MongoDB Connection Closed")

   