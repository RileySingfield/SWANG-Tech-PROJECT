import Back_end.Product;
import Back_end.ProductManager;
import Back_end.UserManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserManagerTest {
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager(); // Reset user manager before each test
    }

    @Test
    void testValidAdminLogin() {
        assertTrue(userManager.login("admin", "passwd1"), "Admin should be able to log in");
        assertNotNull(userManager.getLoggedInUser(), "Logged-in user should not be null");
        assertTrue(userManager.isAdmin(), "Logged-in user should be an admin");
    }

    @Test
    void testValidRegularUserLogin() {
        assertTrue(userManager.login("generalUser", "passwd2"), "Regular user should be able to log in");
        assertNotNull(userManager.getLoggedInUser(), "Logged-in user should not be null");
        assertFalse(userManager.isAdmin(), "Logged-in user should NOT be an admin");
    }

    @Test
    void testInvalidLogin() {
        assertFalse(userManager.login("wrongUser", "wrongPass"), "Invalid user should not be able to log in");
        assertNull(userManager.getLoggedInUser(), "Logged-in user should be null after failed login");
    }

    @Test
    void testLogout() {
        userManager.login("admin", "passwd1");
        assertNotNull(userManager.getLoggedInUser(), "User should be logged in before logout");

        userManager.logout();
        assertNull(userManager.getLoggedInUser(), "User should be null after logout");
    }

    @Test
    void testAdminCheck() {
        userManager.login("admin", "passwd1");
        assertTrue(userManager.isAdmin(), "Admin should have admin privileges");

        userManager.login("generalUser", "passwd2");
        assertFalse(userManager.isAdmin(), "Regular users should not have admin privileges");
    }
}