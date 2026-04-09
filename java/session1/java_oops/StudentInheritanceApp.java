import java.util.Scanner;
// Parent Class
class Student {
    private String name;
    private int rollNumber;
    private double marks;

    // Constructor
    public Student(String name, int rollNumber, double marks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = marks;
    }

    // Getter methods (Encapsulation)
    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public double getMarks() {
        return marks;
    }

    public void displayDetails() {
        System.out.println("Name: " + name);
        System.out.println("Roll No: " + rollNumber);
        System.out.println("Marks: " + marks);
    }
}

// Child Class
class GraduateStudent extends Student {
    private String specialization;

    public GraduateStudent(String name, int rollNumber, double marks, String specialization) {
        super(name, rollNumber, marks); // call parent constructor
        this.specialization = specialization;
    }

    public void displayGraduateDetails() {
        displayDetails(); // reuse parent method
        System.out.println("Specialization: " + specialization);
    }
}

// Main Class
public class StudentInheritanceApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Taking user input
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Roll Number: ");
        int rollNumber = scanner.nextInt();

        System.out.print("Enter Marks: ");
        double marks = scanner.nextDouble();
        scanner.nextLine(); // consume leftover newline

        System.out.print("Enter Specialization: ");
        String specialization = scanner.nextLine();

        GraduateStudent student =
                new GraduateStudent(name, rollNumber, marks, specialization);
        System.out.println("\n--- Student Details ---");
        student.displayGraduateDetails();
        scanner.close();
    }
}