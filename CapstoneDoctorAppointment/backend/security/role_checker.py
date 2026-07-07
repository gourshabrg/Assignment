from fastapi import Depends

from enums.role_enum import UserRole
from exceptions import AccessDeniedException

from security.current_user import get_current_user


class RoleChecker:
    """A FastAPI dependency that only lets through users whose role is
    in the allowed list. Use the pre-built instances below rather than
    creating new ones per route.
    """

    def __init__(self, allowed_roles: list[UserRole]):
        self.allowed_roles = allowed_roles

    async def __call__(
        self,
        current_user=Depends(get_current_user)
    ):

        if current_user.role not in self.allowed_roles:
            raise AccessDeniedException()

        return current_user


admin_required = RoleChecker([UserRole.ADMIN])

doctor_required = RoleChecker([UserRole.DOCTOR])

patient_required = RoleChecker([UserRole.PATIENT])
