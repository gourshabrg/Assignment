# 27. Reverse a list without using reverse().


def reverse_list(numbers):

    reversed_list = numbers[::-1]

    print(f"Original List = {numbers}")
    print(f"Reversed List = {reversed_list}")

numbers = []

print("Enter 10 numbers:")

for i in range(10):
    number = int(input(f"Enter number {i + 1}: "))
    numbers.append(number)

reverse_list(numbers)