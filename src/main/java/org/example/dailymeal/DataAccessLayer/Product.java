package org.example.dailymeal.DataAccessLayer;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class Product {

    private String name;

    private int gramms;

    private Double  protein;

    private Double  fats;

    private Double  carbs; // uglivody

    private int calories;


    public Product(){}
    public Product(String name, int gramms, Double  protein, Double  fats, Double  carbs, int calories) {
        this.name = name;
        this.gramms = gramms;
        this.protein = protein;
        this.fats = fats;
        this.carbs = carbs;
        this.calories = calories;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if(name.isEmpty()) this.name = "Empty";
        this.name = name;
    }

    @XmlElement(name = "Gramms")
    public int getGramms() {
        return gramms;
    }
    public void setGramms(int gramms) {
        this.gramms = gramms;
    }

    @XmlElement(name = "Protein")
    @XmlJavaTypeAdapter(DoubleAdapter.class)
    public Double getProtein() {
        return protein;
    }
    public void setProtein(Double  protein) {
        this.protein = protein;
    }

    @XmlElement(name = "Fats")
    @XmlJavaTypeAdapter(DoubleAdapter.class)
    public Double getFats() {
        return fats;
    }
    public void setFats(Double  fats) {
        this.fats = fats;
    }

    @XmlElement(name = "Carbs")
    @XmlJavaTypeAdapter(DoubleAdapter.class)
    public Double getCarbs() {
        return carbs;
    }
    public void setCarbs(Double  carbs) {
        this.carbs = carbs;
    }

    @XmlElement(name = "Calories")
    public int getCalories() {
        return calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }

}
