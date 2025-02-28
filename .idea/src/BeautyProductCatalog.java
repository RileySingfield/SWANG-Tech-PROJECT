import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
public interface BeautyProductCatalog<T> {
    void addProduct(T product);
    boolean removeProduct(String productId);
    T getProduct(String productId);
    List<T> listProducts();
}