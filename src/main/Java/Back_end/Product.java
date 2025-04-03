package Back_end;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a beauty product with attributes like brand, name, price, description, etc.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    /**
     * Unique identifier for the product.
     */
    private int id;
    /**
     * Brand of the product.
     */
    private String brand;
    /**
     * Name of the product.
     */
    private String name;
    /**
     * Price of the product in string format.
     */
    private String price;
    /**
     * URL link to the product's image.
     */
    @JsonProperty("image_link")
    private String imageLink;
    /**
     * Description of the product.
     */
    private String description;
    /**
     * Product's category (e.g., lipstick, foundation).
     */
    private String category;
    /**
     * Product type (e.g., makeup, skincare).
     */
    @JsonProperty("product_type")
    private String productType;
    /**
     * User rating of the product.
     */
    private Double rating;

    public Product(int id, String brand, String name, String price, String imageLink,
                   String description, String category, String productType, Double rating) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.imageLink = imageLink;
        this.description = description;
        this.category = category;
        this.productType = productType;
        this.rating = rating;
    }

    public Product() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description.replace("\n", " ").replace("\t", " ").trim(); // ✅ Remove newlines & tabs
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", productType='" + productType + '\'' +
                ", rating=" + rating +
                '}';
    }
}


