from datetime import datetime, timedelta, timezone

import jwt

from config.settings import settings


class JWTManager:
    """Creates and verifies the JWT access tokens used for auth."""

    @staticmethod
    def create_access_token(
        user_id: str,
        email: str,
        role: str
    ) -> str:
        """Builds a signed token containing the user id, email and role."""

        now = datetime.now(timezone.utc)

        payload = {
            "sub": user_id,
            "email": email,
            "role": role,
            "iat": now,
            "exp": now + timedelta(
                minutes=settings.access_token_expire_minutes
            )
        }

        token = jwt.encode(
            payload,
            settings.jwt_secret_key,
            algorithm=settings.jwt_algorithm
        )

        return token

    @staticmethod
    def verify_token(token: str):
        """Decodes a token, raising if it's invalid or expired."""

        return jwt.decode(
            token,
            settings.jwt_secret_key,
            algorithms=[settings.jwt_algorithm]
        )
