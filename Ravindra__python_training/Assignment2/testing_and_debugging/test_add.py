from add import add_numbers

# Test case 1

def test_add_positive_numbers():

    assert add_numbers(10, 20) == 30


# Test case 2

def test_add_negative_numbers():

    assert add_numbers(-5, -5) == -10


# Test case 3

def test_add_zero():

    assert add_numbers(0, 0) == 0