#  Create a function with a logical bug and use pdb to identify the issue.

import pdb

def calculate_average(numbers):

    pdb.set_trace()

    total = sum(numbers)

    average = total / len(numbers)

    return average


numbers = [10, 20, 30]

print(calculate_average(numbers))