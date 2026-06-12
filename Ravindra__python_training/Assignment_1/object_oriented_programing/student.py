# 40. Create a Student class with attributes and display details.

# Student class

class Student:

    def __init__(self, name, age, course):

        self.name = name
        self.age = age
        self.course = course

    def display_details(self):

        print(f"Name = {self.name}")
        print(f"Age = {self.age}")
        print(f"Course = {self.course}")


# Creating object

student1 = Student("Ravindra", 23, "MCA")

student1.display_details()