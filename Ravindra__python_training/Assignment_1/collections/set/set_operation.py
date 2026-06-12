# 30. Perform union, intersection, and difference on two sets.

def set_operations(set1, set2):

    print("Union =", set1.union(set2))

    print("Intersection =", set1.intersection(set2))

    print("Difference =", set1.difference(set2))


set1 = {1, 2, 3, 4, 5}
set2 = {4, 5, 6, 7, 8}

set_operations(set1, set2)