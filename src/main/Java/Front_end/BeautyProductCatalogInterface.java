package Front_end;
import Back_end.*;
import Back_end.Product;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class BeautyProductCatalogInterface {
    private JFrame frame;
    private JPanel catalogPanel;
    private JPanel detailPanel;
    private JSplitPane mainSplitPane;
    private ProductManager productManager;
    private Product selectedProduct = null; // currently selected product
    private JTextField searchField; // top panel search field
    private Map<String, ImageIcon> imageCache = new HashMap<>(); // Cache for images
    private ExecutorService imageLoaderExecutor = Executors.newFixedThreadPool(4);

    // Filter components as instance variables
    private JComboBox<String> priceDropdown;
    private JSlider sliderRating;
    private JComboBox<String> categoryDropdown;

    public BeautyProductCatalogInterface() {
        productManager = new ProductManager();
        initializeUI();
    }

    private void initializeUI() {
        // Enable hardware acceleration
        System.setProperty("sun.java2d.opengl", "true");

        // Set Nimbus look and feel for a softer appearance.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        frame = new JFrame("Product Catalog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 800);
        frame.getContentPane().setBackground(new Color(245, 245, 245));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        JButton clearButton = new JButton("x");

        clearButton.setBackground(Color.WHITE);
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setPreferredSize(new Dimension(40, 25));
        clearButton.addActionListener(e -> searchField.setText(""));

        JButton removeSearchButton = new JButton("Remove Search");
        removeSearchButton.setBackground(Color.WHITE);
        removeSearchButton.setOpaque(true);
        removeSearchButton.setBorderPainted(false);
        removeSearchButton.setVisible(false);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.add(searchField);
        searchPanel.add(clearButton);
        searchPanel.add(removeSearchButton);

        JButton addButton = new JButton("Add");
        addButton.setBackground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);

        topPanel.add(searchPanel);
        topPanel.add(searchButton);
        topPanel.add(addButton);

        // FILTER
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filterPanel.setBackground(Color.WHITE);

        // Filters label
        JLabel filterLabel = new JLabel("Filters");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createVerticalStrut(10)); // Add spacing after label

        // Price filter
        JLabel priceLabel = new JLabel("Price Range:");
        priceDropdown = new JComboBox<>(new String[]{"All", "$0-10", "$10-20", "$20-30", "$30-40", "$40+"});
        priceDropdown.setPreferredSize(new Dimension(200, 25));

        JPanel priceDropdownPanel = new JPanel();
        priceDropdownPanel.setBackground(Color.WHITE);
        priceDropdownPanel.setLayout(new BoxLayout(priceDropdownPanel, BoxLayout.X_AXIS));
        priceDropdownPanel.add(priceLabel);
        priceDropdownPanel.add(Box.createHorizontalStrut(5));
        priceDropdownPanel.add(priceDropdown);
        filterPanel.add(priceDropdownPanel);
        filterPanel.add(Box.createVerticalStrut(5));

        // Rating filter
        JLabel ratingLabel = new JLabel("Minimum Rating:");
        sliderRating = new JSlider();
        sliderRating.setMaximum(5);
        sliderRating.setMinimum(0);
        sliderRating.setPreferredSize(new Dimension(120, 25));
        sliderRating.setBackground(Color.WHITE);
        sliderRating.setOpaque(true);
        JPanel ratingPanel = new JPanel();
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.X_AXIS));
        ratingPanel.add(ratingLabel);
        ratingPanel.add(Box.createHorizontalStrut(10));
        ratingPanel.add(sliderRating);
        filterPanel.add(ratingPanel);
        filterPanel.add(Box.createVerticalStrut(10));

        // Category filter
        JLabel categoryLabel = new JLabel("Category:");
        categoryDropdown = new JComboBox<>(new String[]{"All", "lipstick", "liquid", "powder", "palette", "pencil", "cream", "mineral", "lip_stain"});
        categoryDropdown.setPreferredSize(new Dimension(120, 25));
        JButton applyCategoryFilter = new JButton("Apply");
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(Color.WHITE);
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
        categoryPanel.add(categoryLabel);
        categoryPanel.add(Box.createHorizontalStrut(5));
        categoryPanel.add(categoryDropdown);
        categoryPanel.add(Box.createHorizontalStrut(5));
        categoryPanel.add(applyCategoryFilter);
        filterPanel.add(categoryPanel);

        // Clear Filters Button
        JButton clearFiltersButton = new JButton("Clear Filters");
        clearFiltersButton.setBackground(Color.WHITE);
        clearFiltersButton.setOpaque(true);
        clearFiltersButton.setBorderPainted(false);
        clearFiltersButton.addActionListener(e -> {
            // Reset filters to default values
            priceDropdown.setSelectedIndex(0); // Reset price dropdown to "All"
            sliderRating.setValue(0); // Reset rating slider to 0
            categoryDropdown.setSelectedIndex(0); // Reset category dropdown to "All"

            // Reload all products
            loadProducts(productManager.getAllProducts());
        });

        // Add the Clear Filters button to the filter panel
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(clearFiltersButton);

        // Apply Filters Button
        applyCategoryFilter.addActionListener(e -> {
            double minPrice = getMinPriceFromDropdown(priceDropdown.getSelectedItem().toString());
            double maxPrice = getMaxPriceFromDropdown(priceDropdown.getSelectedItem().toString());
            int minRating = sliderRating.getValue();
            String selectedCategory = categoryDropdown.getSelectedItem().toString();

            // Get search query
            String query = searchField.getText().trim();

            List<Product> results;
            if (query.isEmpty()) {
                // If no search query, apply only filters
                results = productManager.filterProducts(productManager.getAllProducts(), selectedCategory, minPrice, maxPrice, minRating);
            } else {
                // If there's a search query, combine it with filters
                results = productManager.searchProducts(query); // Get search results
                results = productManager.filterProducts(results, selectedCategory, minPrice, maxPrice, minRating); // Apply filters to search results
            }

            loadProducts(results); // Load the combined results
        });

        catalogPanel = new JPanel();
        catalogPanel.setBackground(Color.WHITE);
        catalogPanel.setLayout(new GridLayout(0, 3, 10, 10));
        loadProducts(productManager.getAllProducts());
        JScrollPane catalogScrollPane = new JScrollPane(catalogPanel);
        catalogScrollPane.setBorder(null);
        catalogScrollPane.getViewport().setBackground(Color.WHITE);

        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane detailScrollPane = new JScrollPane(detailPanel);
        detailScrollPane.setBorder(null);
        detailScrollPane.getViewport().setBackground(Color.WHITE);

        JSplitPane filterSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, catalogScrollPane);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterSplitPane, detailScrollPane);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setDividerLocation(frame.getWidth());
        mainSplitPane.setEnabled(false);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplitPane, BorderLayout.CENTER);
        frame.setVisible(true);

        searchPanel.add(removeSearchButton);
        // Search Button Action Listener
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                performSearch(); // Perform the search
                removeSearchButton.setVisible(true); // Make the button visible
            }
        });


        // Remove Search Button Action Listener
        removeSearchButton.addActionListener(e -> {
            searchField.setText(""); // Clear the search field
            removeSearchButton.setVisible(false); // Hide the button

            // Reload products with active filters (if any)
            double minPrice = getMinPriceFromDropdown(priceDropdown.getSelectedItem().toString());
            double maxPrice = getMaxPriceFromDropdown(priceDropdown.getSelectedItem().toString());
            int minRating = sliderRating.getValue();
            String selectedCategory = categoryDropdown.getSelectedItem().toString();

            loadProducts(productManager.filterProducts(productManager.getAllProducts(), selectedCategory, minPrice, maxPrice, minRating));
        });

        // Add Button Action Listener
        addButton.addActionListener(e -> showAddProductDialog());
    }

    private void loadProducts(List<Product> products) {
        catalogPanel.removeAll(); // Clear panel efficiently

        for (Product product : products) {
            JPanel productCard = new JPanel(new BorderLayout());
            productCard.setBackground(Color.WHITE);
            productCard.setBorder(BorderFactory.createLineBorder(Color.WHITE));

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedProduct = product;
                    try {
                        showProductDetails(product);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Load image asynchronously
            imageLoaderExecutor.submit(() -> {
                try {
                    ImageIcon productImage = fetchProductImage(product.getImageLink());
                    SwingUtilities.invokeLater(() -> imageLabel.setIcon(productImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            JButton detailsButton = new JButton(product.getName());
            detailsButton.setPreferredSize(new Dimension(100, 30));
            detailsButton.setBackground(Color.WHITE);
            detailsButton.setOpaque(true);
            detailsButton.setBorderPainted(false);
            detailsButton.addActionListener(e -> {
                selectedProduct = product;
                try {
                    showProductDetails(product);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            productCard.add(imageLabel, BorderLayout.CENTER);
            productCard.add(detailsButton, BorderLayout.SOUTH);
            catalogPanel.add(productCard);
        }

        catalogPanel.revalidate();
        catalogPanel.repaint();
    }

    public ImageIcon fetchProductImage(String imageUrl) throws IOException {
        if (imageCache.containsKey(imageUrl)) {
            return imageCache.get(imageUrl);
        }

        if (!isURL(imageUrl)) {
            imageUrl = "https://dummyimage.com/158x184/cccccc/000000&text=Not+Found";
        }

        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);
        if (image == null) {
            image = ImageIO.read(new URL("https://dummyimage.com/158x184/cccccc/000000&text=Not+Found"));
        }

        ImageIcon imageIcon = new ImageIcon(image);
        imageCache.put(imageUrl, imageIcon); // Cache the image
        return imageIcon;
    }

    private boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void showProductDetails(Product product) throws IOException {
        detailPanel.removeAll();
        detailPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel priceLabel = new JLabel("Price: " + product.getPrice());
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JButton closeButton = new JButton("x");
        closeButton.setPreferredSize(new Dimension(35, 30));
        closeButton.setBackground(Color.WHITE);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> closeDetailPanel());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> performDeleteProduct());

        // Add Edit Button
        JButton editButton = new JButton("Edit");
        editButton.setBackground(Color.WHITE);
        editButton.setOpaque(true);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> showEditProductDialog(product));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        headerPanel.add(priceLabel, BorderLayout.EAST);
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        detailPanel.add(headerPanel, BorderLayout.NORTH);
        detailPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Content panel for product details
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel imageLabel = new JLabel(fetchProductImage(product.getImageLink()));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(product.getDescription());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        descriptionArea.setOpaque(false);
        descriptionArea.setMaximumSize(new Dimension(250, 600));
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        JLabel ratingLabel = new JLabel("Rating: " + product.getRating());
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(ratingLabel);
        detailPanel.add(contentPanel, BorderLayout.CENTER);

        detailPanel.revalidate();
        detailPanel.repaint();

        mainSplitPane.setDividerLocation((int) (frame.getWidth() * 0.66));
    }

    private void showEditProductDialog(Product product) {
        JTextField nameField = new JTextField(product.getName(), 20);
        JTextField priceField = new JTextField(product.getPrice(), 20);
        JTextField descriptionField = new JTextField(product.getDescription(), 20);
        JTextField categoryField = new JTextField(product.getCategory(), 20);
        JTextField ratingField = new JTextField(String.valueOf(product.getRating()), 20);
        JTextField brandField = new JTextField(product.getBrand(), 20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createLabelAndField("Name:", nameField));
        panel.add(createLabelAndField("Price:", priceField));
        panel.add(createLabelAndField("Description:", descriptionField));
        panel.add(createLabelAndField("Category:", categoryField));
        panel.add(createLabelAndField("Rating:", ratingField));
        panel.add(createLabelAndField("Brand:", brandField));

        while (true) {
            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Product", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) {
                return; // Cancelled, exit the method
            }

            // Trimmed input values
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = categoryField.getText().trim();
            String ratingText = ratingField.getText().trim();
            String brand = brandField.getText().trim();

            // Validation checks
            if (name.isEmpty() || priceText.isEmpty() || category.isEmpty() || brand.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name, Description, Category, and Brand cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid price. Please enter a valid positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            double rating;
            try {
                rating = Double.parseDouble(ratingText);
                if (rating < 0 || rating > 5) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid rating. Please enter a number between 0 and 5.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try {
                // Create a new Product object with validated fields
                Product updatedProduct = new Product(
                        product.getId(),
                        brand,
                        name,
                        priceText, // Ensure correct data type in your Product constructor
                        product.getImageLink(),
                        description,
                        category,
                        product.getProductType(),
                        rating
                );

                // Update the product in ProductManager
                boolean success = productManager.updateProduct(product.getId(), updatedProduct);
                if (success) {
                    // Refresh UI
                    showProductDetails(updatedProduct);
                    loadProducts(productManager.getAllProducts());
                    return; // Exit the method if successful
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "An error occurred while updating the product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private JPanel createLabelAndField(String labelText, JTextField textField) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.add(new JLabel(labelText));
        rowPanel.add(textField);
        return rowPanel;
    }


    private void closeDetailPanel() {
        detailPanel.removeAll();
        detailPanel.revalidate();
        detailPanel.repaint();
        mainSplitPane.setDividerLocation(frame.getWidth());
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        List<Product> results;

        // Get current filter values
        double minPrice = getMinPriceFromDropdown(priceDropdown.getSelectedItem().toString());
        double maxPrice = getMaxPriceFromDropdown(priceDropdown.getSelectedItem().toString());
        int minRating = sliderRating.getValue();
        String selectedCategory = categoryDropdown.getSelectedItem().toString();

        if (query.isEmpty()) {
            // If no search query, apply only filters
            results = productManager.filterProducts(productManager.getAllProducts(), selectedCategory, minPrice, maxPrice, minRating);
        } else {
            // If there's a search query, combine it with filters
            results = productManager.searchProducts(query); // Get search results
            results = productManager.filterProducts(results, selectedCategory, minPrice, maxPrice, minRating); // Apply filters to search results
        }

        loadProducts(results); // Load the combined results
    }

    private double getMinPriceFromDropdown(String priceRange) {
        switch (priceRange) {
            case "$0-10":
                return 0.0;
            case "$10-20":
                return 10.0;
            case "$20-30":
                return 20.0;
            case "$30-40":
                return 30.0;
            case "$40+":
                return 40.0;
            default:
                return 0.0; // "All"
        }
    }

    private double getMaxPriceFromDropdown(String priceRange) {
        switch (priceRange) {
            case "$0-10":
                return 10.0;
            case "$10-20":
                return 20.0;
            case "$20-30":
                return 30.0;
            case "$30-40":
                return 40.0;
            case "$40+":
                return 100.0; // Arbitrary large value
            default:
                return 100.0; // "All"
        }
    }



    private void showAddProductDialog() {
        JTextField URLField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField ratingField = new JTextField();

        String[] categories = {"All", "lipstick", "liquid", "powder", "palette", "pencil", "cream", "mineral", "lip_stain"};
        JComboBox<String> categoryField = new JComboBox<>(categories);

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("WEBP Image URL:"));
        panel.add(URLField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Product Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (nameField.getText().trim().isEmpty() ||brandField.getText().trim().isEmpty()|| priceField.getText().trim().isEmpty() || (String) categoryField.getSelectedItem()== null || descriptionField.getText().trim().isEmpty() || typeField.getText().trim().isEmpty() || ratingField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Name, Price, Brand, and Category are required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Parse numeric fields
                double price = Double.parseDouble(priceField.getText().trim());
                double rating = Double.parseDouble(ratingField.getText().trim());

                if (price <= 0) {
                    JOptionPane.showMessageDialog(frame, "Price must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate rating
                if (rating < 0 || rating > 5) {
                    JOptionPane.showMessageDialog(frame, "Rating must be between 0 and 5.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                //check for duplicates
                if (productManager.productExists(nameField.getText().trim(), brandField.getText().trim())) {
                    JOptionPane.showMessageDialog(frame, "A product with this Name and Brand already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Generate a new ID
                int newId = generateNewId();

                // Create the new product
                Product newProduct = new Product(
                        newId,
                        brandField.getText().trim(),
                        nameField.getText().trim(),
                        priceField.getText().trim(),
                        URLField.getText().trim(),
                        descriptionField.getText().trim(),
                        (String)categoryField.getSelectedItem(),
                        typeField.getText().trim(),
                        rating
                );

                // Add the product to the ProductManager
                productManager.addProduct(newProduct);

                // Clear the fields
                URLField.setText("");
                brandField.setText("");
                nameField.setText("");
                priceField.setText("");
                descriptionField.setText("");
                categoryField.setSelectedIndex(0);
                typeField.setText("");
                ratingField.setText("");

                // Refresh the product list
                loadProducts(productManager.getAllProducts());

                // Show success message
                JOptionPane.showMessageDialog(frame, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid numeric value for Price or Rating.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method to generate a new ID
    private int generateNewId() {
        int maxId = 0;
        for (Product product : productManager.getAllProducts()) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        return maxId + 1;
    }

    private void performDeleteProduct() {
        if (selectedProduct != null) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete " + selectedProduct.getName() + "?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                productManager.deleteProduct(selectedProduct.getId());
                loadProducts(productManager.getAllProducts());
                closeDetailPanel();
                selectedProduct = null;
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a product to delete by clicking on it.",
                    "No Product Selected", JOptionPane.WARNING_MESSAGE);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(BeautyProductCatalogInterface::new);
    }
}