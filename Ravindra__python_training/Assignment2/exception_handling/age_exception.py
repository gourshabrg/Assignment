# - Create a custom exception called AgeException and raise it if age is less than 18.

class AgeException(Exception):
    pass


def check_age(age):

    if age < 18:
        raise AgeException("Age must be 18 or above.")

    print("Eligible")


try:

    age = int(input("Enter age: "))

    check_age(age)

except AgeException as error:

    print(error)