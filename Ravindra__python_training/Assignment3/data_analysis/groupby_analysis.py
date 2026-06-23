import pandas as pd

# Use GroupBy
# Using employee dataset:
# Tasks:
# Find average salary by department


# Find max salary by department


# Count employees per department


def analyze_data():

    employees = {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }

    dataframe = pd.DataFrame(employees)

    print("\nAverage Salary")
    print(
        dataframe.groupby(
            "Department"
        )["Salary"].mean()
    )

    print("\nMaximum Salary")
    print(
        dataframe.groupby(
            "Department"
        )["Salary"].max()
    )

    print("\nEmployee Count")
    print(
        dataframe.groupby(
            "Department"
        )["Name"].count()
    )


analyze_data()