#  Write a program using try-except-else-finally to read a number from a file  and print its square.



from constants import DEFAULT_FILE_NAME

def read_and_square():

    try:

       with open(DEFAULT_FILE_NAME, "r", encoding="utf-8") as file:
        return int(file.read().strip())

    except FileNotFoundError:
        print("File not found.")

    except ValueError:
        print("File does not contain a valid number.")

    else:
        print(f"Square = {number ** 2}")

    finally:
        print("Program completed.")




read_and_square()