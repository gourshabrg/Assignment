from concurrent.futures import ProcessPoolExecutor

# Convert a normal function into parallel execution using ProcessPoolExecutor.

def square(number):

    return number ** 2


numbers = [1, 2, 3, 4, 5]

with ProcessPoolExecutor() as executor:

    results = executor.map(
        square,
        numbers
    )

    for result in results:

        print(result)