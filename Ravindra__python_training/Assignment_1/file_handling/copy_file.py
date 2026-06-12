# 38. Copy content from one file to another.


def copy_file():

    source_file = open("student.txt", "r")

    content = source_file.read()

    source_file.close()

    destination_file = open("copy.txt", "w")

    destination_file.write(content)

    destination_file.close()

    print("File copied successfully.")



copy_file()