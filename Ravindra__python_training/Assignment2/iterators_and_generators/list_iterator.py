#  Create an iterator for a list and print elements using next().

def iterator_example():

    numbers = [10, 20, 30, 40]

    iterator = iter(numbers)

    print(next(iterator))
    print(next(iterator))
    print(next(iterator))
    print(next(iterator))


iterator_example()