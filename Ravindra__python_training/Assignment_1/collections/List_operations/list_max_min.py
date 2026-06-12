# 25. Create a list of 10 numbers and find sum, max, sort it, and remove duplicates.


def list_operations(numbers):

    print(f"Original List = {numbers}")

    print(f"Sum = {sum(numbers)}")

    print(f"Maximum Number = {max(numbers)}")

    sorted_list = numbers.copy()
    sorted_list.sort()

    print(f"Sorted List = {sorted_list}")

    unique_numbers = list(set(numbers))

    print(f"List Without Duplicates = {unique_numbers}")


numbers = []

print("Enter 10 numbers:")

for i in range(10):
    number = int(input(f"Enter number {i + 1}: "))
    numbers.append(number)

list_operations(numbers)