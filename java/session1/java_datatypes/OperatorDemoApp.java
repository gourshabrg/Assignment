public class OperatorDemoApp {

    public static void main(String[] args) {

        int a = 10;
        int b = 5;

        // Arithmetic Operators
        System.out.println("Arithmetic Operators:");
        System.out.println("Addition: " + (a + b));
        System.out.println("Subtraction: " + (a - b));
        System.out.println("Multiplication: " + (a * b));
        System.out.println("Division: " + (a / b));

        // Relational Operators
        System.out.println("\nRelational Operators:");
        System.out.println("a > b: " + (a > b));
        System.out.println("a < b: " + (a < b));
        System.out.println("a == b: " + (a == b));

        // Logical Operators
        System.out.println("\nLogical Operators:");
        System.out.println("(a > 5 && b < 10): " + (a > 5 && b < 10));
        System.out.println("(a > 5 || b > 10): " + (a > 5 || b > 10));
        System.out.println("!(a > b): " + !(a > b));
    }
}