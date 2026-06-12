# 9. Find the largest of three numbers.


def find_largest(first_number, second_number, third_number):

    largest = first_number

    if second_number > largest:
        largest = second_number

    if third_number > largest:
        largest = third_number

    print("Largest Number =", largest)


first_number = float(input("Enter first number: "))
second_number = float(input("Enter second number: "))
third_number = float(input("Enter third number: "))

find_largest(first_number, second_number, third_number)