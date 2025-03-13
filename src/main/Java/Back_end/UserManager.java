package Back_end;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, ProductUser> users = new HashMap<>();
    private ProductUser loggedInUser = null;

    public UserManager() {
        users.put("admin", new ProductUser("adminAminah", "passwd1", "admin"));
        users.put("generalUser", new ProductUser("generalUserAminah", "passwd2", "regular"));
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

    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.isAdmin();
    }
}

