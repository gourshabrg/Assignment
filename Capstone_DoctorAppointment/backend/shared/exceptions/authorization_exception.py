from fastapi import HTTPException

from shared.constants import (
    HTTP_FORBIDDEN,
    FORBIDDEN
)


class AccessDeniedException(
    HTTPException
):

    def __init__(self):

        super().__init__(

            status_code=HTTP_FORBIDDEN,

            detail=FORBIDDEN
        )