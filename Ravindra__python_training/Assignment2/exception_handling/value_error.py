# - Write a program that takes a number as input and handles ValueError if the input is not a valid integer.


def get_number():

    try:
        number = int(input("Enter a number: "))
        print(f"You entered: {number}")

    except ValueError:
        print("Invalid input. Please enter an integer.")

get_number()