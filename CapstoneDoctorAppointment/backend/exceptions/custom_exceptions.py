from fastapi import HTTPException, status


class BaseAPIException(HTTPException):
    """Base class for all custom API errors.

    Subclasses set status_code and message as class attributes instead
    of repeating an __init__. All project exception classes live in
    this single file.
    """

    status_code: int = status.HTTP_500_INTERNAL_SERVER_ERROR

    message: str = "Something went wrong."

    def __init__(self):
        super().__init__(
            status_code=self.status_code,
            detail=self.message
        )
