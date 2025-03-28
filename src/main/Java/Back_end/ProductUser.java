package Back_end;


import java.util.HashMap;
import java.util.Map;

public class ProductUser {
    private String username;
    private String password;
    private Map<Integer, Product> wishlist = new HashMap<>();

    public ProductUser(String username, String password) {
        this.username = username;
        this.password= password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<Integer, Product> getWishlist() {
        return wishlist;
    }

    public boolean addToWishlist(Product product) {
        if (!wishlist.containsKey(product.getId())) {
            wishlist.put(product.getId(), product);
            return true;
        }
        //if already in wishlist
        return false;
    }

    public boolean removeFromWishlist(int productId) {
        if (wishlist.containsKey(productId)) {
            wishlist.remove(productId);
            return true;
        }
        return false;
    }

}

