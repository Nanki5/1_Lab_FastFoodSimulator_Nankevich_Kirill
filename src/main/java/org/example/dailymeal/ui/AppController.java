package org.example.dailymeal.ui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.dailymeal.model.Customer;
import org.example.dailymeal.simulation.SimulationConfig;
import org.example.dailymeal.simulation.SimulationEngine;
import org.example.dailymeal.simulation.SimulationSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppController {

    @FXML private TextField arrivalField;
    @FXML private TextField cookTimeField;
    @FXML private TextField orderTakingTimeField;
    @FXML private TextField orderTakersField;
    @FXML private TextField cooksField;
    @FXML private TextField serversField;

    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Label errorLabel;

    @FXML private Label customerQueueLabel;
    @FXML private Label servingLineLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label orderTakerStatusLabel;
    @FXML private Label cookStatusLabel;
    @FXML private Label serverStatusLabel;
    @FXML private Label kitchenQueueLabel;
    @FXML private Label serviceQueueLabel;

    @FXML private Pane animationPane;
    @FXML private Rectangle queueZone;
    @FXML private Rectangle orderZone;
    @FXML private Rectangle waitingZone;
    @FXML private Rectangle kitchenZone;
    @FXML private Rectangle pickupZone;

    private SimulationEngine engine;
    private final Map<Integer, Circle> circles = new HashMap<>();

    private final List<Rectangle> orderTakerBoxes = new ArrayList<>();
    private final List<Text> orderTakerTexts = new ArrayList<>();
    private final List<Rectangle> cookBoxes = new ArrayList<>();
    private final List<Text> cookTexts = new ArrayList<>();

    @FXML
    public void initialize() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (engine != null) {
                    updateUI();
                }
            }
        }.start();
    }

    @FXML
    private void onStart() {
        errorLabel.setText("");
        try {
            int arrival = Integer.parseInt(arrivalField.getText());
            int cookTime = Integer.parseInt(cookTimeField.getText());
            int orderTakingTime = Integer.parseInt(orderTakingTimeField.getText());
            int orderTakers = Integer.parseInt(orderTakersField.getText());
            int cooks = Integer.parseInt(cooksField.getText());
            int servers = Integer.parseInt(serversField.getText());

            if (arrival <= 0 || cookTime <= 0 || orderTakingTime <= 0 || orderTakers <= 0 || cooks <= 0 || servers <= 0) {
                throw new NumberFormatException();
            }

            SimulationConfig config = new SimulationConfig(arrival, cookTime, orderTakingTime, orderTakers, cooks, servers);
            engine = new SimulationEngine(config);
            engine.start();

            startButton.setDisable(true);
            stopButton.setDisable(false);
            setInputsDisabled(true);

            circles.clear();
            animationPane.getChildren().removeIf(node -> node instanceof Circle);
            createStaffBoxes(orderTakers, cooks);

        } catch (NumberFormatException e) {
            errorLabel.setText("⚠ Ошибка: введите положительные числа!");
            e.printStackTrace(); // Чтобы увидеть ошибку в консоли, если что
        } catch (Exception e) {
            e.printStackTrace(); // Ловим NullPointerException, если забыли поле в FXML
            errorLabel.setText("Ошибка запуска: проверьте консоль");
        }
    }

    @FXML
    private void onStop() {
        if (engine != null) {
            engine.stop();
            engine = null;
        }
        startButton.setDisable(false);
        stopButton.setDisable(true);
        setInputsDisabled(false);
    }

    private void createStaffBoxes(int orderTakersCount, int cooksCount) {
        animationPane.getChildren().removeAll(orderTakerBoxes);
        animationPane.getChildren().removeAll(orderTakerTexts);
        animationPane.getChildren().removeAll(cookBoxes);
        animationPane.getChildren().removeAll(cookTexts);
        orderTakerBoxes.clear(); orderTakerTexts.clear();
        cookBoxes.clear(); cookTexts.clear();

        for (int i = 0; i < orderTakersCount; i++) {
            Rectangle box = new Rectangle(100, 40, Color.web("#FFE0B2"));
            box.setX(orderZone.getX() + 10);
            box.setY(orderZone.getY() + 50 + i * 50);
            box.setArcWidth(10); box.setArcHeight(10);
            box.setStroke(Color.web("#FF9800"));

            Text text = new Text("Кассир " + (i + 1) + "\nСвободен");
            text.setFont(Font.font("System", 11));
            text.setX(box.getX() + 10);
            text.setY(box.getY() + 25);

            orderTakerBoxes.add(box);
            orderTakerTexts.add(text);
            animationPane.getChildren().addAll(box, text);
        }

        for (int i = 0; i < cooksCount; i++) {
            Rectangle box = new Rectangle(120, 40, Color.web("#FFCDD2"));
            box.setX(kitchenZone.getX() + 10 + (i % 2) * 140);
            box.setY(kitchenZone.getY() + 50 + (i / 2) * 50);
            box.setArcWidth(10); box.setArcHeight(10);
            box.setStroke(Color.web("#F44336"));

            Text text = new Text("Повар " + (i + 1) + "\nСвободен");
            text.setFont(Font.font("System", 11));
            text.setX(box.getX() + 10);
            text.setY(box.getY() + 25);

            cookBoxes.add(box);
            cookTexts.add(text);
            animationPane.getChildren().addAll(box, text);
        }
    }

    private void updateUI() {
        SimulationSnapshot snap = engine.getSnapshot();
        if (snap == null) return;

        customerQueueLabel.setText(String.valueOf(snap.getCustomerQueueSize()));
        servingLineLabel.setText(String.valueOf(snap.getCustomersInServingLine()));
        totalCustomersLabel.setText(String.valueOf(snap.getCustomers().size()));

        for (int i = 0; i < orderTakerBoxes.size(); i++) {
            Integer order = snap.getOrderTakerCurrentOrders().get(i);
            Text t = orderTakerTexts.get(i);
            if (order == null) {
                t.setText("Кассир " + (i + 1) + "\nСвободен");
                orderTakerBoxes.get(i).setFill(Color.web("#FFE0B2"));
            } else {
                t.setText("Кассир " + (i + 1) + "\nПринимает #" + order);
                orderTakerBoxes.get(i).setFill(Color.web("#FFB74D"));
            }
        }

        for (int i = 0; i < cookBoxes.size(); i++) {
            Integer order = snap.getCookCurrentOrders().get(i);
            Text t = cookTexts.get(i);
            if (order == null) {
                t.setText("Повар " + (i + 1) + "\nСвободен");
                cookBoxes.get(i).setFill(Color.web("#FFCDD2"));
            } else {
                t.setText("Повар " + (i + 1) + "\nГотовит #" + order);
                cookBoxes.get(i).setFill(Color.web("#E57373"));
            }
        }

        kitchenQueueLabel.setText("Очередь: " + snap.getKitchenQueueSize());
        serviceQueueLabel.setText("Очередь: " + snap.getServiceQueueSize());
        orderTakerStatusLabel.setText("Кассиры: " + format(snap.getOrderTakerCurrentOrders()));
        cookStatusLabel.setText("Повара: " + format(snap.getCookCurrentOrders()));
        serverStatusLabel.setText("Серверы: " + format(snap.getServerCurrentOrders()));

        for (Customer c : engine.getAllCustomers()) {
            moveCustomer(c);
        }

        for (SimulationSnapshot.CustomerSnapshot cs : snap.getCustomers()) {
            Circle circle = circles.get(cs.id());
            if (circle == null) {
                circle = new Circle(10);
                circles.put(cs.id(), circle);
                animationPane.getChildren().add(circle);
            }
            Customer real = findCustomer(cs.id());
            if (real != null) {
                circle.setCenterX(real.getPositionX());
                circle.setCenterY(real.getPositionY());
                circle.setFill(colorFor(real.getState()));
            }
        }

        circles.keySet().removeIf(id -> {
            boolean dead = snap.getCustomers().stream().noneMatch(c -> c.id() == id);
            if (dead) animationPane.getChildren().remove(circles.get(id));
            return dead;
        });
    }

    private void moveCustomer(Customer c) {
        double speed = 1.5;
        double targetX = c.getPositionX();
        double targetY = c.getPositionY();

        switch (c.getState()) {
            case WALKING_TO_ORDER_TAKER:
                // Очередь на входе
                int queueIndex = c.getId() % 10;
                targetX = queueZone.getX() + 20 + (queueIndex % 2) * 30;
                targetY = queueZone.getY() + 40 + (queueIndex / 2) * 30;
                break;
            case PLACING_ORDER:
                // Идут к кассиру
                targetX = orderZone.getX() + 10;
                targetY = orderZone.getY() + 50 + (c.getId() % 3) * 40;
                break;
            case WAITING_IN_SERVING_LINE:
                // В зону ожидания
                int index = c.getId() % 15;
                int row = index / 5;
                int col = index % 5;
                targetX = waitingZone.getX() + 30 + (col * 40);
                targetY = waitingZone.getY() + 30 + (row * 40);
                break;
            case PICKING_UP:
                // К выдаче
                targetX = pickupZone.getX() + 30;
                targetY = pickupZone.getY() + 50 + (c.getId() % 3) * 40;
                break;
            case LEAVING:
                // Уходят
                targetX = animationPane.getWidth() + 50;
                targetY = pickupZone.getY() + 150;
                break;
        }

        double dx = targetX - c.getPositionX();
        double dy = targetY - c.getPositionY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > speed) {
            c.setPosition(c.getPositionX() + (dx / dist) * speed, c.getPositionY() + (dy / dist) * speed);
        } else {
            c.setPosition(targetX, targetY);
            if (c.getState() == Customer.CustomerState.LEAVING) {
                engine.getAllCustomers().remove(c);
            }
        }
    }

    private String format(java.util.List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : list) {
            sb.append(i == null ? "—" : "#" + i).append(" ");
        }
        return sb.toString().trim();
    }

    private Customer findCustomer(int id) {
        for (Customer c : engine.getAllCustomers()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    private Color colorFor(Customer.CustomerState state) {
        return switch (state) {
            case WALKING_TO_ORDER_TAKER -> Color.DODGERBLUE;
            case PLACING_ORDER -> Color.ORANGE;
            case WAITING_IN_SERVING_LINE -> Color.GOLD;
            case PICKING_UP -> Color.LIMEGREEN;
            case LEAVING -> Color.GRAY;
        };
    }

    private void setInputsDisabled(boolean b) {
        arrivalField.setDisable(b);
        cookTimeField.setDisable(b);
        orderTakingTimeField.setDisable(b);
        orderTakersField.setDisable(b);
        cooksField.setDisable(b);
        serversField.setDisable(b);
    }
}