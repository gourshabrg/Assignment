from fastapi import HTTPException

from shared.constants import (
    DOCTOR_PROFILE_ALREADY_EXISTS,
    DOCTOR_PROFILE_NOT_FOUND,
    HTTP_CONFLICT,
    HTTP_NOT_FOUND
)


class DoctorProfileAlreadyExistsException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_CONFLICT,
            detail=DOCTOR_PROFILE_ALREADY_EXISTS
        )


class DoctorProfileNotFoundException(HTTPException):

    def __init__(self):
        super().__init__(
            status_code=HTTP_NOT_FOUND,
            detail=DOCTOR_PROFILE_NOT_FOUND
        )