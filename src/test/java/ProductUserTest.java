import Back_end.Product;
import Back_end.ProductUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductUserTest {

    // TC-026: Add to wishlist
    @Test
    void testAddToWishlist() {
        ProductUser user = new ProductUser("wishlistUser", "pw");
        Product product = new Product(1001, "Brand", "Lipstick", "15.99", "", "", "lipstick", "Lipstick", 4.0);

        user.addToWishlist(product);
        List<Product> wishlist = user.getWishlist();

        assertEquals(1, wishlist.size());
        assertEquals(product.getId(), wishlist.get(0).getId(), "Product should be added to wishlist.");
    }

    // TC-027: Remove from wishlist
    @Test
    void testRemoveFromWishlist() {
        ProductUser user = new ProductUser("wishlistUser", "pw");
        Product product = new Product(1002, "Brand", "Lipstick", "15.99", "", "", "lipstick", "Lipstick", 4.0);

        user.addToWishlist(product);
        assertEquals(1, user.getWishlist().size());

        user.removeFromWishlist(product);
        assertTrue(user.getWishlist().isEmpty(), "Wishlist should be empty after removal.");
    }
}
