import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
class BeautyProductCatalogGUI {
    private BeautyProductCatalogImpl catalog;
    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> productList;

    public BeautyProductCatalogGUI() {
        catalog = new BeautyProductCatalogImpl();
        frame = new JFrame("Beauty Product Catalog");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        productList = new JList<>(listModel);
        frame.add(new JScrollPane(productList), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(5);
        JButton addButton = new JButton("Add Product");

        panel.add(new JLabel("Name: "));
        panel.add(nameField);
        panel.add(new JLabel("Price: "));
        panel.add(priceField);
        panel.add(addButton);
        frame.add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Product name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    BeautyProduct product = new BeautyProduct(String.valueOf(System.currentTimeMillis()), name, price);
                    catalog.addProduct(product);
                    listModel.addElement(product.toString());
                    nameField.setText("");
                    priceField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid price!", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BeautyProductCatalogGUI());
    }
}