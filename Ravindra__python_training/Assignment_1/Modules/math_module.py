# 22. Use math module to find square root, power, and factorial.

# Importing math module
import math

def math_operations(number):

    print("Square Root =", math.sqrt(number))
    print("Power =", math.pow(number, 2))
    print("Factorial =", math.factorial(int(number)))

number = int(input("Enter a number: "))

math_operations(number)