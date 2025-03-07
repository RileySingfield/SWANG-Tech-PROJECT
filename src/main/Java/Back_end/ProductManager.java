package Back_end;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private List<Product> products;
    private final String FILE_PATH = "src/main/resources/makeup_data.json";

    public ProductManager() {
        this.products = readFile();
    }

    // Read JSON file and load products
    public List<Product> readFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Product> loadedProducts = mapper.readValue(new File(FILE_PATH), new TypeReference<List<Product>>() {});

            // Clean data: Ensure all values are valid
            for (Product product : loadedProducts) {
                if (product.getPrice() == null || product.getPrice().isEmpty()) {
                    product.setPrice("0.00"); // Default price
                }
                if (product.getRating() == null) {
                    product.setRating(0.0); // Default rating
                }
                if (product.getDescription() == null) {
                    product.setDescription("No description available.");
                }
            }
            return loadedProducts;

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Save changes to the JSON file
    public void saveToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), products);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get all products
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Get product by ID
    public Product getProductById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst().orElse(null);
    }

    // Add a new product
    public void addProduct(Product product) {
        products.add(product);
        saveToFile();
    }

    // Update a product
    public boolean updateProduct(int id, Product updatedProduct) {
        if (updatedProduct == null) {
            throw new IllegalArgumentException("Updated product cannot be null.");
        }

        for (int i = 0; i < products.size(); i++) {
            Product existingProduct = products.get(i);
            if (existingProduct.getId() == id) {
                // Update only the editable fields
                existingProduct.setName(updatedProduct.getName());
                existingProduct.setPrice(updatedProduct.getPrice());
                existingProduct.setDescription(updatedProduct.getDescription());
                existingProduct.setCategory(updatedProduct.getCategory());
                existingProduct.setRating(updatedProduct.getRating());
                existingProduct.setBrand(updatedProduct.getBrand());

                saveToFile(); // Save changes to file
                return true;
            }
        }
        return false; // Product not found
    }

    // Delete a product
    public boolean deleteProduct(int id) {
        boolean removed = products.removeIf(product -> product.getId() == id);
        if (removed) saveToFile();
        return removed;
    }

    // Search products
    public List<Product> searchProducts(String query) {
        String lowerQuery = query.toLowerCase();
        List<Product> searchResults = new ArrayList<>();
        for (Product product : products) {
            if ((product.getName() != null && product.getName().toLowerCase().contains(lowerQuery)) ||
                    (product.getBrand() != null && product.getBrand().toLowerCase().contains(lowerQuery)) ||
                    (product.getCategory() != null && product.getCategory().toLowerCase().contains(lowerQuery))) {
                searchResults.add(product);
            }
        }
        return searchResults;
    }

    // Filter products
    public List<Product> filterProducts(List<Product> products, String category, double minPrice, double maxPrice, double minRating) {
        List<Product> filteredProducts = new ArrayList<>(); // Create a new list to store filtered products

        for (Product product : products) {
            boolean categoryMatches = category.equals("All") || matchesCategory(product, category);
            if (categoryMatches &&
                    matchesPrice(product, minPrice, maxPrice) &&
                    matchesRating(product, minRating)) {
                filteredProducts.add(product); // Add matching products to the new list
            }
        }

        return filteredProducts; // Return the filtered list
    }


    // Helper function to check category match
    private boolean matchesCategory(Product product, String category) {
        return category == null || category.isEmpty() ||
                (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category));
    }

    // Helper function to check price range match
    private boolean matchesPrice(Product product, double minPrice, double maxPrice) {
        if (product.getPrice() == null || !isNumeric(product.getPrice())) {
            return false;
        }
        double price = Double.parseDouble(product.getPrice());
        return price >= minPrice && price <= maxPrice;
    }

    // Helper function to check rating match
    private boolean matchesRating(Product product, double minRating) {
        return product.getRating() != null && product.getRating() >= minRating;
    }

    // Helper function to check if a string is a valid number
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean productExists(String name, String brand) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name) && p.getBrand().equalsIgnoreCase(brand)) {
                return true; // Duplicate found
            }
        }
        return false;
    }
}
