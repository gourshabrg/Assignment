from unittest.mock import AsyncMock
import pytest
from fastapi.security import HTTPAuthorizationCredentials
from exceptions import InvalidTokenException, UserNotFoundException
from security import current_user as current_user_module
from security.current_user import get_current_user
from utils.jwt_handler import JWTManager


def _credentials(token: str) -> HTTPAuthorizationCredentials:
    return HTTPAuthorizationCredentials(scheme="Bearer", credentials=token)


class TestGetCurrentUser:
    """test case: JWT Validation."""

    async def test_returns_user_for_valid_token(self, patient, mocker):
        mocker.patch.object(
            current_user_module,
            "user_repository",
            AsyncMock(get_by_id=AsyncMock(return_value=patient))
        )

        token = JWTManager.create_access_token(
            user_id=str(patient.id),
            email=patient.email,
            role=patient.role.value
        )

        user = await get_current_user(credentials=_credentials(token))

        assert user.email == patient.email

    async def test_rejects_invalid_token(self):
        with pytest.raises(InvalidTokenException):
            await get_current_user(credentials=_credentials("bad-token"))

    async def test_rejects_unknown_user(self, patient, mocker):
        mocker.patch.object(
            current_user_module,
            "user_repository",
            AsyncMock(get_by_id=AsyncMock(return_value=None))
        )

        token = JWTManager.create_access_token(
            user_id=str(patient.id),
            email=patient.email,
            role=patient.role.value
        )

        with pytest.raises(UserNotFoundException):
            await get_current_user(credentials=_credentials(token))
