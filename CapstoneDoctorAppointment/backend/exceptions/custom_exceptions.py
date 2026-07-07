from fastapi import HTTPException, status

from constants import (
    INVALID_PASSWORD,
    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,
    INVALID_CREDENTIALS,
    INVALID_TOKEN,
    SAME_PASSWORD,
    INCORRECT_OLD_PASSWORD,
    ACCOUNT_PENDING_APPROVAL,
    ACCESS_DENIED,
    DOCTOR_PROFILE_NOT_FOUND,
    NO_FIELDS_TO_UPDATE,
    SLOT_NOT_FOUND,
    SLOT_ALREADY_BOOKED,
    INVALID_SLOT_TIME,
    PAST_SLOT_DATE,
    SLOT_OVERLAP,
    SLOT_UNAVAILABLE,
    APPOINTMENT_NOT_FOUND,
    APPOINTMENT_NOT_CANCELLABLE,
    CANCELLATION_WINDOW_EXPIRED,
    INVALID_STATUS_UPDATE,
    STATUS_UPDATE_TOO_EARLY,
    APPOINTMENT_NOT_UPDATABLE,
    PAYMENT_ALREADY_EXISTS,
    PAYMENT_NOT_ALLOWED
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


class UserNotFoundException(BaseAPIException):
    status_code = status.HTTP_404_NOT_FOUND
    message = USER_NOT_FOUND


class InvalidCredentialsException(BaseAPIException):
    status_code = status.HTTP_401_UNAUTHORIZED
    message = INVALID_CREDENTIALS


class InvalidTokenException(BaseAPIException):
    status_code = status.HTTP_401_UNAUTHORIZED
    message = INVALID_TOKEN


class SamePasswordException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = SAME_PASSWORD


class IncorrectOldPasswordException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = INCORRECT_OLD_PASSWORD


class AccountPendingApprovalException(BaseAPIException):
    status_code = status.HTTP_403_FORBIDDEN
    message = ACCOUNT_PENDING_APPROVAL


class AccessDeniedException(BaseAPIException):
    status_code = status.HTTP_403_FORBIDDEN
    message = ACCESS_DENIED


class DoctorProfileNotFoundException(BaseAPIException):
    status_code = status.HTTP_404_NOT_FOUND
    message = DOCTOR_PROFILE_NOT_FOUND


class NoFieldsToUpdateException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = NO_FIELDS_TO_UPDATE


class SlotNotFoundException(BaseAPIException):
    status_code = status.HTTP_404_NOT_FOUND
    message = SLOT_NOT_FOUND


class SlotAlreadyBookedException(BaseAPIException):
    status_code = status.HTTP_409_CONFLICT
    message = SLOT_ALREADY_BOOKED


class InvalidSlotTimeException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = INVALID_SLOT_TIME


class PastSlotDateException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = PAST_SLOT_DATE


class SlotOverlapException(BaseAPIException):
    status_code = status.HTTP_409_CONFLICT
    message = SLOT_OVERLAP


class SlotUnavailableException(BaseAPIException):
    status_code = status.HTTP_409_CONFLICT
    message = SLOT_UNAVAILABLE


class AppointmentNotFoundException(BaseAPIException):
    status_code = status.HTTP_404_NOT_FOUND
    message = APPOINTMENT_NOT_FOUND


class AppointmentNotCancellableException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = APPOINTMENT_NOT_CANCELLABLE


class CancellationWindowExpiredException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = CANCELLATION_WINDOW_EXPIRED


class InvalidStatusUpdateException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = INVALID_STATUS_UPDATE


class StatusUpdateTooEarlyException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = STATUS_UPDATE_TOO_EARLY


class AppointmentNotUpdatableException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = APPOINTMENT_NOT_UPDATABLE


class PaymentAlreadyExistsException(BaseAPIException):
    status_code = status.HTTP_409_CONFLICT
    message = PAYMENT_ALREADY_EXISTS


class PaymentNotAllowedException(BaseAPIException):
    status_code = status.HTTP_400_BAD_REQUEST
    message = PAYMENT_NOT_ALLOWED
