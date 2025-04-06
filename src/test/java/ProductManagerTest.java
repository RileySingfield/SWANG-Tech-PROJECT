import Back_end.Product;
import Back_end.ProductManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductManagerTest {
    private ProductManager productManager;

    @BeforeEach
    void setUp() {
        // Create a spy of ProductManager so we can mock just saveToFile()
        productManager = spy(new ProductManager());

        // Mock saveToFile() so it doesn't write to the JSON file
        doNothing().when(productManager).saveToFile();
    }

    // UT-01-CB: Add product with valid details
    @Test
    void testAddProduct_ValidDetails() {
        int id = 123456;
        productManager.deleteProduct(id); // Clean up

        Product testProduct = new Product(
                id, "BrandX", "JUnit Lipstick", "19.99",
                "url", "A test lipstick", "lipstick", "Lipstick", 4.0);

        boolean success = productManager.addProduct(testProduct);
        assertTrue(success);
        assertNotNull(productManager.getProductById(id));
    }

    // UT-02-CB: Add product with missing fields
    @Test
    void testAddProductWithMissingFields() {
        int id = 123457;
        productManager.deleteProduct(id);

        Product incompleteProduct = new Product(
                id, "", "", "", "", "", "", "", 0.0);

        boolean result = productManager.addProduct(incompleteProduct);
        assertTrue(result, "Product with missing fields is allowed as per current logic.");
    }

    // UT-03-CB: Add duplicate product
    @Test
    void testAddDuplicateProduct() {
        int id = 123458;
        productManager.deleteProduct(id);

        Product product = new Product(
                id, "TestBrand", "DuplicateProduct", "22.00",
                "url", "desc", "category", "type", 3.5);

        productManager.addProduct(product);
        boolean result = productManager.addProduct(product);
        assertFalse(result, "Duplicate product should not be added.");
    }

    // UT-04-CB: Update existing product
    @Test
    void testUpdateProduct_ValidUpdate() {
        int id = 123459;
        productManager.deleteProduct(id);

        Product original = new Product(
                id, "BaseBrand", "BaseProduct", "10.00", "", "", "", "", 3.0);
        productManager.addProduct(original);

        Product updated = new Product(
                id, "UpdatedBrand", "UpdatedProduct", "15.00", "", "", "", "", 4.5);
        boolean updatedSuccessfully = productManager.updateProduct(id, updated);

        assertTrue(updatedSuccessfully, "Product should be updated successfully.");
        Product result = productManager.getProductById(id);
        assertEquals("UpdatedProduct", result.getName());
    }

    // UT-05-CB: Update product with invalid input
    @Test
    void testUpdateProductWithInvalidInput() {
        int id = 123460;
        productManager.deleteProduct(id);

        productManager.addProduct(new Product(
                id, "Initial", "BadTest", "10.00", "", "", "", "", 3.0));

        Product invalid = new Product(
                id, "BadBrand", "BadProduct", "-5.00", "", "", "", "", 2.0);

        boolean result = productManager.updateProduct(id, invalid);
        assertTrue(result, "Update should succeed based on current logic.");
        Product updated = productManager.getProductById(id);
        assertEquals("-5.00", updated.getPrice());
    }

    // UT-06-CB: Delete product
    @Test
    void testDeleteProduct() {
        int id = 123461;
        productManager.addProduct(new Product(
                id, "DelBrand", "DelProduct", "12.00", "", "", "", "", 3.2));

        boolean success = productManager.deleteProduct(id);
        assertTrue(success, "Product should be deleted successfully.");
        assertNull(productManager.getProductById(id), "Product should no longer exist.");
    }

    // UT-09-CB: Search existing product
    @Test
    void testSearchProduct() {
        List<Product> searchResults = productManager.searchProducts("Lipstick");
        assertNotNull(searchResults);
        assertFalse(searchResults.isEmpty(), "Search should return at least one product.");
    }

    // UT-10-CB: Search non-existent product
    @Test
    void testSearchProductNotFound() {
        List<Product> searchResults = productManager.searchProducts("NONEXISTENT_987654321");
        assertNotNull(searchResults);
        assertTrue(searchResults.isEmpty(), "Search should return an empty list.");
    }

    // UT-11-CB: Case-insensitive search
    @Test
    void testSearchProductCaseInsensitive() {
        List<Product> results = productManager.searchProducts("lipSTICK");
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Search should be case-insensitive.");
    }

    //  Read file test (skip if your JSON is broken)
    @Test
    void testReadFile() {
        List<Product> products = productManager.getAllProducts();
        assertNotNull(products, "Product list should not be null.");
        assertFalse(products.isEmpty(), "Product list should not be empty.");
    }
}
