from models.user_model import User
from repositories.user_repository import UserRepository
from utils.password import PasswordManager
from enums.role_enum import UserRole
from config.settings import settings
from logger.logger import get_logger

logger = get_logger(__name__)

class AdminSeeder:
    """Seeds the admin account from .env values on startup."""

    @staticmethod
    async def seed_admin():

        user_repository = UserRepository()

        existing_admin = await user_repository.get_by_email(
            email=settings.admin_email
        )

        if existing_admin:
            logger.info("Admin already exists")
            return

        hashed_password = PasswordManager.hash_password(
            settings.admin_password
        )

        admin = User(
            full_name=settings.admin_name,
            email=settings.admin_email,
            password=hashed_password,
            phone=settings.admin_phone,
            role=UserRole.ADMIN,
            is_active=True
        )

        await user_repository.create(user=admin)

        logger.info("Default admin created successfully")
