
# - Write a program that handles FileNotFoundError when trying to open a file.
def open_file():

    try:

        with open("data.txt", "r" ,encoding="utf-8") as file:
            print(file.read())

    except FileNotFoundError as error:
        print("File not found.!", error)


open_file()