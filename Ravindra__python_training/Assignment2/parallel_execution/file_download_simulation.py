import threading
import time

# Create multiple threads to simulate file downloading using time.sleep().

def download_file(file_name):

    print(f"Downloading {file_name}")

    time.sleep(3)

    print(f"{file_name} Downloaded")


thread1 = threading.Thread(
    target=download_file,
    args=("File1",)
)

thread2 = threading.Thread(
    target=download_file,
    args=("File2",)
)

thread1.start()

thread2.start()