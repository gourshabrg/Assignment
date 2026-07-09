import re

PHONE_PATTERN = re.compile(r"^[6-9]\d{9}$")

ALLOWED_EMAIL_DOMAINS = (
    "gmail.com",
    "hotmail.com",
    "outlook.com",
    "yahoo.com"
)


def validate_phone(value: str) -> str:
    """Raises ValueError unless value is a 10-digit mobile number
    starting with 6-9. Used as a Pydantic field validator.
    """

    if not PHONE_PATTERN.match(value):
        raise ValueError(
            "Phone number must be 10 digits and start with 6-9."
        )

    return value


def validate_email_domain(value: str) -> str:
    """Raises ValueError unless the email's domain is one of the
    allowed providers. Used as a Pydantic field validator.
    """

    domain = value.split("@")[-1].lower()

    if domain not in ALLOWED_EMAIL_DOMAINS:
        raise ValueError(
            "Email must be a gmail, hotmail, outlook, or yahoo address."
        )

    return value
