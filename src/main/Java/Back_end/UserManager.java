package Back_end;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Manages user registration, login, logout, and persistence.
 */
public class UserManager {
    private Map<String, ProductUser> users = new HashMap<>();
    private ProductUser loggedInUser = null;
    private final String USER_FILE = "src/main/resources/users.csv";

    // Default users
    /**
     * Loads users from CSV file into memory.
     */
    public UserManager() {
        loadUsersFromCSV();
    }
    /**
     * Loads existing users from a CSV file.
     */
    private void loadUsersFromCSV() {
        File file = new File(USER_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    users.put(username, new ProductUser(username, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Appends a new user to the CSV file.
     *
     * @param user The user to append.
     */
    public void appendUserToCSV(ProductUser user) {
        try (FileWriter fw = new FileWriter(USER_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print("\n"+user.getUsername() + "," + user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }
    /**
     * Checks if a user exists by username.
     *
     * @param username The username to check.
     * @return true if the user exists.
     */
    public boolean register(String username, String password) {
        if (userExists(username)) return false;
        ProductUser newUser = new ProductUser(username, password);
        users.put(username, newUser);
        appendUserToCSV(newUser);
        loggedInUser = newUser;
        return true;
    }
    /**
     * Registers a new user.
     *
     * @return true if registration is successful.
     */
    public boolean login(String username, String password) {
        ProductUser user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            return true;
        }
        return false;
    }
    /**
     * Attempts to log in a user.
     *
     * @return true if login is successful.
     */
    public void logout() {
        loggedInUser = null;
    }
    /**
     * Logs out the current user.
     */
    public ProductUser getLoggedInUser() {
        return loggedInUser;
    }
}
