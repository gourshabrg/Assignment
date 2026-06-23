from concurrent.futures import ThreadPoolExecutor

# Convert a normal function into parallel execution using ThreadPoolExecutor.

def square(number):

    return number ** 2


numbers = [1, 2, 3, 4, 5]

with ThreadPoolExecutor() as executor:

    results = executor.map(
        square,
        numbers
    )

    for result in results:

        print(result)