# 43. Implement encapsulation using private variables in Bank class.

# Bank class

class Bank:

    def __init__(self, balance):

        self.__balance = balance

    def deposit(self, amount):

        self.__balance += amount

    def show_balance(self):

        print(f"Balance = {self.__balance}")


# Creating object

account = Bank(10000)

account.deposit(5000)

account.show_balance()