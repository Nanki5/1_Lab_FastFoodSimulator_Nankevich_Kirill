package org.example.dailymeal.simulation;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderNumberGenerator {
    private final AtomicInteger counter = new AtomicInteger(0);

    public int next(){
        return counter.incrementAndGet();
    }
}
