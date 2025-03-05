package com.beauty.catalog.Frontend;

import com.beauty.catalog.Backend.Product;
import com.beauty.catalog.Backend.ProductManager;

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

        // Top panel with search, add, delete buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add");
        searchButton.setBackground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        addButton.setBackground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addButton);

        // Catalog panel with white background
        catalogPanel = new JPanel();
        catalogPanel.setBackground(Color.WHITE);
        catalogPanel.setLayout(new GridLayout(0, 3, 10, 10));
        loadProducts(productManager.getAllProducts());
        JScrollPane catalogScrollPane = new JScrollPane(catalogPanel);
        catalogScrollPane.setBorder(null);
        catalogScrollPane.getViewport().setBackground(Color.WHITE);

        // Detail panel with soft background and padding, and make it scrollable.
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane detailScrollPane = new JScrollPane(detailPanel);
        detailScrollPane.setBorder(null);
        detailScrollPane.getViewport().setBackground(new Color(250, 250, 250));

        // Use a split pane for catalog and details.
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, catalogScrollPane, detailScrollPane);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setDividerLocation(frame.getWidth());
        mainSplitPane.setEnabled(false);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainSplitPane, BorderLayout.CENTER);
        frame.setVisible(true);

        // Button actions
        searchButton.addActionListener(e -> performSearch());
        addButton.addActionListener(e -> showAddProductDialog());
    }

    private void loadProducts(List<Product> products) {
        catalogPanel.removeAll();
        for (Product product : products) {
            JPanel productCard = new JPanel(new BorderLayout());
            productCard.setBackground(Color.WHITE);
            productCard.setBorder(BorderFactory.createLineBorder(Color.WHITE));

            // Product image label
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

            // Details button with soft pink color and narrow size.
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

    public static ImageIcon fetchProductImage(String webpUrlString) throws IOException {
        // Open the WebP image from the URL
        URL webpUrl = new URL(webpUrlString);
        BufferedImage webpImage = ImageIO.read(webpUrl);
        if (webpImage == null) {
            throw new IOException("Could not read the WebP image. Ensure a WebP reader is installed.");
        }

        // Get a JPEG writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPEG writers available");
        }
        ImageWriter writer = writers.next();

        // Write the JPEG to a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        // Set the compression quality to maximum
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(1.0f); // Maximum quality
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

        // Header panel with product name and a small close ("X") button in the top right.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

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
        descriptionArea.setOpaque(false);
        descriptionArea.setMaximumSize(new Dimension(350, 100));
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ratingLabel = new JLabel("Rating: " + product.getRating());
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(descriptionArea);
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
        JTextField brandField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField ratingField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int newId = productManager.getAllProducts().size() + 1;
            try {
                double rating = Double.parseDouble(ratingField.getText().trim());
                Product newProduct = new Product(newId, brandField.getText().trim(), nameField.getText().trim(),
                        priceField.getText().trim(), "", "", categoryField.getText().trim(), "", rating);
                productManager.addProduct(newProduct);
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
