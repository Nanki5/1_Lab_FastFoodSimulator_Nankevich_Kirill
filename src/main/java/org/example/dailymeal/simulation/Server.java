package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Customer;
import org.example.dailymeal.model.Order;

import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {

    private final int serverId;
    private final ServiceQueue serviceQueue;
    private final ConcurrentHashMap<Integer, Customer> orderToCustomer;
    private volatile boolean running = true;

    private volatile Integer currentOrderNumber = null;

    public Server(int serverId,
                  ServiceQueue serviceQueue,
                  ConcurrentHashMap<Integer, Customer> orderToCustomer) {
        this.serverId = serverId;
        this.serviceQueue = serviceQueue;
        this.orderToCustomer = orderToCustomer;
    }

    public void stop() { running = false; }

    public Integer getCurrentOrderNumber() { return currentOrderNumber; }

    @Override
    public void run() {
        System.out.println("[Server#" + serverId + "] started");
        try {
            while (running) {
                Order order = serviceQueue.take();

                currentOrderNumber = order.getOrderNumber();
                order.markReadyForPickUp();

                Customer customer = orderToCustomer.remove(order.getOrderNumber());
                if (customer != null) {
                    customer.setState(Customer.CustomerState.PICKING_UP);
                } else {
                    System.out.println("[Server#" + serverId + "] WARNING: no customer for " + order);
                }

                Thread.sleep(200);

                currentOrderNumber = null;
                System.out.println("[Server#" + serverId + "] served " + order
                        + (customer != null ? " to " + customer : "")
                        + " (service queue size=" + serviceQueue.size() + ")");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Server#" + serverId + "] interrupted, shutting down");
        } finally {
            currentOrderNumber = null;
        }
        System.out.println("[Server#" + serverId + "] stopped");
    }
}