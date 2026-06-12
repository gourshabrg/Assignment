# 39. Search a word in a file.


def search_word(word):

    file = open("student.txt", "r")

    content = file.read()

    file.close()

    if word in content:
        print(f"'{word}' found in file.")
    else:
        print(f"'{word}' not found in file.")


word = input("Enter word to search: ")

search_word(word)