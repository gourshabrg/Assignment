import re

PHONE_PATTERN = re.compile(r"^[6-9]\d{9}$")

ALLOWED_EMAIL_DOMAINS = (
    "gmail.com",
    "hotmail.com",
    "outlook.com",
    "yahoo.com"
)


def validate_phone(value: str) -> str:
    """Validates a 10-digit mobile number starting with 6-9."""

    if not PHONE_PATTERN.match(value):
        raise ValueError(
            "Phone number must be 10 digits and start with 6-9."
        )

    return value


def validate_email_domain(value: str) -> str:
    """Validates the email's domain is an allowed provider."""

    domain = value.split("@")[-1].lower()

    if domain not in ALLOWED_EMAIL_DOMAINS:
        raise ValueError(
            "Email must be a gmail, hotmail, outlook, or yahoo address."
        )

    return value
