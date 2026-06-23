# Use pdb breakpoints inside a loop and inspect variable values.

import pdb

def print_numbers():

    for number in range(1, 6):

        pdb.set_trace()

        print(f"Number = {number}")


print_numbers()