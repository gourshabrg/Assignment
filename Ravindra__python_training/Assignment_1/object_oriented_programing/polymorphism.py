# 44. Demonstrate polymorphism using different classes with the same method name.

# First class

class Dog:

    def sound(self):

        print("Dog barks")


# Second class

class Cat:

    def sound(self):

        print("Cat meows")


# Function demonstrating polymorphism

def animal_sound(animal):

    animal.sound()


# Creating objects

dog = Dog()
cat = Cat()

animal_sound(dog)
animal_sound(cat)