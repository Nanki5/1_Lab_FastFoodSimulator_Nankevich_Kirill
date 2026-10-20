package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Order;

public class Cook implements Runnable {

    private final int cookId;
    private final KitchenQueue kitchenQueue;
    private final ServiceQueue serviceQueue;
    private final int cookTimeMs;
    private volatile boolean running = true;

    private volatile Integer currentOrderNumber = null;

    public Cook(int cookId,
                KitchenQueue kitchenQueue,
                ServiceQueue serviceQueue,
                int cookTimeMs) {
        this.cookId = cookId;
        this.kitchenQueue = kitchenQueue;
        this.serviceQueue = serviceQueue;
        this.cookTimeMs = cookTimeMs;
    }

    public void stop() {
        running = false;
    }

    public Integer getCurrentOrderNumber() {
        return currentOrderNumber;
    }

    @Override
    public void run() {
        System.out.println("[Cook#" + cookId + "] started");
        try {
            while (running) {
                Order order = kitchenQueue.take();

                currentOrderNumber = order.getOrderNumber();
                System.out.println("[Cook#" + cookId + "] cooking " + order
                        + " (kitchen queue size=" + kitchenQueue.size() + ")");

                Thread.sleep(cookTimeMs);

                serviceQueue.put(order);
                order.markReadyForServer();

                currentOrderNumber = null;
                System.out.println("[Cook#" + cookId + "] finished " + order
                        + " -> service queue (size=" + serviceQueue.size() + ")");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Cook#" + cookId + "] interrupted, shutting down");
        } finally {
            currentOrderNumber = null;
        }
        System.out.println("[Cook#" + cookId + "] stopped");
    }
}