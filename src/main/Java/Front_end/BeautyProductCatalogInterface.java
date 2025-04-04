package Front_end;
import Back_end.*;
import Back_end.Product;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.image.BufferedImage;
import java.net.URL;

import static java.lang.String.format;

public class BeautyProductCatalogInterface {
    private JFrame frame;
    private JPanel catalogPanel;
    private JPanel detailPanel;
    private JSplitPane mainSplitPane;
    private final ProductManager productManager;
    private Product selectedProduct = null; // currently selected product
    private JTextField searchField; // top panel search field
    private final Map<String, ImageIcon> imageCache = new HashMap<>(); // Cache for images
    private final ExecutorService imageLoaderExecutor = Executors.newFixedThreadPool(4);
    private final UserManager userManager = new UserManager();
    private final String[] categories = {"All", "lipstick", "liquid", "powder", "palette", "pencil", "cream", "mineral", "lip_stain"};

    boolean wishlist = false;
    // Filter components as instance variables
    private JComboBox<String> priceDropdown;
    private JSlider sliderRating;
    private JComboBox<String> categoryDropdown;

    /**
     *
     *
     */
    public BeautyProductCatalogInterface() {
        productManager = new ProductManager();
        initializeUI();
    }


    /**
     * This method sets the look and feel and initializes the frame
     *
     */
    private void initializeUI() {
        // Enable hardware acceleration
        System.setProperty("sun.java2d.opengl", "true");

        // Set Nimbus look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        frame = new JFrame("Product Catalog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 800);
        frame.getContentPane().setBackground(new Color(245, 245, 245));

        loginScreen();
        //catalogScreen();
    }

    /**
     * This method loads products and places them in the parameterized
     * panel. It loads the label first and then the image, calling
     * fetchProductImage with a URL.
     *
     * @param products
     * @param panel
     */
    private void loadProducts(List<Product> products,JPanel panel) {
        panel.removeAll(); // Clear panel efficiently
        System.out.println("new screen");
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
            detailsButton.setSize(100, 30);
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
            productCard.setSize(new Dimension(200, 200));
            productCard.add(imageLabel, BorderLayout.CENTER);
            productCard.add(detailsButton, BorderLayout.SOUTH);
            panel.add(productCard);
        }

        panel.revalidate();
        panel.repaint();
    }

    /**
     * When passed a url (read from csv file), this method converts the
     * URL into an ImageIcon, which is then returned to loadProducts.
     *
     * @param imageUrl
     * @return
     * @throws IOException
     */
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

    /**
     * This method checks that the URL found in the csv file is, in
     * fact, a proper, working URL. It returns a boolean value true
     * if it is a URL, and false if it isn't.
     *
     * @param url
     * @return
     */
    private boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * This product adds a panel to the split screen that shows details
     * about the selected product. this is then displayed on the frame.
     *
     * @param product
     * @throws IOException
     */
    private void showProductDetails(Product product) throws IOException {
        detailPanel.removeAll();
        detailPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        String priceS;
        try{
            float price = Float.parseFloat(product.getPrice());
            priceS = format("%.2f",price);
        }catch (NumberFormatException e){
            priceS = product.getPrice();
        }

        JLabel priceLabel = new JLabel("Price: $" + priceS);
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(65, 30));
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

        JButton wishlistButton = new JButton("Add to Wishlist");
        wishlistButton.setBackground(Color.WHITE);
        wishlistButton.setOpaque(true);
        wishlistButton.setBorderPainted(false);

        JButton removeButton = new JButton("Remove from Wishlist");
        removeButton.setBackground(Color.WHITE);
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);

        if(userManager.getLoggedInUser()!=null){
            if(userManager.getLoggedInUser().getWishlist()!=null){
                if(userManager.getLoggedInUser().getWishlist().contains(product)) {
                    buttonPanel.add(removeButton);
                }else {
                    buttonPanel.add(wishlistButton);
                }
            }else {
                buttonPanel.add(wishlistButton);
            }
        }
        removeButton.addActionListener(e -> {
            userManager.getLoggedInUser().removeFromWishlist(product);
            buttonPanel.remove(removeButton);
            buttonPanel.add(wishlistButton);
            frame.revalidate();
            frame.repaint();
        });
        wishlistButton.addActionListener(e -> {
            userManager.getLoggedInUser().addToWishlist(product);
            buttonPanel.remove(wishlistButton);
            buttonPanel.add(removeButton);
            frame.revalidate();
            frame.repaint();
        });



        headerPanel.add(nameLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.add(priceLabel, BorderLayout.WEST);
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
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(150, 400));

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

    /**
     * When a user chooses to edit a product in the details panel,
     * this screen will pop up and allow for the user to change details about the product.
     * @param product
     */
    private void showEditProductDialog(Product product) {
        JTextField nameField = new JTextField(product.getName(), 20);
        JTextField priceField = new JTextField(product.getPrice(), 20);
        JTextField descriptionField = new JTextField(product.getDescription(), 20);
        JComboBox categoryBox = new JComboBox<>(categories);
        JTextField ratingField = new JTextField(String.valueOf(product.getRating()), 20);
        JTextField brandField = new JTextField(product.getBrand(), 20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createLabelAndField("Name:", nameField));
        panel.add(createLabelAndField("Price:", priceField));
        panel.add(createLabelAndField("Description:", descriptionField));
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
        categoryPanel.add(new JLabel("Category:"));

        categoryBox.setSelectedItem(product.getCategory());
        categoryPanel.add(categoryBox);
        panel.add(categoryPanel);
        panel.add(createLabelAndField("Rating:", ratingField));
        panel.add(createLabelAndField("Brand:", brandField));
        //TODO add edit image option

        while (true) {
            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Product", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) {
                return; // Cancelled, exit the method
            }

            // Trimmed input values
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = categories[categoryBox.getSelectedIndex()];
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
                    loadProducts(productManager.getAllProducts(),catalogPanel);
                    return; // Exit the method if successful
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "An error occurred while updating the product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * helper method to create and pair label and text field.
     *
     * @param labelText
     * @param textField
     * @return
     */
    private JPanel createLabelAndField(String labelText, JTextField textField) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowPanel.add(new JLabel(labelText));
        rowPanel.add(textField);
        return rowPanel;
    }

    /**
     * button to close the detail panel
     */
    private void closeDetailPanel() {
        detailPanel.removeAll();
        detailPanel.revalidate();
        detailPanel.repaint();
        mainSplitPane.setDividerLocation(frame.getWidth());
    }

    /**
     * use searchField to filter items in the display. then calls loadProducts to display them.
     */
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

        loadProducts(results,catalogPanel); // Load the combined results
    }

    /**
     * gets minimum price from values in choice box and returns it
     * @param priceRange
     * @return
     */
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

    /**
     * gets maximum price from values in choice box and returns it
     * @param priceRange
     * @return double
     */
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

    private FileFilter webpFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().endsWith(".webp");
        }

        @Override
        public String getDescription() {
            return "";
        }
    };
    private void showAddProductDialog() {
        JTextField brandField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField descriptionField = new JTextField();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(webpFilter);
        JTextField typeField = new JTextField();
        JTextField ratingField = new JTextField();

        JComboBox<String> categoryField = new JComboBox<>(categories);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("WEBP Image URL:"));
        panel.add(fileChooser);
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

                //generate URL for picture
                URL img = fileChooser.getSelectedFile().toURI().toURL();
                String newURL = img.toString();

                // Create the new product
                Product newProduct = new Product(
                        newId,
                        brandField.getText().trim(),
                        nameField.getText().trim(),
                        priceField.getText().trim(),
                        newURL,
                        descriptionField.getText().trim(),
                        (String)categoryField.getSelectedItem(),
                        typeField.getText().trim(),
                        rating
                );

                // Add the product to the ProductManager
                productManager.addProduct(newProduct);

                // Clear the fields
                brandField.setText("");
                nameField.setText("");
                priceField.setText("");
                descriptionField.setText("");
                categoryField.setSelectedIndex(0);
                typeField.setText("");
                ratingField.setText("");

                // Refresh the product list
                loadProducts(productManager.getAllProducts(),catalogPanel);

                // Show success message
                JOptionPane.showMessageDialog(frame, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid numeric value for Price or Rating.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * when a new product is added, a new id is created here.
     * @return int
     */
    private int generateNewId() {
        int maxId = 0;
        for (Product product : productManager.getAllProducts()) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        return maxId + 1;
    }

    /**
     * confirms the user would like to remove the product and then removes it.
     */
    private void performDeleteProduct() {
        if (selectedProduct != null) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete " + selectedProduct.getName() + "?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                productManager.deleteProduct(selectedProduct.getId());
                loadProducts(productManager.getAllProducts(),catalogPanel);
                closeDetailPanel();
                selectedProduct = null;
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a product to delete by clicking on it.",
                    "No Product Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * this is the main display of the program. shows all products, filter and search panels.
     */
    public void catalogScreen(){
        System.out.println("Catalog Screen");
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

        //WISHLIST BUTTON
        JButton wishlist =  new JButton("Wishlist");
        wishlist.setBackground(Color.WHITE);
        wishlist.setOpaque(true);
        wishlist.setBorderPainted(false);
        if(userManager.getLoggedInUser()!=null){
            //todo display button
            if(userManager.getLoggedInUser().getWishlist()!=null){
                topPanel.add(wishlist);
            }
        }
        //show username and status
        if(userManager.getLoggedInUser()!=null){
            String user = userManager.getLoggedInUser().getUsername();
            JLabel usernameLabel = new JLabel(user);
            if(!user.equals("guest")){
                topPanel.add(usernameLabel);
            }
        }
        JButton logoutButton = new JButton("Logout");
        topPanel.add(logoutButton);

        topPanel.add(searchPanel);
        topPanel.add(searchButton);
        topPanel.add(addButton);


        // FILTER PANEL
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
        priceDropdown.setPreferredSize(new Dimension(200, 1)); // this line is useless from what I can tell
        // below line controls the size of the price dropdown button
        priceDropdown.setMaximumSize(new Dimension(100, 40));

        JPanel priceDropdownPanel = new JPanel();
        priceDropdownPanel.setBackground(Color.WHITE);
        priceDropdownPanel.setLayout(new BoxLayout(priceDropdownPanel, BoxLayout.X_AXIS));
        priceDropdownPanel.add(priceLabel);
        priceDropdownPanel.add(Box.createHorizontalStrut(5));
        priceDropdownPanel.add(priceDropdown);
        filterPanel.add(priceDropdownPanel);
        filterPanel.add(Box.createVerticalStrut(5));

        // Category filter
        JLabel categoryLabel = new JLabel("Category:");
        categoryDropdown = new JComboBox<>(new String[]{"All", "lipstick", "liquid", "powder", "palette", "pencil", "cream", "mineral", "lip_stain"});
        categoryDropdown.setPreferredSize(new Dimension(120, 25)); //this line is actually useless from what I can tell
        // below line actually changes the size of the category drop down button
        categoryDropdown.setMaximumSize(new Dimension(100, 40));

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
            loadProducts(productManager.getAllProducts(),catalogPanel);
        });

        filterPanel.add(categoryPanel);
        filterPanel.add(Box.createVerticalStrut(10));

// Create a new panel to hold Apply and Clear Filters buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);

// Add buttons to the new panel
        buttonPanel.add(clearFiltersButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Space between buttons
        buttonPanel.add(applyCategoryFilter);

// Add the button panel to the filter panel
        filterPanel.add(buttonPanel);


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

            loadProducts(results,catalogPanel); // Load the combined results
        });



        catalogPanel = new JPanel();
        catalogPanel.setBackground(Color.WHITE);
        catalogPanel.setLayout(new GridLayout(0, 3, 10, 10));
        loadProducts(productManager.getAllProducts(),catalogPanel);
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

        wishlist.addActionListener(e->{
            if(userManager.getLoggedInUser().getWishlist()!=null) {
                frame.remove(topPanel);
                frame.remove(mainSplitPane);
                wishListScreen();
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

            loadProducts(productManager.filterProducts(productManager.getAllProducts(), selectedCategory, minPrice, maxPrice, minRating),catalogPanel);
        });

        // Add Button Action Listener
        addButton.addActionListener(e -> showAddProductDialog());

        logoutButton.addActionListener(e -> {
            userManager.logout();
            frame.remove(catalogPanel);
            frame.remove(mainSplitPane);
            frame.remove(topPanel);
            loginScreen();
        });
    }

    /**
     * shows products that have been added to a users wishlist in a seperate panel to the catalog screen.
     */
    public void wishListScreen() {
        wishlist = true;
        JPanel wishListPanel = new JPanel();
        wishListPanel.setLayout(new BorderLayout(5,5));
        wishListPanel.setBackground(Color.WHITE);
        String name = userManager.getLoggedInUser().getUsername();
        JLabel wishListLabel = new JLabel(name+"'s Wishlist:");
        wishListLabel.setFont(new Font("Tahoma", Font.PLAIN, 112));
        JButton backButton = new JButton("Back");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(wishListLabel,BorderLayout.CENTER);
        topPanel.add(backButton,BorderLayout.WEST);
        wishListPanel.add(topPanel,BorderLayout.PAGE_START);
        JPanel showItemsPanel = new JPanel();
        showItemsPanel.setBackground(Color.WHITE);
        loadProducts(userManager.getLoggedInUser().getWishlist(),showItemsPanel);
        JScrollPane wishListScrollPane = new JScrollPane(showItemsPanel);
        wishListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wishListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        wishListScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        wishListScrollPane.setPreferredSize(new Dimension(150, 400));
        wishListPanel.add(wishListScrollPane,BorderLayout.CENTER);
        wishListPanel.setVisible(true);
        JPanel splitPanel = new JPanel();
        splitPanel.setBackground(Color.WHITE);
        splitPanel.add(wishListPanel);
        splitPanel.add(detailPanel);
        frame.add(splitPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        backButton.addActionListener(e -> {
            frame.remove(splitPanel);
            wishlist = false;
            catalogScreen();
        });
        frame.revalidate();
        frame.repaint();
    }

    /**
     * shows login fields and allows a user to log in
     */
    public void loginScreen(){
        //TODO add option to use as guest
        JPanel loginPanel = new JPanel();
        loginPanel.requestFocus();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        loginPanel.setBackground(Color.WHITE);
        JLabel loginLabel = new JLabel("Login");
        c.gridx = 1;
        c.gridy = 0;
        loginPanel.add(loginLabel, c);
        JLabel userLabel = new JLabel("Username: ");
        c.gridx=0;
        c.gridy =1;
        loginPanel.add(userLabel, c);
        JTextField usernameField = new JTextField(20);
        usernameField.setMinimumSize(new Dimension(120, 25));
        usernameField.setEditable(true);
        c.gridx=1;
        loginPanel.add(usernameField, c);
        JLabel passwordLabel = new JLabel("Password: ");
        c.gridx=0;
        c.gridy =2;
        loginPanel.add(passwordLabel, c);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEditable(true);
        passwordField.setMinimumSize(new Dimension(120, 25));
        c.gridx=1;
        c.gridwidth = 120;
        loginPanel.add(passwordField, c);
        JButton loginButton = new JButton("Login");
        c.gridx=1;
        c.gridy =3;
        loginPanel.add(loginButton, c);


        //warning message
        JLabel warningEmpty = new JLabel("Please enter valid credentials.");
        warningEmpty.setForeground(Color.RED);
        warningEmpty.setVisible(false);
        c.gridx=1;
        c.gridy =4;
        JLabel warningWrong = new JLabel("Username and password do not match.");
        warningWrong.setForeground(Color.RED);
        warningWrong.setVisible(false);
        c.gridx=1;
        c.gridy =4;
        loginPanel.add(warningEmpty, c);
        loginPanel.add(warningWrong, c);

        //TEMP
        JButton override = new JButton("Continue as Guest");
        c.gridx=1;
        c.gridy =5;
        loginPanel.add(override, c);

        JButton registerButton = new JButton("Make an account");
        c.gridx=1;
        c.gridy =6;
        loginPanel.add(registerButton, c);


        override.addActionListener(e -> {
            //use the general account
            userManager.login("guest","0");
            loginPanel.setVisible(false);
            frame.remove(loginPanel);
            catalogScreen();
        });

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            char[] passwordChar = passwordField.getPassword();
            String password = new String(passwordChar);
            System.out.println(password);
            if (username.equals("") || password.equals("")) {
                warningEmpty.setVisible(true);
                warningWrong.setVisible(false);
            }else{
                boolean success = userManager.login(username, password);
                if(!success){
                    warningWrong.setVisible(true);
                    warningEmpty.setVisible(false);
                }else{
                    loginPanel.setVisible(false);
                    usernameField.setText("");
                    passwordField.setText("");
                    warningEmpty.setVisible(false);
                    warningWrong.setVisible(false);
                    catalogScreen();
                }
            }
        });
        registerButton.addActionListener(e -> {
            loginPanel.setVisible(false);
            frame.remove(loginPanel);
            registerScreen();
        });

        loginPanel.setVisible(true);
        frame.add(loginPanel,BorderLayout.CENTER);
        frame.setVisible(true);
    }

    /**
     * shows register screen and allows a user to register/log in
     */
    public void registerScreen(){
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        registerPanel.setBackground(Color.WHITE);
        JLabel loginLabel = new JLabel("Register");
        c.gridx = 1;
        c.gridy = 0;
        registerPanel.add(loginLabel, c);
        JLabel userLabel = new JLabel("Username: ");
        c.gridx=0;
        c.gridy =1;
        registerPanel.add(userLabel, c);
        JTextField usernameField = new JTextField(20);
        usernameField.setMinimumSize(new Dimension(120, 25));
        usernameField.setEditable(true);
        c.gridx=1;
        registerPanel.add(usernameField, c);
        JLabel passwordLabel = new JLabel("Password: ");
        c.gridx=0;
        c.gridy =2;
        registerPanel.add(passwordLabel, c);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setEditable(true);
        passwordField.setMinimumSize(new Dimension(120, 25));
        c.gridx=1;
        registerPanel.add(passwordField, c);
        c.gridx=0;
        c.gridy =3;
        JLabel password2Label = new JLabel("Re-enter Password:");
        registerPanel.add(password2Label, c);
        JPasswordField passwordField2 = new JPasswordField(20);
        passwordField2.setEditable(true);
        passwordField2.setMinimumSize(new Dimension(120, 25));
        c.gridx=1;
        registerPanel.add(passwordField2, c);
        JButton registerButton = new JButton("Register");
        c.gridx=1;
        c.gridy =4;
        registerPanel.add(registerButton, c);


        //warning message
        JLabel warningEmpty = new JLabel("Please fill in all fields.");
        warningEmpty.setForeground(Color.RED);
        warningEmpty.setVisible(false);
        c.gridx=1;
        c.gridy =5;
        JLabel warningWrong = new JLabel("Passwords do not match.");
        warningWrong.setForeground(Color.RED);
        warningWrong.setVisible(false);

        JLabel warningTaken = new JLabel("Username is already taken.");
        warningTaken.setForeground(Color.RED);
        warningTaken.setVisible(false);
        registerPanel.add(warningWrong, c);
        registerPanel.add(warningTaken, c);
        registerPanel.add(warningEmpty, c);

        //TEMP
        JButton override = new JButton("Continue as guest");
        c.gridx=1;
        c.gridy =6;
        registerPanel.add(override, c);

        JButton loginButton = new JButton("Login");
        c.gridx=1;
        c.gridy =7;
        registerPanel.add(loginButton, c);

        override.addActionListener(e -> {
            registerPanel.setVisible(false);
            catalogScreen();
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String password2 = passwordField2.getText().trim();
            if (username.isEmpty() || password.isEmpty()||password2.isEmpty()) {
                warningEmpty.setVisible(true);
                warningWrong.setVisible(false);
                warningTaken.setVisible(false);
            }else if(!password.equals(password2)){
                warningWrong.setVisible(true);
                warningTaken.setVisible(false);
                warningEmpty.setVisible(false);
            }else if(userManager.userExists(username)) {
                warningWrong.setVisible(false);
                warningTaken.setVisible(true);
                warningEmpty.setVisible(false);
            }else{
                userManager.register(username,password);
                registerPanel.setVisible(false);
                catalogScreen();
            }
        });

        loginButton.addActionListener(e -> {
            frame.remove(registerPanel);
            loginScreen();
        });
        registerPanel.setVisible(true);
        frame.add(registerPanel,BorderLayout.CENTER);
        frame.setVisible(true);
    }

    /**
     * invoke GUI!
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BeautyProductCatalogInterface::new);
    }
}