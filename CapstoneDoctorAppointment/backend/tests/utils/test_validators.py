import pytest
from utils.validators import validate_phone, validate_email_domain


class TestValidatePhone:

    @pytest.mark.parametrize("phone", [
        "9876543210",
        "6000000000",
        "7123456789",
        "8123456789"
    ])
    def test_accepts_ten_digits_starting_six_to_nine(self, phone):
        assert validate_phone(phone) == phone

    @pytest.mark.parametrize("phone", [
        "1234567890",
        "5876543210",
        "98765",
        "98765432101",
        "abcdefghij"
    ])
    def test_rejects_invalid_phone(self, phone):
        with pytest.raises(ValueError):
            validate_phone(phone)


class TestValidateEmailDomain:

    @pytest.mark.parametrize("email", [
        "a@gmail.com",
        "b@hotmail.com",
        "c@outlook.com",
        "d@yahoo.com"
    ])
    def test_accepts_allowed_domain(self, email):
        assert validate_email_domain(email) == email

    def test_accepts_uppercase_domain(self):
        assert validate_email_domain("a@GMAIL.com") == "a@GMAIL.com"

    @pytest.mark.parametrize("email", [
        "a@example.com",
        "b@company.org"
    ])
    def test_rejects_other_domain(self, email):
        with pytest.raises(ValueError):
            validate_email_domain(email)
