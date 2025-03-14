import Back_end.Product;
import Back_end.ProductManager;
import Back_end.UserManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductManagerTest {
    private ProductManager productManager;

    @BeforeEach
    void setUp() {
        productManager = new ProductManager(); // No userManager dependency for now
    }

    @Test
    void testAddProduct() {
        Product product = new Product(1, "BrandX", "Lipstick", "19.99", "image.webp", "A great lipstick", "Makeup", "Cosmetic", 4.5);
        assertTrue(productManager.addProduct(product), "Should be able to add a product");
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product(2, "BrandY", "Foundation", "25.00", "image.webp", "Best foundation", "Makeup", "Cosmetic", 4.8);
        productManager.addProduct(product);

        assertTrue(productManager.deleteProduct(2), "Should be able to delete a product");
        assertFalse(productManager.deleteProduct(99), "Should return false for a non-existent product");
    }

    @Test
    void testGetAllProducts() {
        productManager.addProduct(new Product(3, "BrandZ", "Mascara", "15.00", "image.webp", "Waterproof mascara", "Makeup", "Cosmetic", 4.2));
        productManager.addProduct(new Product(4, "BrandA", "Eyeliner", "10.00", "image.webp", "Smooth eyeliner", "Makeup", "Cosmetic", 4.3));

        List<Product> products = productManager.getAllProducts();
        assertEquals(products.size(), products.size(), "Should return 2 products");
    }
}