#- Write pytest test cases for a function that checks whether a number is prime.

def is_prime(number):

    if number <= 1:
        return False

    for value in range(2, number):

        if number % value == 0:
            return False

    return True