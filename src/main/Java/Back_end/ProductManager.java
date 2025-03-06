package Back_end;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                products.set(i, updatedProduct);
                saveToFile();
                return true;
            }
        }
        return false;
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
    public List<Product> filterProducts(String category, double minPrice, double maxPrice, double minRating) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : products) {
            if (matchesCategory(product, category) &&
                    matchesPrice(product, minPrice, maxPrice) &&
                    matchesRating(product, minRating)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
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
}
