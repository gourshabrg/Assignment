
# Use map() to convert a list of numbers into their squares.

def find_squares(numbers):

    squares = list(map(lambda number: number ** 2, numbers))

    print(f"Squares = {squares}")


numbers = [1, 2, 3, 4, 5]

find_squares(numbers)