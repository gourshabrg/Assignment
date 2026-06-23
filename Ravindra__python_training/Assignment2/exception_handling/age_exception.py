# - Create a custom exception called AgeException and raise it if age is less than 18.

from constants import MINIMUM_AGE
class AgeException(Exception):
    pass


def check_age(age):

    if age < MINIMUM_AGE:
        raise AgeException(f"Age must be {MINIMUM_AGE} or above.")

    print("Eligible")


try:

    age = int(input("Enter age: "))

    check_age(age)

except AgeException as error:

    print(error)