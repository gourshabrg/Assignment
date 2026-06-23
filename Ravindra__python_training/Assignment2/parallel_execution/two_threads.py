#  Write a program to create two threads that print numbers from 1 to 5 simultaneously.

import threading


def print_numbers():

    for number in range(1, 6):

        print(f"{number}")


thread1 = threading.Thread(target=print_numbers)

thread2 = threading.Thread(target=print_numbers)

thread1.start()

thread2.start()