from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials

from security.jwt_bearer import jwt_bearer

from utils.jwt_handler import JWTManager

from repositories.user_repository import UserRepository

from exceptions import (
    InvalidTokenException,
    UserNotFoundException
)

user_repository = UserRepository()


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(
        jwt_bearer
    )
):
    """Resolves the logged-in User from the request's bearer token."""

    token = credentials.credentials

    try:

        payload = JWTManager.verify_token(
            token
        )

    except Exception:

        raise InvalidTokenException()

    user = await user_repository.get_by_id(
        payload["sub"]
    )

    if not user:

        raise UserNotFoundException()

    return user
