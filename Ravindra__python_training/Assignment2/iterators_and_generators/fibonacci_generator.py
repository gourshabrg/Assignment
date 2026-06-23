
# Write a generator to produce Fibonacci numbers.

def fibonacci_generator(limit):

    first = 0
    second = 1

    for _ in range(limit):

        yield first

        first, second = second, first + second


for number in fibonacci_generator(10):
    print(number)