import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Thread 1
class TaskOne extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Task One: " + i);
        }
    }
}

// Thread 2
class TaskTwo extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Task Two: " + i);
        }
    }
}

public class FileAndThreadApp {

    public static void readFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        // File Reading
        System.out.println("Reading File:");
        readFile();

        // Multithreading
        System.out.println("\nStarting Threads:");

        TaskOne t1 = new TaskOne();
        TaskTwo t2 = new TaskTwo();

        t1.start();
        t2.start();
    }
}