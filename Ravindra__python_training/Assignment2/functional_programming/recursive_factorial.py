
# Write a recursive function to calculate factorial.

def factorial(number):

    if number == 0 or number == 1:
        return 1

    return number * factorial(number - 1)


number = int(input("Enter a number: "))

print(f"Factorial = {factorial(number)}")