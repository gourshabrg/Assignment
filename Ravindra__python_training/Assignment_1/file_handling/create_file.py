# 35. Create a file and write your name into it


def write_name_to_file():

    file = open("student.txt", "w")

    file.write("Ravindra Gour")

    file.close()

    print("Name written successfully.")



write_name_to_file()