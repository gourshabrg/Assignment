from fastapi import HTTPException

from shared.constants import (
    HTTP_CONFLICT,
    HTTP_FORBIDDEN,
    HTTP_NOT_FOUND,
    USER_ALREADY_EXISTS,
    USER_INACTIVE,
    USER_NOT_FOUND
)


class UserAlreadyExistsException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_CONFLICT,
            detail=USER_ALREADY_EXISTS
        )


class UserNotFoundException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_NOT_FOUND,
            detail=USER_NOT_FOUND
        )


class UserInactiveException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_FORBIDDEN,
            detail=USER_INACTIVE
        )