# - Use re.findall() to extract all words starting with a capital letter.

import re

def capital_words(text):

    words = re.findall(r"\b[A-Z][a-zA-Z]*\b", text)

    print(f"Words = {words}")


text = "Ravindra studies at NIT Jamshedpur"

capital_words(text)