# 26. Count even and odd numbers in a list.

def count_even_odd(numbers):

    even_count = 0
    odd_count = 0

    for number in numbers:

        if number % 2 == 0:
            even_count += 1
        else:
            odd_count += 1

    print(f"Even Numbers = {even_count}")
    print(f"Odd Numbers = {odd_count}")


numbers = []

print("Enter 10 numbers:")

for i in range(10):
    number = int(input(f"Enter number {i + 1}: "))
    numbers.append(number)

count_even_odd(numbers)