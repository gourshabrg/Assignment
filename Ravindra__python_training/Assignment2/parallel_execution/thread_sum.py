

import threading

# Create a thread that calculates the sum of numbers from 1 to 100.

def calculate_sum():

    total = sum(range(1, 101))

    print(f"Sum = {total}")


thread = threading.Thread(
    target=calculate_sum
)

thread.start()