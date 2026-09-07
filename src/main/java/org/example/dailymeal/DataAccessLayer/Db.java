package org.example.dailymeal.DataAccessLayer;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Db")
public class Db {
    private List<Category> categories;

    @XmlElement(name = "Category")
    public List<Category> getCategories(){
        return categories;
    }
    public void setCategories(List<Category> categories){
        this.categories = categories;
    }
}
