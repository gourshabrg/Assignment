# 11. Check whether a year is a leap year.

def check_leap_year(year):

    if (year % 400 == 0) or (year % 4 == 0 and year % 100 != 0):
        print("Leap Year")
    else:
        print("Not a Leap Year")

year = int(input("Enter year: "))

check_leap_year(year)