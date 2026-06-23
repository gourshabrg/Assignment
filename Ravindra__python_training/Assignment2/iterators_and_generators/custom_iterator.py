# Write a custom iterator class that returns numbers from 1 to N.

class NumberIterator:

    def __init__(self, limit):

        self.limit = limit
        self.current = 1

    def __iter__(self):
        return self

    def __next__(self):

        if self.current <= self.limit:

            value = self.current
            self.current += 1

            return value

        raise StopIteration


numbers = NumberIterator(5)

for number in numbers:
    print(number)