# 19. Write a function that returns maximum number from a list.



def find_maximum(numbers):

    maximum = numbers[0]
# Alternative way to find the maximum number using built-in max() function
    for number in numbers:
        if number > maximum:
            maximum = number

    print("Maximum Number =", maximum)

count = int(input("How many numbers do you want to enter? "))

numbers = []

for i in range(count):
    number = int(input("Enter a number: "))
    numbers.append(number)

find_maximum(numbers)