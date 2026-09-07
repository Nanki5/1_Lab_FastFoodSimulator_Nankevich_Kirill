package org.example.dailymeal.BusinessLayer;

import java.util.HashMap;
import java.util.Map;

public class DailyRation {
    private Map<String, MealTime> mealTimes;
    private User user;

    public DailyRation(User user) {
        this.mealTimes = new HashMap<>();
        this.mealTimes.put("Breakfast", new MealTime("Breakfast"));
        this.mealTimes.put("Lunch", new MealTime("Lunch"));
        this.mealTimes.put("Dinner", new MealTime("Dinner"));
        this.user = user;
    }

    public MealTime getMealTime(String nameOfMealTime) {
        return mealTimes.get(nameOfMealTime);
    }

    public void addMealTime(MealTime mealTime) {
        mealTimes.put(mealTime.getName(), mealTime);
    }

    public double getTotalDailyCalories() {
        return mealTimes.values().stream()
                .mapToDouble(MealTime::getTotalCalories)
                .sum();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, MealTime> getAllMealTimes() {
        return mealTimes;
    }

    public double getTotalProteins() {
        return mealTimes.values().stream()
                .mapToDouble(MealTime::getTotalProteins)
                .sum();
    }

    public double getTotalFats() {
        return mealTimes.values().stream()
                .mapToDouble(MealTime::getTotalFats)
                .sum();
    }

    public double getTotalCarbs() {
        return mealTimes.values().stream()
                .mapToDouble(MealTime::getTotalCarbs)
                .sum();
    }

    public double getRemainingCalories() {
        if (user != null) {
            return user.calculateDailyCaloriesRate() - getTotalDailyCalories();
        }
        return 0;
    }
}