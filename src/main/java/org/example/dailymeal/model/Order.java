package org.example.dailymeal.model;

import java.util.concurrent.CompletableFuture;

public class Order {
    private final int orderNumber;
    private final CompletableFuture<Order> readyForServer;
    private final CompletableFuture<Order> readyForPickUp;

    public Order(int orderNumber){
        this.orderNumber = orderNumber;
        this.readyForServer = new CompletableFuture<>();
        this.readyForPickUp = new CompletableFuture<>();

    }

    public void markReadyForServer() {
        readyForServer.complete(this);
    }

    public void markReadyForPickUp() {
        readyForPickUp.complete(this);
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public CompletableFuture<Order> getReadyForServer() {
        return readyForServer;
    }

    public CompletableFuture<Order> getReadyForPickUp() {
        return readyForPickUp;
    }

    @Override
    public String toString() {
        return "Order#" + orderNumber;
    }
}
