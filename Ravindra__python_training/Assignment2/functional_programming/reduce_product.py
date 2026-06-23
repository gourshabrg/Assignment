# Use reduce() to find the product of all elements in a list.

from functools import reduce

def find_product(numbers):

    product = reduce(
        lambda first, second: first * second,
        numbers
    )

    print(f"Product = {product}")


numbers = [1, 2, 3, 4, 5]

find_product(numbers)