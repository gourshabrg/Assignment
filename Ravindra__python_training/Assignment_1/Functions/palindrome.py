# 18. Write a function to check palindrome(Number and string).


def check_palindrome(value):

    value = str(value)

    if value == value[::-1]:
        print("Palindrome")
    else:
        print("Not Palindrome")

value = input("Enter a number or string: ")

check_palindrome(value)