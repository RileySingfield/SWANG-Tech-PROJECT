package Back_end;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents a user with a wishlist of products.
 */
public class ProductUser {
    private String username;
    private String password;
    private List<Product> wishlist= new ArrayList<Product>();
    /**
     * Constructs a user with the specified username and password.
     */
    public ProductUser(String username, String password) {
        this.username = username;
        this.password= password;
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
            if (!wishlist.contains(product)) {
                wishlist.add(product);
                return true;
            }
        }else{
            wishlist.add(product);
            return true;
        }

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
            return true;
        }
        return false;
    }

}

