
# 24. Create your own module and import it.

import calculator_module

num1 = int(input("Enter first number: "))
num2 = int(input("Enter second number: "))

print("Addition =", calculator_module.add(num1, num2))
print("Subtraction =", calculator_module.subtract(num1, num2))