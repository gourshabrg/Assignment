import jwt
import pytest
from utils.jwt_handler import JWTManager


class TestCreateAccessToken:
    """test case: JWT Validation."""

    def test_token_carries_expected_claims(self):
        token = JWTManager.create_access_token(
            user_id="abc123",
            email="ravi@gmail.com",
            role="PATIENT"
        )

        payload = JWTManager.verify_token(token=token)

        assert payload["sub"] == "abc123"
        assert payload["email"] == "ravi@gmail.com"
        assert payload["role"] == "PATIENT"
        assert "iat" in payload
        assert "exp" in payload


class TestVerifyToken:

    def test_rejects_malformed_token(self):
        with pytest.raises(jwt.PyJWTError):
            JWTManager.verify_token(token="not-a-real-token")

    def test_rejects_tampered_token(self):
        token = JWTManager.create_access_token(
            user_id="abc123",
            email="ravi@gmail.com",
            role="PATIENT"
        )

        with pytest.raises(jwt.PyJWTError):
            JWTManager.verify_token(token=token + "tampered")

    def test_rejects_expired_token(self, mocker):
        mocker.patch(
            "utils.jwt_handler.settings.access_token_expire_minutes",
            -1
        )

        token = JWTManager.create_access_token(
            user_id="abc123",
            email="ravi@gmail.com",
            role="PATIENT"
        )

        with pytest.raises(jwt.ExpiredSignatureError):
            JWTManager.verify_token(token=token)
