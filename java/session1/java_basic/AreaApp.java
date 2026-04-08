import java.util.Scanner;

class AreaCalculator {
    public double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }
    public double calculateRectangleArea(double length, double width) {
        return length * width;
    }
    public double calculateTriangleArea(double base, double height) {
        return 0.5 * base * height;
    }
}
public class AreaApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AreaCalculator calculator = new AreaCalculator();
        System.out.println("Choose shape: 1.Circle 2.Rectangle 3.Triangle");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.print("Enter radius: ");
                double radius = scanner.nextDouble();
                System.out.println("Area: " + calculator.calculateCircleArea(radius));
                break;

            case 2:
                System.out.print("Enter length & width: ");
                double length = scanner.nextDouble();
                double width = scanner.nextDouble();
                System.out.println("Area: " + calculator.calculateRectangleArea(length, width));
                break;
            case 3:
                System.out.print("Enter base & height: ");
                double base = scanner.nextDouble();
                double height = scanner.nextDouble();
                System.out.println("Area: " + calculator.calculateTriangleArea(base, height));
                break;
            default:
                System.out.println("Invalid choice");
        }
    }
}