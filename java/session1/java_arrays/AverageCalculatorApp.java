import java.util.Scanner;

public class AverageCalculatorApp {

    public static double calculateAverage(int[] numbers) {
        int sum = 0;

        for (int num : numbers) {
            sum += num;
        }

        return (double) sum / numbers.length;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of elements: ");
        int size = scanner.nextInt();

        int[] numbers = new int[size];

        System.out.println("Enter elements:");
        for (int i = 0; i < size; i++) {
            numbers[i] = scanner.nextInt();
        }

        double average = calculateAverage(numbers);
        System.out.println("Average: " + average);
    }
}