# Write a regular expression to validate a 10-digit mobile number.
import re

def validate_mobile(number):

    pattern = r"^[0-9]{10}$"

    if re.match(pattern, number):
        print("Valid Mobile Number")
    else:
        print("Invalid Mobile Number")


number = input("Enter mobile number: ")

validate_mobile(number)