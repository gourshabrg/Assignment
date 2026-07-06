from fastapi import HTTPException


class BaseAPIException(HTTPException):
    """Base class for all custom API errors.

    Subclasses just set status_code and message as class attributes
    instead of repeating an __init__.
    """

    status_code: int = 500

    message: str = "Something went wrong."

    def __init__(self):
        super().__init__(
            status_code=self.status_code,
            detail=self.message
        )
