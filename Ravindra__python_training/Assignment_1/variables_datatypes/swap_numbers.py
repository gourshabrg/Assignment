
# 5. Write a program to swap two numbers.

def swap_numbers(first_number , second_number):
    print("Before swapping: ", first_number, second_number)
    
    # Swapping logic
    # temp = first_number
    # first_number = second_number
    # second_number = temp
    
    # Swapping logic in python
    first_number, second_number = second_number, first_number
    
    print("After swapping: ", first_number, second_number)

num1 = int(input("Enter first number: "))
num2 = int(input("Enter second number: "))


swap_numbers(num1, num2)