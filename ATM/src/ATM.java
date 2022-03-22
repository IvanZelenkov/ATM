import java.util.Scanner;

public class ATM {
    public static void main(String[] args) {
        // initialize scanner
        Scanner scanner = new Scanner(System.in);

        // initialize Bank
        Bank theBank = new Bank("JPMorgan Chase & Co.");

        // add a user, which also creates a savings account
        User user1 = theBank.addUser("Ivan", "Zelenkov", "1234");

        // add a checking account for our user
        Account newAccount = new Account("Checking", user1, theBank);
        user1.addAccount(newAccount);
        theBank.addAccount(newAccount);

        User curUser;
        while (true) {
            // stay in the login prompt until successful login
            curUser = ATM.mainMenuPrompt(theBank, scanner);

            // stay in main many until user quits
            ATM.printUserMenu(curUser, scanner);
        }
    }

    public static User mainMenuPrompt(Bank theBank, Scanner scanner) {
        // initialize
        String userID;
        String pin;
        User authUser;

        // prompt the user for user ID/pin combo until a correct one is reached
        do {
            System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
            System.out.print("Enter user ID: ");
            userID = scanner.nextLine();
            System.out.print("Enter pin: ");
            pin = scanner.nextLine();

            // try to get the user object corresponding to the ID and pin combo
            authUser = theBank.userLogin(userID, pin);
            if (authUser == null) {
                System.out.println("Incorrect user ID/pin combination. Please try again");
            }
        } while(authUser == null); // continue looping until successful login

        return authUser;
    }

    public static void printUserMenu(User theUser, Scanner scanner) {
        // print a summary of the user's accounts
        theUser.printAccountSummary();

        // initialize
        int choice;

        // user menu
        do {
            System.out.printf("Welcome %s, what would you like to do?\n", theUser.getFirstName());
            System.out.println("    1) Show account transaction history");
            System.out.println("    2) Withdraw");
            System.out.println("    3) Deposit");
            System.out.println("    4) Transfer");
            System.out.println("    5) Quit");
            System.out.println();
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please choose 1-5");
            }
        } while(choice < 1 || choice > 5);

        // process the choice
        switch (choice) {
            case 1 -> ATM.showTransactionHistory(theUser, scanner);
            case 2 -> ATM.withdrawFunds(theUser, scanner);
            case 3 -> ATM.depositFunds(theUser, scanner);
            case 4 -> ATM.transferFunds(theUser, scanner);
            case 5 -> scanner.nextLine();
        }

        // redisplay this menu unless the user wants to quit
        if (choice != 5) {
            ATM.printUserMenu(theUser, scanner);
        }
    }

    /**
     * Show the transaction history for an account
     * @param theUser the logged-in User object
     * @param scanner the Scanner object used for user input
     */
    public static void showTransactionHistory(User theUser, Scanner scanner) {
        int theAccount;

        // get account whose transaction history to look at
        do {
            System.out.printf("Enter the number (1-%d) of the account whose " +
                            "transactions you want to use: ", theUser.numAccounts());
            theAccount = scanner.nextInt() - 1;
            if (theAccount < 0 || theAccount >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while(theAccount < 0 || theAccount >= theUser.numAccounts());

        // print the transactions history
        theUser.printAccountTransactionHistory(theAccount);
    }

    /**
     * Process transferring funds from one account to another
     * @param theUser   the logged-in User object
     * @param scanner   the Scanner object used for user input
     */
    public static void transferFunds(User theUser, Scanner scanner) {
        // initialize
        int fromAccount;
        int toAccount;
        double amount;
        double actualBalance;

        // get the account to transfer from
        do {
            System.out.printf("Enter the number (1-%d) of the account " +
                    "to transfer from: ", theUser.numAccounts());
            fromAccount = scanner.nextInt() - 1;
            if (fromAccount < 0 || fromAccount >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while(fromAccount < 0 || fromAccount >= theUser.numAccounts());

        actualBalance = theUser.getAccountBalance(fromAccount);

        // get the account to transfer to
        do {
            System.out.printf("Enter the number (1-%d) of the account " +
                    "to transfer to: ", theUser.numAccounts());
            toAccount = scanner.nextInt() - 1;
            if (toAccount < 0 || toAccount >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while(toAccount < 0 || toAccount >= theUser.numAccounts());

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to tranfer (max $%.02f): $", actualBalance);
            amount = scanner.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > actualBalance) {
                System.out.printf("Amount must not be grrster than balance of $%.02f.\n", actualBalance);
            }
        } while(amount < 0 || amount > actualBalance);

        // finally, do the transfer
        theUser.addAccountTransaction(fromAccount, -1*amount, String.format("Transfer to account %s", theUser.getAccountUUID(toAccount)));
        theUser.addAccountTransaction(toAccount, amount, String.format("Transfer to account %s", theUser.getAccountUUID(fromAccount)));
    }

    /**
     * Process a fund withdraw from an account
     * @param theUser   the logged-in User object
     * @param scanner   the Scanner object used for user input
     */
    public static void withdrawFunds(User theUser, Scanner scanner) {
        // initialize
        int fromAccount;
        double amount;
        double actualBalance;
        String memo;

        // get the account to transfer from
        do {
            System.out.printf("Enter the number (1-%d) of the account " +
                    "to withdraw from: ", theUser.numAccounts());
            fromAccount = scanner.nextInt() - 1;
            if (fromAccount < 0 || fromAccount >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while(fromAccount < 0 || fromAccount >= theUser.numAccounts());

        actualBalance = theUser.getAccountBalance(fromAccount);

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to withdraw (max $%.02f): $", actualBalance);
            amount = scanner.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > actualBalance) {
                System.out.printf("Amount must not be greater than balance of $%.02f.\n", actualBalance);
            }
        } while(amount < 0 || amount > actualBalance);

        // gobble up rest of previous input
        scanner.nextLine();

        // get a memo
        System.out.print("Enter a memo: ");
        memo = scanner.nextLine();

        // do the withdraw
        theUser.addAccountTransaction(fromAccount, -1*amount, memo);
    }

    /**
     * Process a fund deposit to an account
     * @param theUser   the logged-in User object
     * @param scanner   the Scanner object used for user input
     */
    public static void depositFunds(User theUser, Scanner scanner) {
        // initialize
        int toAccount;
        double amount;
        double actualBalance;
        String memo;

        // get the account to transfer from
        do {
            System.out.printf("Enter the number (1-%d) of the account " +
                    "to deposit in: ", theUser.numAccounts());
            toAccount = scanner.nextInt() - 1;
            if (toAccount < 0 || toAccount >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while(toAccount < 0 || toAccount >= theUser.numAccounts());

        actualBalance = theUser.getAccountBalance(toAccount);

        // get the amount to transfer
        do {
            System.out.printf("Enter the amount to deposit (max $%.02f): $", actualBalance);
            amount = scanner.nextDouble();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            }
        } while(amount < 0);

        // gobble up rest of previous input
        scanner.nextLine();

        // get a memo
        System.out.print("Enter a memo: ");
        memo = scanner.nextLine();

        // do the withdraw
        theUser.addAccountTransaction(toAccount, amount, memo);
    }
}
