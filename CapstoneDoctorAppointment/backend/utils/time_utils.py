from datetime import time


def time_to_str(value: time) -> str:
    """MongoDB/BSON has no native time type, so Beanie documents store
    times as ISO strings (HH:MM:SS). Zero-padding keeps string
    comparisons/sorting equivalent to comparing time objects directly.
    """

    return value.isoformat()


def str_to_time(value: str) -> time:

    return time.fromisoformat(value)
