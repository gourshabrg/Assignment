# - Handle multiple exceptions  in a single program.

def multiple_exceptions():

    try:

        number = int(input("Enter a number: "))

        result = 100 / number

        print(f"Result = {result}")

    except ValueError:
        print("Invalid number.")

    except ZeroDivisionError:
        print("Division by zero not allowed.")



multiple_exceptions()