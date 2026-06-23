import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

def student_analysis():

    students = {

        "Name": [
            "Rahul",
            "Priya",
            "Siri",
            "Anuj"
        ],

        "Marks": [
            70,
            80,
            90,
            60
        ],

        "Hours Studied": [
            2,
            3,
            5,
            1
        ]
    }

    dataframe = pd.DataFrame(students)

    dataframe["Performance"] = (
        dataframe["Marks"]
        .apply(
            lambda marks:
            "Pass"
            if marks > 65
            else "Fail"
        )
    )

    print(dataframe)

    plt.plot(
        dataframe["Hours Studied"],
        dataframe["Marks"]
    )

    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")

    plt.title("Hours vs Marks")

    plt.show()

    plt.scatter(
        dataframe["Hours Studied"],
        dataframe["Marks"]
    )

    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")

    plt.title("Study vs Marks")

    plt.show()

    sns.barplot(
        data=dataframe,
        x="Performance",
        y="Marks"
    )

    plt.show()


student_analysis()