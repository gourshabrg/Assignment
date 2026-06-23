from prime import is_prime

# Test case 1

def test_prime_number():

    assert is_prime(7) == True


# Test case 2

def test_non_prime_number():

    assert is_prime(10) == False


# Test case 3

def test_zero():

    assert is_prime(0) == False