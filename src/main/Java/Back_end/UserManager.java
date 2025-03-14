package Back_end;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, ProductUser> users = new HashMap<>();
    private ProductUser loggedInUser = null;
    //TODO add register method
    //TODO add a method that checks if a username is taken already
    public UserManager() {
        users.put("adminAminah", new ProductUser("adminAminah", "passwd1", "admin"));
        users.put("gillian", new ProductUser("gillian", "test", "admin"));
        users.put("generalUserAminah", new ProductUser("generalUserAminah", "passwd2", "regular"));
    }

    public boolean login(String username, String password) {
        ProductUser user = users.get(username);
        if (user != null && (user.getPassword()).equals(password)) {
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

    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.isAdmin();
    }
}

