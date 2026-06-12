# 36. Read a file and count words, lines, and characters.

def file_statistics():

    file = open("student.txt", "r")

    content = file.read()

    file.close()

    word_count = len(content.split())

    line_count = len(content.splitlines())

    character_count = len(content)

    print(f"Words = {word_count}")
    print(f"Lines = {line_count}")
    print(f"Characters = {character_count}")


file_statistics()