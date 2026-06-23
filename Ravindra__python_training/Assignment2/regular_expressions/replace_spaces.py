# Replace multiple spaces in a string with a single space using re.sub().

import re

def remove_extra_spaces(text):

    updated_text = re.sub(r"\s+", " ", text)

    print(f"Result = {updated_text}")


text = "Python     is      very      easy"

remove_extra_spaces(text)