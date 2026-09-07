package org.example.dailymeal.ServiceLayer;

import org.example.dailymeal.DataAccessLayer.Category;
import org.example.dailymeal.DataAccessLayer.Product;

import java.util.List;

public interface ICatalogService {
    List<Category> getAllCategories();
    List<Product> getAllProducts();
    List<Product> searchProductsByName(String name);
    Category getCategoryByName(String name);
    void addCategory(Category category);
    void removeCategory(Category category);
    void addProduct(Product product, Category category);
    void removeProduct(Product product, Category category);
    void updateProduct(Product product);
}
