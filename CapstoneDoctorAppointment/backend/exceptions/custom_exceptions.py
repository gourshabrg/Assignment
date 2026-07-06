from fastapi import HTTPException, status

from constants import (
    INVALID_PASSWORD,
    USER_ALREADY_EXISTS
)


class BaseAPIException(HTTPException):
    """Base class for all custom API errors."""

    status_code: int = status.HTTP_500_INTERNAL_SERVER_ERROR

    message: str = "Something went wrong."

    def __init__(self):
        super().__init__(
            status_code=self.status_code,
            detail=self.message
        )


class InvalidPasswordException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = INVALID_PASSWORD


class UserAlreadyExistsException(BaseAPIException):
    status_code = status.HTTP_409_CONFLICT
    message = USER_ALREADY_EXISTS
