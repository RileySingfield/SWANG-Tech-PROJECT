package com.beauty.catalog;


import com.beauty.catalog.Backend.Product;
import com.beauty.catalog.Backend.ProductManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProductManager productManager = new ProductManager();

       /* // 2. Add a new test product
        Product newProduct = new Product(
                99999, "Test Brand", "Test Lipstick", "19.99",
                "https://example.com/image.jpg", "A test product description.",
                "lipstick", "cosmetic", 4.5
        );
        productManager.addProduct(newProduct);


        // 3. Update the test product
        Product updatedProduct = new Product(
                99999, "Updated Brand", "Updated Test Lipstick", "22.99",
                "https://example.com/updated.jpg", "Updated description.",
                "lipstick", "cosmetic", 4.8
        );
        boolean updateSuccess = productManager.updateProduct(99999, updatedProduct);




        // 4. Search for a product
        System.out.println("\n🔎 Searching for 'Lipstick':");
        List<Product> searchResults = productManager.searchProducts("Lipstick");
        searchResults.forEach(System.out::println);

        // 5. Filter products by category, price range, and rating
        System.out.println("\n🏷️ Filtering products (Category: lipstick, Price: $10-$50, Rating: 4.0+):");
        List<Product> filteredProducts = productManager.filterProducts("lipstick", 10.0, 50.0, 4.0);
        filteredProducts.forEach(System.out::println);
*/
        // 6. Delete the test product
        boolean deleteSuccess = productManager.deleteProduct(99999);
        System.out.println("\n🗑️ Deleting Test Product (ID: 99999): " + deleteSuccess);
        productManager.getAllProducts().forEach(System.out::println);


    }
}
