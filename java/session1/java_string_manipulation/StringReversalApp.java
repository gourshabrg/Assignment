import java.util.Scanner;

public class StringReversalApp {

   
    public static String reverseString(String input) {
        StringBuilder reversed = new StringBuilder(input);
        return reversed.reverse().toString(); 
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a string: ");
        String input = scanner.nextLine();

        String result = reverseString(input);
        System.out.println("Reversed String: " + result);
    }
}