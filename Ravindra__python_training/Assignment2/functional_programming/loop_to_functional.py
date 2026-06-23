# Convert a simple  loop-based program into a functional style using map or filter.

numbers = [1, 2, 3, 4, 5]

squares = list(
    map(lambda number: number ** 2, numbers)
)

print(f"Squares = {squares}")