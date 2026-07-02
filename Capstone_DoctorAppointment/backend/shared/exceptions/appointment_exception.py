from shared.constants import (
    SLOT_UNAVAILABLE,
    HTTP_CONFLICT
)

from shared.exceptions.base_exception import BaseAPIException


class SlotUnavailableException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = SLOT_UNAVAILABLE
