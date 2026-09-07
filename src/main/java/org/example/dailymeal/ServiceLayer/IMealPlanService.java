package org.example.dailymeal.ServiceLayer;

import org.example.dailymeal.BusinessLayer.DailyRation;
import org.example.dailymeal.BusinessLayer.MealTime;
import org.example.dailymeal.BusinessLayer.ProductEntry;
import org.example.dailymeal.BusinessLayer.User;
import org.example.dailymeal.DataAccessLayer.Product;
import java.util.List;
import java.util.Map;

public interface IMealPlanService {

    void setUser(User user);
    User getUser();

    void createNewDailyRation();
    void saveDailyRation(String fileName);

    Map<String, MealTime> getAllMealTimes();
    MealTime getMealTime(String name);
    void addMealTime(String name);
    void removeMealTime(String name);

    void addProductToMealTime(String mealTimeName, Product product, double weight);
    void removeProductFromMealTime(String mealTimeName, ProductEntry productEntry);
    void updateProductWeight(String mealTimeName, ProductEntry productEntry, double newWeight);

    double getTotalDailyCalories();
    double getRemainingCalories();

    boolean isWithinDailyNorm();
}