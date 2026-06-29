from fastapi import Depends

from fastapi.security import HTTPAuthorizationCredentials

from shared.security.jwt_bearer import jwt_bearer

from user_service.utils.jwt_handler import JWTManager

from user_service.repositories.user_repository import (
    UserRepository
)

from shared.exceptions import (
    InvalidTokenException,
    UserNotFoundException
)

user_repository = UserRepository()

async def get_current_user(

    credentials: HTTPAuthorizationCredentials = Depends(
        jwt_bearer
    )

):

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