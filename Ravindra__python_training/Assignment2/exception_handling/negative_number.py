# - Create a function that raises a ValueError if a number is negative.

def check_number(number):

    if number < 0:
        raise ValueError("Negative number is not allowed.")

    print(f"Number = {number}")


try:

    number = int(input("Enter a number: "))

    check_number(number)

except ValueError as error:

    print(error)