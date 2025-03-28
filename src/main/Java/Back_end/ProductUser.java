package Back_end;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductUser {
    private String username;
    private String password;
    private List<Product> wishlist= new ArrayList<Product>();

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

    public List<Product> getWishlist() {
        return wishlist;
    }

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

    public boolean removeFromWishlist(Product product) {
        if (wishlist.contains(product)) {
            wishlist.remove(product);
            return true;
        }
        return false;
    }

}

