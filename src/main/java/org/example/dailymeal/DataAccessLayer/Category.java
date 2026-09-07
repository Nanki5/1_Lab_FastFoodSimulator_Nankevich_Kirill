package org.example.dailymeal.DataAccessLayer;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

public class Category {
    private String name;

    private String description;

    private List<Product> products;

    @XmlAttribute(name = "name")
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @XmlAttribute(name = "description")
    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @XmlElement(name = "Product")
    public List<Product> getProducts(){
        return products;
    }
    public void setProducts(List<Product> products){
        this.products = products;
    }

}
