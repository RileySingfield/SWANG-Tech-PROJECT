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
        productManager = new ProductManager(); // Loads existing data
    }

    // ✅ Test if product list loads correctly
    @Test
    void testReadFile() {
        List<Product> products = productManager.getAllProducts();
        assertNotNull(products, "Product list should not be null.");
        assertFalse(products.isEmpty(), "Product list should not be empty.");
    }

    // ✅ Test adding a new product (without modifying real JSON)
    @Test
    void testAddProduct() {
        Product testProduct = new Product(
                99999, "TestBrand", "Test Lipstick", "20.00",
                "https://example.com/image.jpg", "Test lipstick description",
                "lipstick", "Lipstick", 4.5);

        boolean success = productManager.addProduct(testProduct);
        assertTrue(success, "Product should be added successfully.");

        Product retrievedProduct = productManager.getProductById(99999);
        assertNotNull(retrievedProduct, "Added product should exist.");
        assertEquals("Test Lipstick", retrievedProduct.getName());
    }

    @Test
    void testUpdateProduct() {
        Product updatedProduct = new Product(
                99999, "UpdatedBrand", "Updated Lipstick", "25.00",
                "https://example.com/image2.jpg", "Updated description",
                "lipstick", "Lipstick", 4.8);

        boolean success = productManager.updateProduct(99999, updatedProduct);
        assertTrue(success, "Product should be updated successfully.");

    }

    @Test
    void testDeleteProduct() {
        boolean success = productManager.deleteProduct(99999);
        assertTrue(success, "Product should be deleted successfully.");
        assertNull(productManager.getProductById(99999), "Deleted product should no longer exist.");
    }

    @Test
    void testSearchProduct() {
        List<Product> searchResults = productManager.searchProducts("Lipstick");
        assertNotNull(searchResults, "Search results should not be null.");
        assertFalse(searchResults.isEmpty(), "Search should return at least one product.");
    }

    @Test
    void testSearchProductNotFound() {
        String query = "NonExistingProduct";
        List<Product> searchResults = productManager.searchProducts(query);

        assertNotNull(searchResults, "Search results should not be null.");
        assertTrue(searchResults.isEmpty(), "Search should return an empty list if no products match.");
    }

    @Test
    void testFilterProducts() {
        List<Product> filteredProducts = productManager.filterProducts(
                productManager.getAllProducts(), "lipstick", 10.00, 50.00, 3.5);

        assertNotNull(filteredProducts, "Filtered products should not be null.");
        assertFalse(filteredProducts.isEmpty(), "There should be products that match the filter.");
    }




}
