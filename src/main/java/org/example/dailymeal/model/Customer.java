package org.example.dailymeal.model;

public class Customer {
    private final int id;
    private volatile Order currentOrder;

    private volatile double positionX;
    private volatile double positionY;

    private volatile CustomerState state;

    public enum CustomerState {
        WALKING_TO_ORDER_TAKER,
        PLACING_ORDER,
        WAITING_IN_SERVING_LINE,
        PICKING_UP,
        LEAVING
    }

    public Customer(int id, double startX, double startY) {
        this.id = id;
        this.positionX = startX;
        this.positionY = startY;
        this.state = CustomerState.WALKING_TO_ORDER_TAKER;
    }

    public int getId() {
        return id;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    @Override
    public String toString() {
        return "Customer#" + id + "(" + state + ")";
    }
}
