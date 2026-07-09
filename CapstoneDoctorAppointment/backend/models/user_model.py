from beanie import Document
from pydantic import EmailStr, Field
from datetime import datetime, date

from enums.role_enum import UserRole
from enums.gender_enum import Gender


class User(Document):
    """MongoDB 'users' collection, shared by all roles."""

    full_name: str = Field(min_length=2, max_length=100)

    email: EmailStr

    password: str

    phone: str = Field(min_length=10, max_length=10)

    gender: Gender | None = None

    dob: date | None = None

    role: UserRole

    is_active: bool = True

    created_at: datetime = Field(default_factory=datetime.utcnow)

    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "users"
