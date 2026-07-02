from fastapi import HTTPException


class BaseAPIException(HTTPException):

    status_code: int = 500

    message: str = "Something went wrong."

    def __init__(self):
        super().__init__(
            status_code=self.status_code,
            detail=self.message
        )
