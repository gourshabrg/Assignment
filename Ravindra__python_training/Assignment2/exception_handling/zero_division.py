# Function to divide numbers

def divide_numbers():

    try:
        num1 = float(input("Enter first number: "))
        num2 = float(input("Enter second number: "))

        result = num1 / num2

    except ValueError:
        print("Invalid input. Please enter a valid number.")

    except ZeroDivisionError:
        print("Cannot divide by zero.")

    else:
        print(f"Result = {result}")

divide_numbers()