import java.util.Scanner;

class FactorialCalculator {

    public long calculateFactorial(int number) {
        long factorial = 1;

        for (int i = 2; i <= number; i++) {
            factorial *= i;
        }

        return factorial;
    }
}

public class FactorialApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FactorialCalculator calculator = new FactorialCalculator();

        System.out.print("Enter number: ");
        int number = scanner.nextInt();

        System.out.println("Factorial: " + calculator.calculateFactorial(number));
    }
}