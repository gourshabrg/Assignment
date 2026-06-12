# 14. Find factorial of a number.


def calculate_factorial(number):

    factorial = 1

    for i in range(1, number + 1):
        factorial = factorial * i

    print("Factorial =", factorial)

number = int(input("Enter a number: "))

if number < 0:
    print("Factorial does not exist for negative numbers")
else:
    calculate_factorial(number)