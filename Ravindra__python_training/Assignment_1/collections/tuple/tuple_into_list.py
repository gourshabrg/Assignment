# 29. Convert tuple into list and modify it.


def modify_tuple():

    numbers = (10, 20, 30, 40)

    numbers_list = list(numbers)

    numbers_list.append(50)

    print("Modified List =", numbers_list)


modify_tuple()