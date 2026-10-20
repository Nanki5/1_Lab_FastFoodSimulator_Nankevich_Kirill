package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceQueue {

    private final LinkedBlockingQueue<Order> queue = new LinkedBlockingQueue<>();

    public void put(Order order) {
        queue.add(order);
    }

    public Order take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public List<Order> peekAll() {
        return new ArrayList<>(queue);
    }
}
