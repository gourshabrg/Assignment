from datetime import time


def time_to_str(value: time) -> str:

    return value.isoformat()


def str_to_time(value: str) -> time:

    return time.fromisoformat(value)
