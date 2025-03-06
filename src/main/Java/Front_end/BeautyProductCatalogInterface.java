package Front_end;
import Back_end.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.net.URL;

public class BeautyProductCatalogInterface {
    private JFrame frame;
    private JPanel catalogPanel;
    private JPanel detailPanel;
    private JSplitPane mainSplitPane;
    private ProductManager productManager;
    private Product selectedProduct = null; // currently selected product
    private JTextField searchField; // top panel search field

    public BeautyProductCatalogInterface() {
        productManager = new ProductManager();
        initializeUI();
    }

    private void initializeUI() {
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

        //FILTER
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
        JComboBox<String> priceDropdown = new JComboBox<>(new String[]{"All","$0-10", "$10-20", "$20-30", "$30-40", "$40+"});
        priceDropdown.setPreferredSize(new Dimension(200, 25)); // Set a proper size for the dropdown
        JPanel priceDropdownPanel = new JPanel();
        priceDropdownPanel.setBackground(Color.WHITE);
        priceDropdownPanel.setLayout(new BoxLayout(priceDropdownPanel, BoxLayout.X_AXIS));
        priceDropdownPanel.add(priceLabel);
        priceDropdownPanel.add(Box.createHorizontalStrut(10)); // Add spacing between label and dropdown
        priceDropdownPanel.add(priceDropdown);
        filterPanel.add(priceDropdownPanel);
        filterPanel.add(Box.createVerticalStrut(10)); // Add spacing after price filter

        // Rating filter
        JLabel ratingLabel = new JLabel("Minimum Rating:");
        JSlider sliderRating = new JSlider();
        sliderRating.setMaximum(5);
        sliderRating.setMinimum(0);
        sliderRating.setPreferredSize(new Dimension(200, 50)); // Adjust slider size
        sliderRating.setBackground(Color.WHITE);
        sliderRating.setOpaque(true);
        JPanel ratingPanel = new JPanel();
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.X_AXIS));
        ratingPanel.add(ratingLabel);
        ratingPanel.add(Box.createHorizontalStrut(10)); // Add spacing
        ratingPanel.add(sliderRating);
        filterPanel.add(ratingPanel);
        filterPanel.add(Box.createVerticalStrut(10)); // Add spacing after rating filter

        // Category filter
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryDropdown = new JComboBox<>(new String[]{"All", "lipstick", "liquid", "powder", "palette", "pencil", "cream", "mineral", "lip_stain"});
        categoryDropdown.setPreferredSize(new Dimension(200, 25)); // Set preferred size
        JButton applyCategoryFilter = new JButton("Apply");
        JPanel categoryPanel = new JPanel();
        categoryPanel.setBackground(Color.WHITE);
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
        categoryPanel.add(categoryLabel);
        categoryPanel.add(Box.createHorizontalStrut(10)); // Add spacing between label and dropdown
        categoryPanel.add(categoryDropdown);
        categoryPanel.add(Box.createHorizontalStrut(10)); // Add spacing between dropdown and button
        categoryPanel.add(applyCategoryFilter);
        filterPanel.add(categoryPanel);

        applyCategoryFilter.addActionListener(e -> {

            if(applyCategoryFilter.isEnabled()) {
                double minPrice;
                double maxPrice;
                if(priceDropdown.getSelectedItem().equals("$0-10")) {
                    minPrice = 0.0;
                    maxPrice = 10.0;
                }else if(priceDropdown.getSelectedItem().equals("$10-20")) {
                    minPrice = 10.0;
                    maxPrice = 20.0;
                }else if(priceDropdown.getSelectedItem().equals("$20-30")) {
                    minPrice = 20.0;
                    maxPrice = 30.0;
                }else if(priceDropdown.getSelectedItem().equals("$30-40")) {
                    minPrice = 30.0;
                    maxPrice = 40.0;
                }else if(priceDropdown.getSelectedItem().equals("$40")) {
                    minPrice = 40.0;
                    maxPrice = 100.0;
                }else{
                    minPrice = 0.0;
                    maxPrice = 100.0;
                }
                loadProducts(productManager.filterProducts(categoryDropdown.getSelectedItem().toString(),minPrice,maxPrice,sliderRating.getValue()));
            }
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

        JSplitPane filterSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,filterPanel,catalogScrollPane);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterSplitPane, detailScrollPane);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setDividerLocation(frame.getWidth());
        mainSplitPane.setEnabled(false);


        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplitPane, BorderLayout.CENTER);
        frame.setVisible(true);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                performSearch();
                searchField.setText("");
                removeSearchButton.setVisible(true); // this was hidden before
            }
        });
        removeSearchButton.addActionListener(e -> {
            searchField.setText("");
            removeSearchButton.setVisible(false);
            loadProducts(productManager.getAllProducts());
        });
        addButton.addActionListener(e -> showAddProductDialog());
    }

    private void loadProducts(List<Product> products) {
        catalogPanel.removeAll();
        for (Product product : products) {
            JPanel productCard = new JPanel(new BorderLayout());
            productCard.setBackground(Color.WHITE);
            productCard.setBorder(BorderFactory.createLineBorder(Color.WHITE));

            JLabel imageLabel = new JLabel();
            ImageIcon productImage;
            try {
                productImage = fetchProductImage(product.getImageLink());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (productImage != null) {
                imageLabel.setIcon(productImage);
            } else {
                imageLabel.setText("No Image Available");
            }
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedProduct = product;
                    try {
                        showProductDetails(product);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });


            JButton detailsButton = new JButton(product.getName());
            detailsButton.setPreferredSize(new Dimension(100, 30));
            detailsButton.setBackground(new Color(255, 255, 255));
            detailsButton.setOpaque(true);
            detailsButton.setBorderPainted(false);
            detailsButton.addActionListener(e -> {
                selectedProduct = product;
                try {
                    showProductDetails(product);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            productCard.add(imageLabel, BorderLayout.CENTER);
            productCard.add(detailsButton, BorderLayout.SOUTH);

            catalogPanel.add(productCard);
        }
        catalogPanel.revalidate();
        catalogPanel.repaint();
    }
    public static boolean isURL(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) { }
        return false;
    }

    public static ImageIcon fetchProductImage(String webpUrlString) throws IOException {
        // Open the WebP image from URL
        if(!isURL(webpUrlString)){
            webpUrlString="https://dummyimage.com/158x184/cccccc/000000&text=Not+Found";
        }
        URL webpUrl = new URL(webpUrlString);
        BufferedImage webpImage = ImageIO.read(webpUrl);
        if (webpImage == null) {
            webpImage = ImageIO.read(new URL("https://dummyimage.com/158x184/cccccc/000000&text=Not+Found"));
        }

        // initiate JPEG writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPEG writers available");
        }
        ImageWriter writer = writers.next();

        // JPEG to a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        // Compression quality to maximum
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(1.0f);
        }

        // Convert the WebP image to JPEG in memory
        writer.write(null, new IIOImage(webpImage, null, null), param);
        ios.close();
        writer.dispose();

        // Read the JPEG image back from the byte array
        byte[] jpegData = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(jpegData);
        BufferedImage jpegImage = ImageIO.read(bais);

        // Return as an ImageIcon
        return new ImageIcon(jpegImage);
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

        headerPanel.add(priceLabel, BorderLayout.EAST);
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        bottomPanel.add(closeButton, BorderLayout.EAST);
        bottomPanel.add(deleteButton, BorderLayout.WEST);
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

    private void closeDetailPanel() {
        detailPanel.removeAll();
        detailPanel.revalidate();
        detailPanel.repaint();
        mainSplitPane.setDividerLocation(frame.getWidth());
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        List<Product> results;
        if (query.isEmpty()) {
            results = productManager.getAllProducts();
        } else {
            results = productManager.searchProducts(query);
        }
        loadProducts(results);
    }

    private void showAddProductDialog() {
        JTextField URLField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField ratingField = new JTextField();

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
            int newId = productManager.getAllProducts().size() + 1;
            try {
                double rating = Double.parseDouble(ratingField.getText().trim());
                Product newProduct = new Product(newId, brandField.getText().trim(), nameField.getText().trim(),
                        priceField.getText().trim(), URLField.getText().trim(), descriptionField.getText().trim(), categoryField.getText().trim(), typeField.getText().trim(), rating);
                productManager.addProduct(newProduct);
                URLField.setText("");
                brandField.setText("");
                nameField.setText("");
                priceField.setText("");
                descriptionField.setText("");
                categoryField.setText("");
                typeField.setText("");
                ratingField.setText("");
                loadProducts(productManager.getAllProducts());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid rating value.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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