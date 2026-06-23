import threading

# Demonstrate the use of join() method in threading.

def display_message():

    print("Thread Running")


thread = threading.Thread(
    target=display_message
)

thread.start()

thread.join()

print("Thread Finished")