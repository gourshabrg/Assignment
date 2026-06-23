import pandas as pd

# Function to create dataframe

def employee_dataframe():

# Create a DataFrame for employees:
    employees = {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Age": [25, 30, 28, 35],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }

    dataframe = pd.DataFrame(employees)


    print("Employee DataFrame")
    print(dataframe)


# Show first 2 rows


# Show summary statistics


# Display only IT employees


# Add new column:

#  Bonus = Salary * 0.10


    print("\nFirst Two Rows")
    print(dataframe.head(2))

    print("\nSummary Statistics")
    print(dataframe.describe())

    print("\nIT Employees")
    print(
        dataframe[
            dataframe["Department"] == "IT"
        ]
    )

    dataframe["Bonus"] = dataframe["Salary"] * 0.10

    print("\nDataFrame With Bonus")
    print(dataframe)


employee_dataframe()