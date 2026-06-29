from fastapi import HTTPException

from shared.constants import (
    HTTP_BAD_REQUEST,
    HTTP_UNAUTHORIZED,
    INVALID_CREDENTIALS,
    INVALID_PASSWORD,
    INVALID_TOKEN
)


class InvalidCredentialsException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_UNAUTHORIZED,
            detail=INVALID_CREDENTIALS
        )


class InvalidPasswordException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_BAD_REQUEST,
            detail=INVALID_PASSWORD
        )


class InvalidTokenException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_UNAUTHORIZED,
            detail=INVALID_TOKEN
        )