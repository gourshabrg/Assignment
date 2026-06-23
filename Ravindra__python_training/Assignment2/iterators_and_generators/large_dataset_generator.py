# Write a program that processes a large dataset using a generator instead of storing all values in a list.

# Generator creates one value at a time.
# List stores all values in memory.
# Generator is memory efficient. 

def large_data():

    for number in range(1000000):
        yield number


for value in large_data():

    if value == 10:
        break

    print(value)