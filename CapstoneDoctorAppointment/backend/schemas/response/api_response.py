from typing import Generic, TypeVar

from pydantic import BaseModel

T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    """The standard envelope every endpoint returns."""

    success: bool

    message: str

    data: T | None = None
