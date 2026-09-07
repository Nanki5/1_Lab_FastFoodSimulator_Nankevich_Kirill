package org.example.dailymeal.BusinessLayer;

import org.example.dailymeal.DataAccessLayer.Product;

import org.example.dailymeal.DataAccessLayer.Product;

public class ProductEntry extends BusinessObject {
    private Product product;
    private double weight;

    public ProductEntry(Product product, double weight) {
        this.product = product;
        this.weight = weight;
    }

    public Double getProductProtein(){
        return this.product.getProtein();
    }

    public Double getProductFats(){
        return this.product.getFats();
    }

    public Double getPoductCarbs(){
        return this.product.getCarbs();
    }

    public int getPoductCalories(){
        return this.product.getCalories();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getCalories() {
        return product.getCalories() * (weight / 100.0);
    }

    public double getProteins() {
        Double protein = product.getProtein();
        if (protein == null) return 0.0;
        return protein * (weight / 100.0);
    }

    public double getFats() {
        Double fats = product.getFats();
        if (fats == null) return 0.0;
        return fats * (weight / 100.0);
    }

    public double getCarbs() {
        Double carbs = product.getCarbs();
        if (carbs == null) return 0.0;
        return carbs * (weight / 100.0);
    }

    @Override
    public boolean validate() {
        if (product == null) {
            System.out.println("Product cannot be null");
            return false;
        }

        if (weight <= 0) {
            System.out.println("Weight must be positive");
            return false;
        }

        if (weight > 5000) {
            System.out.println("Weight cannot exceed 5000 grams");
            return false;
        }

        if (product.getName() == null || product.getName().isEmpty()) {
            System.out.println("Product name is invalid");
            return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return String.format("%s - %.0fg (%.1f ккал)",
                product.getName(), weight, getCalories());
    }
}