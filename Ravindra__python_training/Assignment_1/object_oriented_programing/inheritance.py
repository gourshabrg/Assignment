# 42. Implement inheritance using Person and Employee class.

# Parent class

class Person:

    def __init__(self, name):

        self.name = name


# Child class

class Employee(Person):

    def __init__(self, name, salary):

        super().__init__(name)

        self.salary = salary

    def display_details(self):

        print(f"Name = {self.name}")
        print(f"Salary = {self.salary}")


# Creating object

employee1 = Employee("Ravindra", 50000)

employee1.display_details()