from shared.constants import (
    HTTP_BAD_REQUEST,
    HTTP_UNAUTHORIZED,
    INVALID_CREDENTIALS,
    INVALID_PASSWORD,
    INVALID_TOKEN,
    SAME_PASSWORD,
    INCORRECT_OLD_PASSWORD
)

from shared.exceptions.base_exception import BaseAPIException


class InvalidCredentialsException(BaseAPIException):
    status_code = HTTP_UNAUTHORIZED
    message = INVALID_CREDENTIALS


class InvalidPasswordException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = INVALID_PASSWORD


class InvalidTokenException(BaseAPIException):
    status_code = HTTP_UNAUTHORIZED
    message = INVALID_TOKEN


class SamePasswordException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = SAME_PASSWORD


class IncorrectOldPasswordException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = INCORRECT_OLD_PASSWORD
