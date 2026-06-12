# 34. Merge two dictionaries.



def merge_dictionaries(dict1, dict2):  # Alternative method using | , merged_dictionary = dict1 | dict2

    # Create a copy of the first dictionary
    merged_dictionary = dict1.copy()

    # Add all key-value pairs from dict2
    merged_dictionary.update(dict2)

    print(f"Merged Dictionary = {merged_dictionary}")


# Creating dictionaries

student = {
    "name": "Ravindra",
    "age": 20
}

course = {
    "age":23,
    "course": "MCA",
    "college": "NIT Jamshedpur"
}

merge_dictionaries(student, course)