package org.example.dailymeal.BusinessLayer;

import java.util.List;
import java.util.ArrayList;

public class MealTime extends BusinessObject {
    private String name;
    private List<ProductEntry> products;

    public MealTime(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public void addProduct(ProductEntry product) {
        if (products != null) {
            products.add(product);
        }
    }

    public void removeProduct(ProductEntry product) {
        if (products != null) {
            products.remove(product);
        }
    }

    public void updateProductWeight(ProductEntry product, double newWeight) {
        if (products != null && products.contains(product)) {
            product.setWeight(newWeight);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductEntry> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntry> products) {
        this.products = products;
    }

    public double getTotalCalories() {
        if (products == null) return 0;
        return products.stream()
                .mapToDouble(ProductEntry::getCalories)
                .sum();
    }

    public double getTotalProteins() {
        if (products == null) return 0;
        return products.stream()
                .mapToDouble(ProductEntry::getProteins)
                .sum();
    }

    public double getTotalFats() {
        if (products == null) return 0;
        return products.stream()
                .mapToDouble(ProductEntry::getFats)
                .sum();
    }

    public double getTotalCarbs() {
        if (products == null) return 0;
        return products.stream()
                .mapToDouble(ProductEntry::getCarbs)
                .sum();
    }

    @Override
    public boolean validate() {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Meal time name cannot be empty");
            return false;
        }
        if (name.length() < 1 || name.length() > 50) {
            System.out.println("Название приема пищи должно быть от 1 до 50 символов");
            return false;
        }
        return true;
    }
}