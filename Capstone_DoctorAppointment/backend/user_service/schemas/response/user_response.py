from datetime import datetime

from pydantic import BaseModel

from shared.enums.role_enum import UserRole


class UserResponse(BaseModel):

    id: str

    full_name: str

    email: str

    phone: str

    role: UserRole

    is_active: bool

    created_at: datetime