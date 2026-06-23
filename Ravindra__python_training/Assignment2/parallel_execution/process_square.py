
import multiprocessing

#  Write a multiprocessing program to calculate the square of numbers using Process class.

def calculate_square(number):

    print(
        f"Square of {number} = {number ** 2}"
    )


numbers = [1, 2, 3, 4, 5]

processes = []

for number in numbers:

    process = multiprocessing.Process(
        target=calculate_square,
        args=(number,)
    )

    processes.append(process)

    process.start()