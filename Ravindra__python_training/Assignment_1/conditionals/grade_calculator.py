# 10. Calculate grade based on marks (A/B/C/Fail).


def calculate_grade(marks):

    if marks >= 90:
        print("Grade A")

    elif marks >= 75:
        print("Grade B")

    elif marks >= 40:
        print("Grade C")

    else:
        print("Fail")

marks = float(input("Enter marks: "))

calculate_grade(marks)