# 33. Count frequency of characters in a string using dictionary.
def count_frequency(text):

    frequency = {}

    for character in text:

        if character in frequency:
            frequency[character] += 1
        else:
            frequency[character] = 1

    print(f"Character Frequency = {frequency}")


text = input("Enter a string: ")

count_frequency(text)