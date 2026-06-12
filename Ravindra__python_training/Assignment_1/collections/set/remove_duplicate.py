# 31. Remove duplicates from list using set


def remove_duplicates(numbers):

    unique_numbers = list(set(numbers))

    print(f"Original List = {numbers}")
    print(f"List Without Duplicates = {unique_numbers}")


numbers = []

print("Enter 10 numbers:")

for i in range(10):
    number = int(input(f"Enter number {i + 1}: "))
    numbers.append(number)

remove_duplicates(numbers)