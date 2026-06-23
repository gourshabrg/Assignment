# Create advanced charts
# Using employee dataset:
# Tasks:
# Create:


# Barplot → Department vs Salary


# Create:


# Boxplot → Salary distribution


# Create:


# Heatmap using correlation between Age & Salary



import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

def create_visualization():

    dataframe = pd.DataFrame({

        "Name": ["Rahul", "Priya", "Amit", "Anuj"],

        "Age": [25, 30, 28, 35],

        "Department": [
            "HR",
            "IT",
            "Finance",
            "IT"
        ],

        "Salary": [
            30000,
            50000,
            45000,
            60000
        ]
    })

    sns.barplot(
        data=dataframe,
        x="Department",
        y="Salary"
    )

    plt.show()

    sns.boxplot(
        y=dataframe["Salary"]
    )

    plt.show()

    correlation = dataframe[
        ["Age", "Salary"]
    ].corr()

    sns.heatmap(
        correlation,
        annot=True
    )

    plt.show()


create_visualization()