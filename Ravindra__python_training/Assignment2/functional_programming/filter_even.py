# Use filter() to extract even numbers from a list.

def get_even_numbers(numbers):

    even_numbers = list(
        filter(lambda number: number % 2 == 0, numbers)
    )

    print(f"Even Numbers = {even_numbers}")


numbers = [1, 2, 3, 4, 5, 6, 7, 8]

get_even_numbers(numbers)