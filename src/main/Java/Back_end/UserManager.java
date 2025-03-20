package Back_end;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, ProductUser> users = new HashMap<>();
    private ProductUser loggedInUser = null;

    // Default users
    public UserManager() {
        users.put("adminAminah", new ProductUser("adminAminah", "passwd1"));
        users.put("gillian", new ProductUser("gillian", "test"));
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public boolean register(String username, String password) {
        if (userExists(username)) {
            return false;
        }
        users.put(username, new ProductUser(username, password));
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
