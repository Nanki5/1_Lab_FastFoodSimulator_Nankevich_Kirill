package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Customer;
import org.example.dailymeal.model.Order;
import java.util.concurrent.ConcurrentHashMap;

public class OrderTaker implements Runnable {
    private final int orderTakerId;
    private final CustomerQueue customerQueue;
    private final KitchenQueue kitchenQueue;
    private final OrderNumberGenerator numberGenerator;
    private final ConcurrentHashMap<Integer, Customer> orderToCustomer;
    private final int orderTakingTimeMs; // НОВОЕ ПОЛЕ
    private volatile boolean running = true;
    private volatile Integer currentOrderNumber = null;

    public OrderTaker(int orderTakerId, CustomerQueue customerQueue, KitchenQueue kitchenQueue,
                      OrderNumberGenerator numberGenerator, ConcurrentHashMap<Integer, Customer> orderToCustomer,
                      int orderTakingTimeMs) {
        this.orderTakerId = orderTakerId;
        this.customerQueue = customerQueue;
        this.kitchenQueue = kitchenQueue;
        this.numberGenerator = numberGenerator;
        this.orderToCustomer = orderToCustomer;
        this.orderTakingTimeMs = orderTakingTimeMs;
    }

    public void stop() { running = false; }
    public Integer getCurrentOrderNumber() { return currentOrderNumber; }

    @Override
    public void run() {
        try {
            while (running) {
                Customer customer = customerQueue.take();
                int orderNumber = numberGenerator.next();
                Order order = new Order(orderNumber);

                currentOrderNumber = orderNumber;
                customer.setCurrentOrder(order);
                customer.setState(Customer.CustomerState.PLACING_ORDER);

                // Ждем столько, сколько указано в настройках
                Thread.sleep(orderTakingTimeMs);

                customer.setState(Customer.CustomerState.WAITING_IN_SERVING_LINE);
                orderToCustomer.put(orderNumber, customer);
                kitchenQueue.put(order);
                currentOrderNumber = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            currentOrderNumber = null;
        }
    }
}