# Write a program to extract all numbers from a given string using regular expressions.

import re

def extract_numbers(text):

    numbers = re.findall(r"\d+", text)

    print(f"Numbers = {numbers}")


text = "Ram scored 95 marks and Shyam scored 88"

extract_numbers(text)