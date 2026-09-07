package org.example.dailymeal.PresentationLayer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.dailymeal.BusinessLayer.User;

public class UserDataController {

    @FXML private TextField weightField;
    @FXML private TextField heightField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> activityComboBox;
    @FXML private Label resultLabel;

    private User user;

    @FXML
    public void initialize() {
        if (activityComboBox.getItems().isEmpty()) {
            activityComboBox = new ComboBox<>();
            activityComboBox.getItems().addAll(
                    "Низкая (1.2)",
                    "Нормальная (1.375)",
                    "Средняя (1.55)",
                    "Высокая (1.725)"
            );
            activityComboBox.setValue("Нормальная (1.375)");
        }
        // Добавляем слушателей
        weightField.textProperty().addListener((obs, old, newVal) -> calculateNorm());
        heightField.textProperty().addListener((obs, old, newVal) -> calculateNorm());
        ageField.textProperty().addListener((obs, old, newVal) -> calculateNorm());
        activityComboBox.valueProperty().addListener((obs, old, newVal) -> calculateNorm());
    }

    private void calculateNorm() {
        try {
            if (weightField.getText() == null || weightField.getText().isEmpty() ||
                    heightField.getText() == null || heightField.getText().isEmpty() ||
                    ageField.getText() == null || ageField.getText().isEmpty() ||
                    activityComboBox.getValue() == null) {
                System.out.println("Some fields are empty");
                return;
            }

            double weight = Double.parseDouble(weightField.getText());
            double height = Double.parseDouble(heightField.getText());
            int age = Integer.parseInt(ageField.getText());

            String activity = activityComboBox.getValue();
            User.ActivityLevel level = getActivityLevel(activity);

            user = new User(weight, height, age, level);
            if (user.validate()) {
                double norm = user.calculateDailyCaloriesRate();
                resultLabel.setText(String.format("Дневная норма: %.0f ккал", norm));
                resultLabel.setStyle("-fx-text-fill: green;");
                System.out.println("Calculated norm: " + norm);
            } else {
                resultLabel.setText("Проверьте введенные данные");
                resultLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Введите корректные числа");
            resultLabel.setStyle("-fx-text-fill: orange;");
        }
    }

    private User.ActivityLevel getActivityLevel(String activity) {
        if (activity == null) return User.ActivityLevel.NORMAL;
        if (activity.contains("Низкая")) return User.ActivityLevel.LOW;
        if (activity.contains("Нормальная")) return User.ActivityLevel.NORMAL;
        if (activity.contains("Средняя")) return User.ActivityLevel.AVERAGE;
        if (activity.contains("Высокая")) return User.ActivityLevel.HIGH;
        return User.ActivityLevel.NORMAL;
    }

    public User getUser() {
        return user;
    }
}