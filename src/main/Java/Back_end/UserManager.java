package Back_end;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, ProductUser> users = new HashMap<>();
    private ProductUser loggedInUser = null;
    private final String USER_FILE = "src/main/resources/users.csv";

    // Default users
    public UserManager() {
        loadUsersFromCSV();
    }

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

    private void appendUserToCSV(ProductUser user) {
        try (FileWriter fw = new FileWriter(USER_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(user.getUsername() + "," + user.getPassword()+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public boolean register(String username, String password) {
        if (userExists(username)) return false;
        ProductUser newUser = new ProductUser(username, password);
        users.put(username, newUser);
        appendUserToCSV(newUser);
        loggedInUser = newUser;
        return true;
    }

    public boolean login(String username, String password) {
        ProductUser user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInUser = null;
    }

    public ProductUser getLoggedInUser() {
        return loggedInUser;
    }
}
