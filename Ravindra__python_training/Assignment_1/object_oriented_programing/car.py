# 41. Create a Car class with a constructor.

# Car class

class Car:

    def __init__(self, brand, model):

        self.brand = brand
        self.model = model

    def display_car(self):

        print(f"Brand = {self.brand}")
        print(f"Model = {self.model}")


# Creating object

car1 = Car("Toyota", "Fortuner")

car1.display_car()