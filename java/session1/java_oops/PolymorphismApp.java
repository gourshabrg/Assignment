import java.util.Scanner;

class Calculator {

    // Method 1
    public int add(int a, int b) {
        return a + b;
    }

    // Method 2 (overloaded)
    public double add(double a, double b) {
        return a + b;
    }

    // Method 3 (overloaded)
    public int add(int a, int b, int c) {
        return a + b + c;
    }
}

public class PolymorphismApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Calculator calc = new Calculator();

        // Input for 2 integers
        System.out.print("Enter two integers: ");
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        System.out.println("Sum (int): " + calc.add(a, b));

        // Input for 2 doubles
        System.out.print("Enter two decimal numbers: ");
        double x = scanner.nextDouble();
        double y = scanner.nextDouble();
        System.out.println("Sum (double): " + calc.add(x, y));

        // Input for 3 integers
        System.out.print("Enter three integers: ");
        int p = scanner.nextInt();
        int q = scanner.nextInt();
        int r = scanner.nextInt();
        System.out.println("Sum (3 numbers): " + calc.add(p, q, r));

        scanner.close();
    }
}