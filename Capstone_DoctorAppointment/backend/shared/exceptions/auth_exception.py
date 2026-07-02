from fastapi import HTTPException

from shared.constants import (
    HTTP_BAD_REQUEST,
    HTTP_UNAUTHORIZED,
    INVALID_CREDENTIALS,
    INVALID_PASSWORD,
    INVALID_TOKEN,
    SAME_PASSWORD,
    INCORRECT_OLD_PASSWORD
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


class SamePasswordException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_BAD_REQUEST,
            detail=SAME_PASSWORD
        )


class IncorrectOldPasswordException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_BAD_REQUEST,
            detail=INCORRECT_OLD_PASSWORD
        )