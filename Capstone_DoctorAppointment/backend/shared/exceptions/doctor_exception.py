from shared.constants import (
    DOCTOR_PROFILE_ALREADY_EXISTS,
    DOCTOR_PROFILE_NOT_FOUND,
    NO_FIELDS_TO_UPDATE,
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
