

import pandas as pd
import numpy as np



def clean_data():
# Create this dataset:
    employees = {
        "Name": ["Rahul", "Priya", "Anuj"],
        "Age": [25, np.nan, 29],
        "Salary": [30000, 40000, np.nan]
    }

    dataframe = pd.DataFrame(employees)

    print("Original Data")
    print(dataframe)

# Detect missing values


# Replace missing Age with mean


# Replace missing Salary with 0

    print("\nMissing Values")
    print(dataframe.isnull())

    mean_age = dataframe["Age"].mean()

    dataframe["Age"] = dataframe["Age"].fillna(
        mean_age
    )

    dataframe["Salary"] = dataframe["Salary"].fillna(
        0
    )

    print("\nCleaned Data")
    print(dataframe)


clean_data()