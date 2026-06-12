# 6. Take two numbers and print sum, difference, multiplication, and division.


def calculate_operations(num1, num2):

    print("Sum =", num1 + num2)
    print("Difference =", num1 - num2)
    print("Multiplication =", num1 * num2)

    if num2 != 0: # Check to avoid division by zero
        print("Division =", num1 / num2)
    else:
        print("Division by zero is not possible")


num1 = float(input("Enter first number: "))
num2 = float(input("Enter second number: "))

calculate_operations(num1, num2)