
import multiprocessing
import os

# Write a program to create two processes that print their Process IDs.

def show_process():

    print(
        f"Process ID = {os.getpid()}"
    )


process1 = multiprocessing.Process(
    target=show_process
)

process2 = multiprocessing.Process(
    target=show_process
)

process1.start()

process2.start()