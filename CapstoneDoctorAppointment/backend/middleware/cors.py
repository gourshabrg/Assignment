from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from config.settings import settings


def add_cors_middleware(app: FastAPI) -> None:
    """Allows the React frontend to call this API from its own origin."""

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origin_list,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"]
    )
