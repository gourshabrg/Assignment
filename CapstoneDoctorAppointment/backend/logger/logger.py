import logging
import sys


LOG_FORMAT = (
    "%(asctime)s | %(levelname)s | %(name)s | %(message)s"
)


def get_logger(name: str) -> logging.Logger:
    """Returns a configured logger, reusing it if already set up."""

    logger = logging.getLogger(name)

    if not logger.handlers:

        logger.setLevel(logging.INFO)

        handler = logging.StreamHandler(sys.stdout)

        handler.setFormatter(
            logging.Formatter(LOG_FORMAT)
        )

        logger.addHandler(handler)

        logger.propagate = False

    return logger
