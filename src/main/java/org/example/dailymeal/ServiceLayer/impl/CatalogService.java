package org.example.dailymeal.ServiceLayer.impl;

import jakarta.xml.bind.JAXBException;
import org.example.dailymeal.DataAccessLayer.Category;
import org.example.dailymeal.DataAccessLayer.Db;
import org.example.dailymeal.DataAccessLayer.Product;
import org.example.dailymeal.ParserXmlToJavaObjects;
import org.example.dailymeal.ServiceLayer.ICatalogService;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogService implements ICatalogService {
    private Db db;
    private final String xmlFilePath = "C:\\Users\\User\\Desktop\\DailyMeal\\src\\main\\resources\\FoodProducts.xml";

    public CatalogService() {
        try {
            loadDate(xmlFilePath);
        } catch (JAXBException | FileNotFoundException e) {
            System.err.println("Ошибка загрузки данных: " + e.getMessage());
            this.db = new Db();
            this.db.setCategories(new ArrayList<>());
        }
    }

    private void loadDate(String pathname) throws JAXBException, FileNotFoundException {
        this.db = ParserXmlToJavaObjects.ParseXmlToJavaObject(pathname);
    }

    public void setDb(Db db) {
        this.db = db;
    }

    public Db getDb() {
        return db;
    }

    @Override
    public List<Category> getAllCategories() {
        if (db == null || db.getCategories() == null) {
            return new ArrayList<>();
        }
        return db.getCategories();
    }

    @Override
    public List<Product> getAllProducts() {
        if (db == null || db.getCategories() == null) {
            return new ArrayList<>();
        }
        return db.getCategories().stream()
                .flatMap(c -> c.getProducts().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        if (db == null || db.getCategories() == null || name == null) {
            return new ArrayList<>();
        }
        return db.getCategories().stream()
                .flatMap(c -> c.getProducts().stream())
                .filter(p -> p.getName() != null &&
                        p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategoryByName(String name) {
        if (db == null || db.getCategories() == null || name == null) {
            return null;
        }
        return db.getCategories().stream()
                .filter(c -> c.getName() != null && c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addCategory(Category category) {
        if (db != null && category != null) {
            if (db.getCategories() == null) {
                db.setCategories(new ArrayList<>());
            }
            db.getCategories().add(category);
        }
    }

    @Override
    public void removeCategory(Category category) {
        if (db != null && db.getCategories() != null && category != null) {
            db.getCategories().remove(category);
        }
    }

    @Override
    public void addProduct(Product product, Category category) {
        if (category != null && product != null) {
            if (category.getProducts() == null) {
                category.setProducts(new ArrayList<>());
            }
            category.getProducts().add(product);
        }
    }

    @Override
    public void removeProduct(Product product, Category category) {
        if (category != null && category.getProducts() != null && product != null) {
            category.getProducts().remove(product);
        }
    }

    @Override
    public void updateProduct(Product product) {
        if (db != null && db.getCategories() != null && product != null && product.getName() != null) {
            db.getCategories().stream()
                    .filter(cat -> cat.getProducts() != null)
                    .forEach(cat -> {
                        cat.getProducts().replaceAll(p ->
                                (p.getName() != null && p.getName().equals(product.getName())) ? product : p
                        );
                    });
        }
    }
}