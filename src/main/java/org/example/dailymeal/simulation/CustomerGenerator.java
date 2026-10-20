package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Customer;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerGenerator {

    private static final double SPAWN_X = 70;
    private static final double SPAWN_Y = 100;

    private final int arrivalIntervalMs;
    private final CustomerQueue customerQueue;
    private final List<Customer> allCustomers;

    private final AtomicInteger customerIdCounter = new AtomicInteger(0);
    private ScheduledExecutorService scheduler;

    public CustomerGenerator(int arrivalIntervalMs,
                             CustomerQueue customerQueue,
                             List<Customer> allCustomers) {
        this.arrivalIntervalMs = arrivalIntervalMs;
        this.customerQueue = customerQueue;
        this.allCustomers = allCustomers;
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::spawnCustomer,
                0,
                arrivalIntervalMs,
                TimeUnit.MILLISECONDS
        );
        System.out.println("[CustomerGenerator] started, interval=" + arrivalIntervalMs + "ms");
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            System.out.println("[CustomerGenerator] stopped");
        }
    }

    private void spawnCustomer() {
        try {
            int id = customerIdCounter.incrementAndGet();
            Customer customer = new Customer(id, SPAWN_X, SPAWN_Y);

            allCustomers.add(customer);
            customerQueue.put(customer);

            System.out.println("[CustomerGenerator] spawned " + customer
                    + " (customer queue size=" + customerQueue.size() + ")");
        } catch (Throwable t) {
            System.err.println("[CustomerGenerator] spawn failed: " + t.getMessage());
            t.printStackTrace();
        }
    }
}