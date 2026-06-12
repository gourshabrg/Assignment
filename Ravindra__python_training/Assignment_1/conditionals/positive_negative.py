# 8. Check whether a number is positive, negative, or zero.


def check_number(number):

    if number > 0:
        print("Positive Number")
    elif number < 0:
        print("Negative Number")
    else:
        print("Zero")


number = float(input("Enter a number: "))

check_number(number)