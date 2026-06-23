# Create a password validation program using regex (minimum length, one digit, one special character).

import re

# Function to validate password

def validate_password(password):

    pattern = (
        r"^(?=.*[0-9])"
        r"(?=.*[@#$%^&+=!])"
        r".{8,}$"
    )

    if re.match(pattern, password):
        print("Valid Password")
    else:
        print("Invalid Password")


password = input("Enter password: ")

validate_password(password)