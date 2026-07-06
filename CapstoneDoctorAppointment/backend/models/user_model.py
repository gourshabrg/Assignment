from beanie import Document
from pydantic import EmailStr, Field
from datetime import datetime, date

from enums.role_enum import UserRole


class User(Document):
    """MongoDB 'users' collection -- patients, doctors and admins all
    share this document, distinguished by the role field.
    """

    full_name: str = Field(min_length=2, max_length=100)

    email: EmailStr

    password: str

    phone: str = Field(min_length=10, max_length=10)

    gender: str | None = None

    dob: date | None = None

    role: UserRole

    is_active: bool = True

    created_at: datetime = Field(default_factory=datetime.utcnow)

    updated_at: datetime = Field(default_factory=datetime.utcnow)

    class Settings:
        name = "users"
