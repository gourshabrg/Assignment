# Write a generator function that yields square numbers up to N.


def square_generator(limit):

    for number in range(1, limit + 1):
        yield number ** 2


for square in square_generator(5):
    print(square)