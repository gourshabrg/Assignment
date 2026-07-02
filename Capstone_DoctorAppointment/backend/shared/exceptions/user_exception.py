from shared.constants import (
    HTTP_CONFLICT,
    HTTP_FORBIDDEN,
    HTTP_NOT_FOUND,
    USER_ALREADY_EXISTS,
    USER_INACTIVE,
    USER_NOT_FOUND
)

from shared.exceptions.base_exception import BaseAPIException


class UserAlreadyExistsException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = USER_ALREADY_EXISTS


class UserNotFoundException(BaseAPIException):
    status_code = HTTP_NOT_FOUND
    message = USER_NOT_FOUND


class UserInactiveException(BaseAPIException):
    status_code = HTTP_FORBIDDEN
    message = USER_INACTIVE
