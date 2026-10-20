package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class KitchenQueue {
    public LinkedBlockingQueue<Order> queue;

    public KitchenQueue(){
        this.queue = new LinkedBlockingQueue<>();
    }

    public void put(Order order) throws InterruptedException {
        this.queue.put(order);
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

