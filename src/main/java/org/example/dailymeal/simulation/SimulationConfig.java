package org.example.dailymeal.simulation;

public class SimulationConfig {
    private final int customerArrivalIntervalMs;
    private final int cookTimeMs;
    private final int orderTakingTimeMs; // НОВОЕ ПОЛЕ
    private final int orderTakerCount;
    private final int cookCount;
    private final int serverCount;

    public SimulationConfig(int customerArrivalIntervalMs, int cookTimeMs, int orderTakingTimeMs,
                            int orderTakerCount, int cookCount, int serverCount) {
        this.customerArrivalIntervalMs = customerArrivalIntervalMs;
        this.cookTimeMs = cookTimeMs;
        this.orderTakingTimeMs = orderTakingTimeMs;
        this.orderTakerCount = orderTakerCount;
        this.cookCount = cookCount;
        this.serverCount = serverCount;
    }

    public int getCustomerArrivalIntervalMs() { return customerArrivalIntervalMs; }
    public int getCookTimeMs() { return cookTimeMs; }
    public int getOrderTakingTimeMs() { return orderTakingTimeMs; } // ГЕТТЕР
    public int getOrderTakerCount() { return orderTakerCount; }
    public int getCookCount() { return cookCount; }
    public int getServerCount() { return serverCount; }
}