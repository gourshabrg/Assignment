from datetime import time
from utils.time_utils import time_to_str, str_to_time


class TestTimeToStr:

    def test_formats_as_iso_string(self):
        assert time_to_str(time(9, 5, 0)) == "09:05:00"

    def test_zero_pads_single_digits(self):
        assert time_to_str(time(1, 2, 3)) == "01:02:03"


class TestStrToTime:

    def test_parses_iso_string(self):
        assert str_to_time("09:05:00") == time(9, 5, 0)


class TestRoundTrip:

    def test_converts_back_to_the_same_time(self):
        assert str_to_time(time_to_str(time(14, 30))) == time(14, 30)
