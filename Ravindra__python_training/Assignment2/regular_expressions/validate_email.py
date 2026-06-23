#  Write a regular expression to validate an email address.

import re

def validate_email(email):

    pattern = r"^[a-zA-Z0-9._]+@[a-zA-Z]+\.[a-zA-Z]{2,}$"

    if re.match(pattern, email):
        print("Valid Email")
    else:
        print("Invalid Email")


email = input("Enter email: ")

validate_email(email)