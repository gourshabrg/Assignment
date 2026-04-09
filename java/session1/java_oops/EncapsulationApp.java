import java.util.Scanner;

class BankAccount {
    private double balance; // private data

    // Getter
    public double getBalance() {
        return balance;
    }

    // Deposit method
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Amount deposited successfully.");
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    // Withdraw method
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Amount withdrawn successfully.");
        } else {
            System.out.println("Invalid withdrawal.");
        }
    }
}

public class EncapsulationApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        BankAccount account = new BankAccount();

        int choice;

        do {
            System.out.println("\n--- Bank Menu ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    account.deposit(depositAmount);
                    break;

                case 2:
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    account.withdraw(withdrawAmount);
                    break;

                case 3:
                    System.out.println("Current Balance: " + account.getBalance());
                    break;

                case 4:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 4);

        scanner.close();
    }
}