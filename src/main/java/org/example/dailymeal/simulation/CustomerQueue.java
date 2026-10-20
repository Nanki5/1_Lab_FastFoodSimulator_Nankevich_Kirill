package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Customer;
import org.example.dailymeal.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomerQueue {
    public final LinkedBlockingQueue<Customer> queue = new LinkedBlockingQueue<>();

    public void put(Customer customer) throws InterruptedException {
        this.queue.put(customer);
    }

    public Customer take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public List<Customer> peekAll() {
        return new ArrayList<>(queue);
    }
}
