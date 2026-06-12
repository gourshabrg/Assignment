# 37. Append data to existing file.

def append_data():

    file = open("student.txt", "a")

    file.write("\nMCA Student")

    file.close()

    print("Data appended successfully.")


append_data()