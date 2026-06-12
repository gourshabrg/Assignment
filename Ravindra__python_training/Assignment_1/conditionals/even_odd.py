# 7. Write a program to check whether a number is even or odd.


def check_even_odd(number):

    if number % 2 == 0:
        print("Even Number")
    else:
        print("Odd Number")


number = int(input("Enter a integer number: "))

check_even_odd(number)