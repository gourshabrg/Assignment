from shared.constants import (
    SLOT_UNAVAILABLE,
    APPOINTMENT_NOT_FOUND,
    APPOINTMENT_NOT_CANCELLABLE,
    CANCELLATION_WINDOW_EXPIRED,
    INVALID_STATUS_UPDATE,
    STATUS_UPDATE_TOO_EARLY,
    APPOINTMENT_NOT_UPDATABLE,
    HTTP_BAD_REQUEST,
    HTTP_CONFLICT,
    HTTP_NOT_FOUND
)

from shared.exceptions.base_exception import BaseAPIException


class SlotUnavailableException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = SLOT_UNAVAILABLE


class AppointmentNotFoundException(BaseAPIException):
    status_code = HTTP_NOT_FOUND
    message = APPOINTMENT_NOT_FOUND


class AppointmentNotCancellableException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = APPOINTMENT_NOT_CANCELLABLE


class CancellationWindowExpiredException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = CANCELLATION_WINDOW_EXPIRED


class InvalidStatusUpdateException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = INVALID_STATUS_UPDATE


class StatusUpdateTooEarlyException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = STATUS_UPDATE_TOO_EARLY


class AppointmentNotUpdatableException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = APPOINTMENT_NOT_UPDATABLE
