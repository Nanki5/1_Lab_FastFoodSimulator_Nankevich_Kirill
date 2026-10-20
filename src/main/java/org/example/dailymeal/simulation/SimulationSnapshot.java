package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SimulationSnapshot {

    private final int customerQueueSize;
    private final int kitchenQueueSize;
    private final int serviceQueueSize;
    private final List<Integer> kitchenQueueOrderNumbers;
    private final List<Integer> serviceQueueOrderNumbers;

    private final List<Integer> orderTakerCurrentOrders;
    private final List<Integer> cookCurrentOrders;
    private final List<Integer> serverCurrentOrders;

    private final int customersInServingLine;
    private final List<CustomerSnapshot> customers;

    private final boolean running;
    private final long timestamp;

    public SimulationSnapshot(int customerQueueSize,
                              int kitchenQueueSize,
                              int serviceQueueSize,
                              List<Integer> kitchenQueueOrderNumbers,
                              List<Integer> serviceQueueOrderNumbers,
                              List<Integer> orderTakerCurrentOrders,
                              List<Integer> cookCurrentOrders,
                              List<Integer> serverCurrentOrders,
                              int customersInServingLine,
                              List<CustomerSnapshot> customers,
                              boolean running) {
        this.customerQueueSize = customerQueueSize;
        this.kitchenQueueSize = kitchenQueueSize;
        this.serviceQueueSize = serviceQueueSize;

        // These lists never contain null
        this.kitchenQueueOrderNumbers = List.copyOf(kitchenQueueOrderNumbers);
        this.serviceQueueOrderNumbers = List.copyOf(serviceQueueOrderNumbers);
        this.customers = List.copyOf(customers);

        // These may contain null
        this.orderTakerCurrentOrders = Collections.unmodifiableList(new ArrayList<>(orderTakerCurrentOrders));
        this.cookCurrentOrders = Collections.unmodifiableList(new ArrayList<>(cookCurrentOrders));
        this.serverCurrentOrders = Collections.unmodifiableList(new ArrayList<>(serverCurrentOrders));

        this.customersInServingLine = customersInServingLine;
        this.running = running;
        this.timestamp = System.currentTimeMillis();
    }

    public int getCustomerQueueSize() { return customerQueueSize; }
    public int getKitchenQueueSize() { return kitchenQueueSize; }
    public int getServiceQueueSize() { return serviceQueueSize; }
    public List<Integer> getKitchenQueueOrderNumbers() { return kitchenQueueOrderNumbers; }
    public List<Integer> getServiceQueueOrderNumbers() { return serviceQueueOrderNumbers; }
    public List<Integer> getOrderTakerCurrentOrders() { return orderTakerCurrentOrders; }
    public List<Integer> getCookCurrentOrders() { return cookCurrentOrders; }
    public List<Integer> getServerCurrentOrders() { return serverCurrentOrders; }
    public int getCustomersInServingLine() { return customersInServingLine; }
    public List<CustomerSnapshot> getCustomers() { return customers; }
    public boolean isRunning() { return running; }
    public long getTimestamp() { return timestamp; }

    public record CustomerSnapshot(int id,
                                   double x,
                                   double y,
                                   Customer.CustomerState state,
                                   Integer orderNumber)
    {
    }
}