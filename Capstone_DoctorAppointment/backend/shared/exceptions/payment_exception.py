from shared.constants import (
    PAYMENT_ALREADY_EXISTS,
    PAYMENT_NOT_ALLOWED,
    HTTP_BAD_REQUEST,
    HTTP_CONFLICT
)

from shared.exceptions.base_exception import BaseAPIException


class PaymentAlreadyExistsException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = PAYMENT_ALREADY_EXISTS


class PaymentNotAllowedException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = PAYMENT_NOT_ALLOWED
