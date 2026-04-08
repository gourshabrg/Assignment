
public class EvenSumCalculator {

    private static final int START = 1;
    private static final int END = 10;

    public static int calculateEvenSum() {
        int sum = 0;
        int current = START;

        while (current <= END) {
            if (current % 2 == 0) {
                sum += current;
            }
            current++;
        }
        return sum;
    }

    public static void main(String[] args) {
        int result = calculateEvenSum();
        System.out.println("Sum of even numbers from 1 to 10: " + result);
    }
}