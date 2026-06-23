# Write a pattern to check if a string contains only alphabets.

import re

def check_alphabets(text):

    pattern = r"^[A-Za-z]+$"

    if re.match(pattern, text):
        print("Only Alphabets")
    else:
        print("Contains Other Characters")


text = input("Enter text: ")

check_alphabets(text)