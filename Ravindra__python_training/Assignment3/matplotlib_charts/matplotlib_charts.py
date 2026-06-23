
# Create basic charts
# Using this data:
# Departments = HR, IT, Finance
#  Employees = 5, 12, 7
# Tasks:
# Create Bar Chart


# Create Line Chart


# Create Histogram using salaries:

#  [30000, 40000, 50000, 60000, 45000]


# Create Scatter Plot:
#  Age vs Salary

import matplotlib.pyplot as plt


def create_charts():

    departments = ["HR", "IT", "Finance"]

    employees = [5, 12, 7]

    plt.bar(departments, employees)
    plt.title("Bar Chart")
    plt.show()

    plt.plot(departments, employees)
    plt.title("Line Chart")
    plt.show()

    salaries = [30000, 40000, 50000, 60000, 45000]

    plt.hist(salaries)
    plt.title("Salary Histogram")
    plt.show()

    age = [25, 30, 28, 35]

    salary = [30000, 50000, 45000, 60000]

    plt.scatter(age, salary)

    plt.xlabel("Age")
    plt.ylabel("Salary")

    plt.title("Age vs Salary")

    plt.show()


create_charts()