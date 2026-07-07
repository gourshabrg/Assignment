import re
import bcrypt


class PasswordManager:
    """Password validation and bcrypt hashing."""

    PASSWORD_PATTERN = (
        r"^(?=.*[A-Z])"
        r"(?=.*[a-z])"
        r"(?=.*\d)"
        r"(?=.*[@$!%*?&])"
        r"[A-Za-z\d@$!%*?&]{8,12}$"
    )

    @staticmethod
    def validate_password(password: str) -> bool:
        """True if the password meets the strength rules."""

        return bool(
            re.match(
                PasswordManager.PASSWORD_PATTERN,
                password
            )
        )

    @staticmethod
    def hash_password(password: str) -> str:
        """Hashes a plain-text password with bcrypt."""

        password_bytes = password.encode("utf-8")

        salt = bcrypt.gensalt()

        hashed_password = bcrypt.hashpw(
            password_bytes,
            salt
        )

        return hashed_password.decode("utf-8")

    @staticmethod
    def verify_password(
        plain_password: str,
        hashed_password: str
    ) -> bool:
        """True if the plain-text password matches the bcrypt hash."""

        return bcrypt.checkpw(
            plain_password.encode("utf-8"),
            hashed_password.encode("utf-8")
        )
