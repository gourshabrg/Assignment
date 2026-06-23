"""
Use package modules.
"""

from my_package.calculator import add_numbers
from my_package.greetings import say_hello


def main():
 
    print(say_hello("Ravindra"))
    print(add_numbers(10, 20))

    main()