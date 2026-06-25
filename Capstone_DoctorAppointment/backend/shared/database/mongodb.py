from motor.motor_asyncio import AsyncIOMotorClient
from shared.config.settings import settings


client = AsyncIOMotorClient(settings.mongo_uri)

database = client[settings.database_name]