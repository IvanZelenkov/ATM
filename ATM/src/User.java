import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.security.MessageDigest;

public class User {

    /**
     * The first name of the user
     */
    private String firstName;

    /**
     * The last name of the user
     */
    private String lastName;

    /**
     * The ID number of the user
     */
    private String uuid;

    /**
     * The MD5 hash of the user's pin number.
     */
    private byte pinHash[];

    /**
     * The list of accounts for this user.
     */
    private ArrayList<Account> accounts;

    /**
     * Create a new user
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param pin       the user's account pin number
     * @param theBank   the Bank object that the user is a customer of
     * @throws NoSuchAlgorithmException
     */
    public User(String firstName, String lastName, String pin, Bank theBank) {
        // set user's name
        this.firstName = firstName;
        this.lastName = lastName;

        // store the pin's MDS hash, rather then the original value, for security reasons
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        // get a new, unique universal ID for the user
        this.uuid = theBank.getNewUserUUID();

        // create empty list of accounts
        this.accounts = new ArrayList<Account>();

        // print log message
        System.out.printf("New user %s, %s with ID %s created.\n", lastName, firstName, uuid);
    }

    /**
     * Add an account for the user
     * @param account
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /**
     * Get the account ID
     * @return the uuid
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Check whether a given pin matches the true User pin
     * @param pin   the pin to check
     * @return      whether the pin is valid or not
     */
    public boolean validatePin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(md.digest(pin.getBytes()), pinHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    /**
     * Return the user's first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    public void printAccountSummary() {
        System.out.printf("\n\n%s's accounts summary\n", firstName);
        for (int a = 0; a < accounts.size(); a++) {
            System.out.printf("  %d) %s\n", a + 1, accounts.get(a).getSummaryLine());
        }
        System.out.println();
    }

    /**
     * Get the number of accounts of the user
     * @return  the number of accounts
     */
    public int numAccounts() {
        return accounts.size();
    }

    /**
     * Print transaction history for a particular account
     * @param accountIndex  the index of the account to use
     */
    public void printAccountTransactionHistory(int accountIndex) {
        accounts.get(accountIndex).printTransactionHistory();
    }

    /**
     * Get the balance of a particular account
     * @param accountIndex  the index of the account to use
     * @return              the balance of the account
     */
    public double getAccountBalance(int accountIndex) {
        return accounts.get(accountIndex).getBalance();
    }

    /**
     * Get the UUID of a particular amount
     * @param accountIndex  the index of the account to use
     * @return              the UUID of the account
     */
    public String getAccountUUID(int accountIndex) {
        return accounts.get(accountIndex).getUUID();
    }

    /**
     * Add a transaction to a particular amount
     * @param accountIndex  the index of the account
     * @param amount        the amount of the transaction
     * @param memo          the memo of the transaction
     */
    public void addAccountTransaction(int accountIndex, double amount, String memo) {
        accounts.get(accountIndex).addTransaction(amount, memo);
    }
}
