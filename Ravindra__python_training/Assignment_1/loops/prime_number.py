# 16. Check whether a number is prime.


def check_prime(number):

    if number <= 1:
        print("Not Prime")
        return

    for i in range(2, number):

        if number % i == 0:
            print("Not Prime")
            return

    print("Prime Number")

number = int(input("Enter a number: "))

check_prime(number)