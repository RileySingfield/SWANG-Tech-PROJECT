import Back_end.Product;
import Back_end.ProductUser;
import Back_end.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserManagerTest {

    private UserManager userManager;

    @BeforeEach
    void setUp() {
        // Use a spy to override appendUserToCSV() and avoid modifying the real CSV
        userManager = spy(new UserManager());
        doNothing().when(userManager).appendUserToCSV(any(ProductUser.class));
    }

    // UT-17-CB: Login with valid credentials
    @Test
    void testLoginValidCredentials() {
        // Simulate registration
        userManager = new UserManager(); // reinitialize with real load
        String username = "junituser";
        String password = "1234";

        // Manually add the user (without writing to file)
        userManager.register(username, password);

        boolean loginSuccess = userManager.login(username, password);
        assertTrue(loginSuccess, "Login should succeed with correct credentials.");
        assertEquals(username, userManager.getLoggedInUser().getUsername());
    }

    // UT-18-CB: Login with invalid credentials
    @Test
    void testLoginInvalidCredentials() {
        userManager = new UserManager(); // reinitialize with real load
        boolean result = userManager.login("nonexistentUser", "wrongpass");
        assertFalse(result, "Login should fail with incorrect credentials.");
    }

    // UT-19-CB: Logout test
    @Test
    void testLogout() {
        userManager = new UserManager(); // reinitialize with real load
        userManager.register("logoutUser", "test123");
        userManager.login("logoutUser", "test123");

        assertNotNull(userManager.getLoggedInUser(), "User should be logged in before logout.");
        userManager.logout();
        assertNull(userManager.getLoggedInUser(), "Logged-in user should be null after logout.");
    }

    // UT-20-CB: Register with existing user credentials
    @Test
    void testRegisterExistingUser() {
        userManager = new UserManager();
        String username = "existingUser";
        String password = "pass";

        userManager.register(username, password);
        boolean secondAttempt = userManager.register(username, password);
        assertFalse(secondAttempt, "Should not register an already existing user.");
    }

    // TC-025: Register new user with valid credentials
    @Test
    void testRegisterNewUser() {
        String username = "newTestUser";
        String password = "pass123";

        boolean success = userManager.register(username, password);
        assertTrue(success, "Registration with valid credentials should succeed.");
    }


}
