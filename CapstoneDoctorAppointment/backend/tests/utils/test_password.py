import pytest
from utils.password import PasswordManager


class TestValidatePassword:

    @pytest.mark.parametrize("password", [
        "Passw0rd!",
        "Abcdef1@",
        "Xyz12345$abc"
    ])
    def test_accepts_valid_password(self, password):
        assert PasswordManager.validate_password(password=password) is True

    @pytest.mark.parametrize("password", [
        "short1!",
        "alllowercase1!",
        "ALLUPPERCASE1!",
        "NoDigits!!",
        "NoSpecial123",
        "WayTooLongPassword1!"
    ])
    def test_rejects_invalid_password(self, password):
        assert PasswordManager.validate_password(password=password) is False


class TestHashPassword:

    def test_hash_differs_from_plain_text(self):
        hashed = PasswordManager.hash_password(password="Passw0rd!")

        assert hashed != "Passw0rd!"

    def test_verify_accepts_matching_password(self):
        hashed = PasswordManager.hash_password(password="Passw0rd!")

        assert PasswordManager.verify_password(
            plain_password="Passw0rd!",
            hashed_password=hashed
        ) is True

    def test_verify_rejects_wrong_password(self):
        hashed = PasswordManager.hash_password(password="Passw0rd!")

        assert PasswordManager.verify_password(
            plain_password="Wrong0rd!",
            hashed_password=hashed
        ) is False
