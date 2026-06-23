
# - Write a program that catches all exceptions and prints the error message.

def handle_all_exceptions():

    try:

        number = int(input("Enter a number: "))

        result = 100 / number

        print(f"Result = {result}")

    except Exception as error:
        print(f"Error: {error}")


handle_all_exceptions()