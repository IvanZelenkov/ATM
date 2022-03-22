import java.util.ArrayList;
import java.util.Random;

public class Bank {

    private String name;

    private ArrayList<User> users;

    private ArrayList<Account> accounts;

    /**
     * Create a new Bank object with empty lists of users and accounts
     * @param name the name of the bank
     */
    public Bank(String name) {
        this.name = name;
        this.users = new ArrayList<User>();
        this.accounts = new ArrayList<Account>();
    }

    /**
     * Generate a new universally unique ID for a user.
     * @return the uuid
     */
    public String getNewUserUUID() {
        // initialize
        String uuid;
        Random rng = new Random();
        int len = 6;
        boolean nonUnique;

        // continue looping until we get a unique ID
        do {
            // generate the number
            uuid = "";
            for (int i = 0; i < len; i++)
                uuid += ((Integer) rng.nextInt(10)).toString();

            // check to make sure it's unique
            nonUnique = false;
            for (User u : this.users) {
                if (uuid.compareTo(u.getUUID()) == 0) {
                    nonUnique = true;
                    break;
                }
            }
        } while(nonUnique);

        return uuid;
    }

    /**
     * Generate a new universally unique ID for an account
     * @return the uuid
     */
    public String getNewAccountUUID() {
        // initialize
        StringBuilder uuid;
        Random rng = new Random();
        int len = 10;
        boolean nonUnique;

        // continue looping until we get a unique ID
        do {
            // generate the number
            uuid = new StringBuilder();
            for (int i = 0; i < len; i++)
                uuid.append(((Integer) rng.nextInt(10)).toString());

            // check to make sure it's unique
            nonUnique = false;
            for (Account a : this.accounts) {
                if (uuid.toString().compareTo(a.getUUID()) == 0) {
                    nonUnique = true;
                    break;
                }
            }
        } while(nonUnique);

        return uuid.toString();
    }

    /**
     * Add an account
     * @param account the account to add
     */
    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    /**
     * Create a new user of the bank
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param pin       the user's pin
     * @return          the new User object
     */
    public User addUser(String firstName, String lastName, String pin) {
        // create a new User object and add it ti our list
        User newUser = new User(firstName, lastName, pin, this);
        this.users.add(newUser);

        // create a savings account for the user
        Account newAccount = new Account("Savings", newUser, this);
        newUser.addAccount(newAccount);
        this.addAccount(newAccount);

        return newUser;
    }

    public User userLogin(String userId, String pin) {
        // search through list of users
        for (User u : users) {
            // check user ID is correct
            if (u.getUUID().compareTo(userId) == 0 && u.validatePin(pin)) {
                return u;
            }
        }
        // if we haven't found the user or have an incorrect pin
        return null;
    }

    /**
     * Get the name of the bank
     * @return the name of the bank
     */
    public String getName() {
        return this.name;
    }
}
