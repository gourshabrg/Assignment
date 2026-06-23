
# Write a recursive function to calculate Fibonacci.

def fibonacci(number):

    if number <= 1:
        return number

    return fibonacci(number - 1) + fibonacci(number - 2)


number = int(input("Enter position: "))

print(f"Fibonacci = {fibonacci(number)}")