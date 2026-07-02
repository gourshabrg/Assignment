from shared.constants import (
    DOCTOR_PROFILE_ALREADY_EXISTS,
    DOCTOR_PROFILE_NOT_FOUND,
    NO_FIELDS_TO_UPDATE,
    SLOT_NOT_FOUND,
    SLOT_ALREADY_BOOKED,
    INVALID_SLOT_TIME,
    PAST_SLOT_DATE,
    SLOT_OVERLAP,
    HTTP_BAD_REQUEST,
    HTTP_CONFLICT,
    HTTP_NOT_FOUND
)

from shared.exceptions.base_exception import BaseAPIException


class DoctorProfileAlreadyExistsException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = DOCTOR_PROFILE_ALREADY_EXISTS


class DoctorProfileNotFoundException(BaseAPIException):
    status_code = HTTP_NOT_FOUND
    message = DOCTOR_PROFILE_NOT_FOUND


class NoFieldsToUpdateException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = NO_FIELDS_TO_UPDATE


class SlotNotFoundException(BaseAPIException):
    status_code = HTTP_NOT_FOUND
    message = SLOT_NOT_FOUND


class SlotAlreadyBookedException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = SLOT_ALREADY_BOOKED


class InvalidSlotTimeException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = INVALID_SLOT_TIME


class PastSlotDateException(BaseAPIException):
    status_code = HTTP_BAD_REQUEST
    message = PAST_SLOT_DATE


class SlotOverlapException(BaseAPIException):
    status_code = HTTP_CONFLICT
    message = SLOT_OVERLAP
