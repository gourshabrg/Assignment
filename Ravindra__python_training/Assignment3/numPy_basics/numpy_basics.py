import numpy as np

# Assignment 1: NumPy Basics

def numpy_operations():

  # Create a NumPy array:
    array = np.array([10, 20, 30, 40, 50])


    print(f"Array = {array}")

    print(f"Mean = {np.mean(array)}")

    print(f"Maximum = {np.max(array)}")

    print(f"Minimum = {np.min(array)}")

    print(f"Sum = {np.sum(array)}")

# Create two arrays:

    array_1 = np.array([1, 2, 3])

    array_2 = np.array([4, 5, 6])

    print(f"Addition = {array_1 + array_2}")

    print(f"Multiplication = {array_1 * array_2}")

# Create a 3×3 matrix using NumPy

    matrix = np.array([
        [1, 2, 3],
        [4, 5, 6],
        [7, 8, 9]
    ])

    print("3x3 Matrix:")
    print(matrix)


numpy_operations()