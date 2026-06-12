# 15. Reverse a number using loop.

def reverse_number(number):

    reverse = 0

    while number > 0:
        digit = number % 10
        reverse = reverse * 10 + digit
        number = number // 10

    print(f"Reversed Number = {reverse}")

number = int(input("Enter a number: "))

reverse_number(number)