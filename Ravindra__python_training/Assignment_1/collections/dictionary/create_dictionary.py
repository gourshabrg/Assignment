# 32. Create a student dictionary and access values

def display_student():

    student = {
        "name": "Ravindra",
        "age": 23,
        "course": "MCA"
    }

    for key, value in student.items():
        print(f"{key} = {value}")


display_student()