import Back_end.Product;
import Back_end.ProductManager;
import Back_end.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagerTest {
    private UserManager userManager;
    private Product testProduct;


    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        testProduct = new Product(12345, "WishlistBrand", "Wishlist Lipstick", "15.00",
                "https://example.com/image.jpg", "Wishlist product description",
                "lipstick", "Lipstick", 4.7);
    }

    @Test
    void testRegisterNewUser() {
        assertTrue(userManager.register("newUser", "password123"), "User should be registered successfully");
        assertFalse(userManager.register("newUser", "newPass"), "Duplicate username should not be allowed");
    }

    @Test
    void testUserExists() {
        assertTrue(userManager.userExists("Aminah"), "Existing user should be found");
        assertFalse(userManager.userExists("nonExistingUser"), "Non-existent user should not be found");
    }

    @Test
    void testLoginSuccess() {
        assertTrue(userManager.login("gillian", "test"), "User should log in with correct credentials");
    }

    @Test
    void testLoginFailure() {
        assertFalse(userManager.login("gillian", "wrongPassword"), "User should not log in with incorrect password");
        assertFalse(userManager.login("nonExistingUser", "password"), "Non-existent user should not log in");
    }


    @Test
    void testLogout() {
        userManager.login("Aminah", "passwd1");
        assertNotNull(userManager.getLoggedInUser(), "User should be logged in");

        userManager.logout();
        assertNull(userManager.getLoggedInUser(), "User should be logged out");
    }

    /*@Test
    void testAddToWishlist() {
        boolean success = userManager.addToWishlist("Aminah", testProduct);
        assertTrue(success, "Product should be added to wishlist.");
    }

    @Test
    void testGetWishlist() {
        List<Product> wishlist = userManager.getWishlist("Aminah");
        assertNotNull(wishlist, "Wishlist should not be null.");
        assertTrue(wishlist.isEmpty(), "Wishlist should be empty for a new user.");
    }

    @Test
    void testRemoveFromWishlist() {
        boolean success = userManager.removeFromWishlist("Aminah", testProduct.getId());
        assertTrue(success, "Product should be removed from wishlist.");
    }*/
}
