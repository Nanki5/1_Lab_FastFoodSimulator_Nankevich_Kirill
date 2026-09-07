package org.example.dailymeal.ServiceLayer.impl;

import org.example.dailymeal.BusinessLayer.DailyRation;
import org.example.dailymeal.BusinessLayer.MealTime;
import org.example.dailymeal.BusinessLayer.ProductEntry;
import org.example.dailymeal.BusinessLayer.User;
import org.example.dailymeal.DataAccessLayer.Product;
import org.example.dailymeal.ServiceLayer.IMealPlanService;

import java.io.*;
import java.util.*;

public class MealPlanService implements IMealPlanService {

    private DailyRation currentDailyRation;
    private User currentUser;

    // Конструктор по умолчанию
    public MealPlanService() {
        this.currentUser = null;
        createNewDailyRation();
    }

    // Конструктор с пользователем
    public MealPlanService(User user) {
        this.currentUser = user;
        createNewDailyRation();
    }

    @Override
    public void setUser(User user) {
        this.currentUser = user;
        if (currentDailyRation != null) {
            currentDailyRation.setUser(user);
        }
    }

    @Override
    public User getUser() {
        return currentUser;
    }

    @Override
    public void createNewDailyRation() {
        if (currentUser != null) {
            this.currentDailyRation = new DailyRation(currentUser);
        } else {
            this.currentDailyRation = new DailyRation(null);
            currentDailyRation.addMealTime(new MealTime("Breakfast"));
            currentDailyRation.addMealTime(new MealTime("Lunch"));
            currentDailyRation.addMealTime(new MealTime("Dinner"));
        }
    }

    @Override
    public void saveDailyRation(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("=== DAILY MEAL PLAN ===");
            if (currentUser != null) {
                writer.printf("User: %.1f kg, %.1f cm, %d years%n",
                        currentUser.getWeight(),
                        currentUser.getHeight(),
                        currentUser.getAge());
                writer.printf("Daily Calorie Norm: %.0f kcal%n",
                        currentUser.calculateDailyCaloriesRate());
            }
            writer.println("=======================");

            for (Map.Entry<String, MealTime> entry : currentDailyRation.getAllMealTimes().entrySet()) {
                writer.printf("%n--- %s ---%n", entry.getKey());
                MealTime mealTime = entry.getValue();

                if (mealTime.getProducts().isEmpty()) {
                    writer.println("No products");
                } else {
                    writer.printf("%-20s %-10s %-10s %-10s %-10s%n",
                            "Product", "Weight(g)", "Calories", "Protein", "Fats");
                    writer.println("-".repeat(60));

                    for (ProductEntry productEntry : mealTime.getProducts()) {
                        writer.printf("%-20s %-10.0f %-10.1f %-10.1f %-10.1f%n",
                                productEntry.getProduct().getName(),
                                productEntry.getWeight(),
                                productEntry.getCalories(),
                                productEntry.getProteins(),
                                productEntry.getFats());
                    }
                    writer.printf("%nTotal: %.1f kcal, %.1f protein, %.1f fats%n",
                            mealTime.getTotalCalories(),
                            mealTime.getTotalProteins(),
                            mealTime.getTotalFats());
                }
            }

            writer.printf("%n%n=== TOTAL DAILY: %.1f / %.0f kcal ===%n",
                    getTotalDailyCalories(),
                    currentUser != null ? currentUser.calculateDailyCaloriesRate() : 0);

        } catch (IOException e) {
            System.err.println("Error saving meal plan: " + e.getMessage());
        }
    }


    @Override
    public Map<String, MealTime> getAllMealTimes() {
        return currentDailyRation.getAllMealTimes();
    }

    @Override
    public MealTime getMealTime(String name) {
        return currentDailyRation.getMealTime(name);
    }

    @Override
    public void addMealTime(String name) {
        MealTime newMealTime = new MealTime(name);
        if (newMealTime.validate()) {
            currentDailyRation.addMealTime(newMealTime);
        }
    }

    @Override
    public void removeMealTime(String name) {
        if (!name.equalsIgnoreCase("Breakfast") &&
                !name.equalsIgnoreCase("Lunch") &&
                !name.equalsIgnoreCase("Dinner")) {
            currentDailyRation.getAllMealTimes().remove(name);
        }
    }

    @Override
    public void addProductToMealTime(String mealTimeName, Product product, double weight) {
        MealTime mealTime = getMealTime(mealTimeName);
        if (mealTime != null && product != null) {
            ProductEntry productEntry = new ProductEntry(product, weight);
            if (productEntry.validate()) {
                mealTime.addProduct(productEntry);
            }
        }
    }

    @Override
    public void removeProductFromMealTime(String mealTimeName, ProductEntry productEntry) {
        MealTime mealTime = getMealTime(mealTimeName);
        if (mealTime != null && productEntry != null) {
            mealTime.removeProduct(productEntry);
        }
    }

    @Override
    public void updateProductWeight(String mealTimeName, ProductEntry productEntry, double newWeight) {
        MealTime mealTime = getMealTime(mealTimeName);
        if (mealTime != null && productEntry != null) {
            mealTime.updateProductWeight(productEntry, newWeight);
        }
    }



    @Override
    public double getTotalDailyCalories() {
        return currentDailyRation != null ? currentDailyRation.getTotalDailyCalories() : 0.0;
    }

    @Override
    public double getRemainingCalories() {
        if (currentUser != null && currentDailyRation != null) {
            return currentUser.calculateDailyCaloriesRate() - getTotalDailyCalories();
        }
        return 0.0;
    }

    @Override
    public boolean isWithinDailyNorm() {
        double remaining = getRemainingCalories();
        return remaining >= 0 && remaining <= 500; // Допуск 500 калорий
    }

    // Дополнительные вспомогательные методы

    /**
     * Получить процент от дневной нормы
     */
    public double getPercentageOfDailyNorm() {
        double total = getTotalDailyCalories();
        double norm = currentUser != null ? currentUser.calculateDailyCaloriesRate() : 1;
        return (total / norm) * 100;
    }

    /**
     * Проверить, превышен ли лимит калорий
     */
    public boolean isCalorieLimitExceeded() {
        return getRemainingCalories() < 0;
    }

    /**
     * Получить краткую сводку по дневному рациону
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DAILY SUMMARY ===\n");
        sb.append(String.format("Total calories: %.1f kcal\n", getTotalDailyCalories()));

        if (currentUser != null) {
            sb.append(String.format("Daily norm: %.0f kcal\n", currentUser.calculateDailyCaloriesRate()));
            sb.append(String.format("Remaining: %.1f kcal\n", getRemainingCalories()));
            sb.append(String.format("Percentage: %.1f%%\n", getPercentageOfDailyNorm()));

            if (isCalorieLimitExceeded()) {
                sb.append("⚠️ WARNING: Calorie limit exceeded!\n");
            } else {
                sb.append("✓ Within daily norm\n");
            }
        }

        sb.append("\nBreakdown by meal:\n");
        for (Map.Entry<String, MealTime> entry : getAllMealTimes().entrySet()) {
            sb.append(String.format("  %s: %.1f kcal\n",
                    entry.getKey(),
                    entry.getValue().getTotalCalories()));
        }

        return sb.toString();
    }
}