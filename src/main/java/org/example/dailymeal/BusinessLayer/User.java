package org.example.dailymeal.BusinessLayer;

public class User extends BusinessObject{
    private double weight;
    private double height;
    private int age;
    private ActivityLevel activityLevel;

    public enum ActivityLevel {
        LOW(1.2),
        NORMAL(1.375),
        AVERAGE(1.55),
        HIGH(1.725);
        private double arm;

        ActivityLevel(double arm) {
            this.arm = arm;
        }

        public double getArm(){
            return this.arm;
        }
    }

    public User(double weight, double height, int age, ActivityLevel activityLevel) {
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.activityLevel = activityLevel;
    }

    public User() {
        this.weight = 70;
        this.height = 170;
        this.age = 30;
        this.activityLevel = ActivityLevel.NORMAL;
    }

    public double calculateBMR() {
        return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
    }

    public double calculateDailyCaloriesRate() {
        double bmr = calculateBMR();
        return bmr * activityLevel.getArm();
    }


    public boolean validate() {
        if (weight <= 0 || weight > 500) {
            return false;
        }
        if (height <= 0 || height > 300) {
            return false;
        }
        if (age <= 0 || age > 150) {
            return false;
        }
        if (activityLevel == null) {
            return false;
        }
        return true;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    public void setHeight(double height) {
        this.height = height;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    @Override
    public String toString() {
        return String.format("User{weight=%.1fkg, height=%.1fcm, age=%d, activity=%s, dailyNorm=%.0f}",
                weight, height, age, activityLevel, calculateDailyCaloriesRate());
    }
}