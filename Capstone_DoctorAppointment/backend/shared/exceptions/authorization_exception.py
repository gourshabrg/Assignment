from shared.constants import (
    HTTP_FORBIDDEN,
    FORBIDDEN
)

from shared.exceptions.base_exception import BaseAPIException


class AccessDeniedException(BaseAPIException):
    status_code = HTTP_FORBIDDEN
    message = FORBIDDEN
