from datetime import datetime, timedelta, timezone

import jwt

from shared.config.settings import settings


class JWTManager:

    @staticmethod
    def create_access_token(
        user_id: str,
        email: str,
        role: str
    ) -> str:

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

        return jwt.decode(
            token,
            settings.jwt_secret_key,
            algorithms=[settings.jwt_algorithm]
        )