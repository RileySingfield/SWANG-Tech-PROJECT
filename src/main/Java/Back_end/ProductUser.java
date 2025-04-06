package Back_end;


import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.util.*;

/**
 * Represents a user with a wishlist of products.
 */
public class ProductUser {
    private static final String FILE_PATH = "src/main/resources/wishlist.csv";
    private String username;
    private String password;
    private List<Product> wishlist;
    /**
     * Constructs a user with the specified username and password.
     */
    public ProductUser(String username, String password) {
        this.username = username;
        this.password= password;
        loadWishlistFromCSV();
    }
    /**
     * Gets the username of the user.
     */
    public String getUsername() {
        return username;
    }
    /**
     * Gets the password of the user.
     */
    public String getPassword() {
        return password;
    }
    /**
     * Gets the wishlist of products for the user.
     */
    public List<Product> getWishlist() {
        return wishlist;
    }
    /**
     * Adds a product to the wishlist if it's not already present.
     *
     * @param product The product to add.
     * @return true if added, false if it was already in the wishlist.
     */
    public boolean addToWishlist(Product product) {
        if(wishlist!=null) {
            if(!wishlist.contains(product)) {
                int id = product.getId();
                String idString = Integer.toString(id);
                wishlist.add(product);
                return addToCSV(username, idString);
            }
        }else{
            wishlist.add(product);
            int id= product.getId();
            String idString= Integer.toString(id);
            return addToCSV(username,idString);
        }
        System.out.println("couldn't add to wishlist");

        //if already in wishlist
        return false;
    }
    /**
     * Removes a product from the wishlist.
     *
     * @param product The product to remove.
     * @return true if removed, false if it was not found.
     */
    public boolean removeFromWishlist(Product product) {
        if (wishlist.contains(product)) {
            wishlist.remove(product);
            int id = product.getId();
            String idString = Integer.toString(id);
            System.out.println("Removed product from wishlist");
            return removeFromCSV(username,idString);
        }
        return false;
    }
    /**
     * Loads the wishlist from the CSV file and returns a map of username to their product wishlist.
     *
     * @return Map of username -> List of Products
     */
    public void loadWishlistFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String user = parts[0].trim();
                    System.out.println(user);
                    String productId = parts[1].trim();
                    System.out.println(productId);
                    int id = 0;
                    try{
                        id = Integer.parseInt(productId);
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }

                    ProductManager productManager = new ProductManager();
                    Product product = productManager.getProductById(id);
                    wishlist =new ArrayList<>();
                    if(product!=null&&user.equals(username)) {
                        wishlist.add(product);
                        System.out.println("adding product to wishlist hashmap");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean addToCSV(String username, String productId) {
        System.out.println("adding to csv");
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print("\n"+username + "," + productId);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("added to csv");
        return true;
    }
    private boolean removeFromCSV(String username, String productId) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File("wishlist_temp.csv");
        System.out.println("removing from csv");
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String lineToRemove = username + "," + productId;
            String currentLine;
            System.out.println(lineToRemove+"being removed");
            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.equals(lineToRemove)) {
                    writer.println(currentLine);
                }
            }
            System.out.println("success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Replace old file with new one
        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
        return true;
    }
}

