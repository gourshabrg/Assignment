# Use re.search() to check whether a word exists in a sentence.

import re

def search_word(sentence, word):

    result = re.search(word, sentence)

    if result:
        print("Word Found")
    else:
        print("Word Not Found")


sentence = "Python is easy to learn"

search_word(sentence, "easy")