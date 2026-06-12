# 3. Take user input (name and age) and print a formatted message.

# Function to display user information

def display_user_info(name, age):
    print(f"My name is {name} and I am {age} years old.")

# Taking input
name = input("Enter your name: ")
age = int(input("Enter your age: "))

# Calling function
display_user_info(name, age)