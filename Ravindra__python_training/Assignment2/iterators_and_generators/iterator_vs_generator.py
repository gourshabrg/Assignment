# Explain the difference between iterator and generator with a small example


numbers = iter([1, 2, 3])

print(next(numbers))


def generate_numbers():

    yield 1
    yield 2
    yield 3


for number in generate_numbers():
    print(number)