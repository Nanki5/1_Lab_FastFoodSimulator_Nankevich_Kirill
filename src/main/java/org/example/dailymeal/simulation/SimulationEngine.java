package org.example.dailymeal.simulation;

import org.example.dailymeal.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {

    private static final long SNAPSHOT_INTERVAL_MS = 50;
    private static final long SHUTDOWN_TIMEOUT_SEC = 2;

    private final SimulationConfig config;

    //created in start()
    private CustomerQueue customerQueue;
    private KitchenQueue kitchenQueue;
    private ServiceQueue serviceQueue;
    private OrderNumberGenerator numberGenerator;
    private ConcurrentHashMap<Integer, Customer> orderToCustomer;
    private CopyOnWriteArrayList<Customer> allCustomers;

    private List<OrderTaker> orderTakers;
    private List<Cook> cooks;
    private List<Server> servers;
    private CustomerGenerator customerGenerator;

    private ExecutorService orderTakerPool;
    private ExecutorService cookPool;
    private ExecutorService serverPool;
    private ScheduledExecutorService snapshotScheduler;

    private volatile SimulationSnapshot snapshot;
    private volatile boolean running = false;

    public SimulationEngine(SimulationConfig config) {
        this.config = config;
    }

    public void start() {
        if (running) throw new IllegalStateException("Engine already running");

        // create shared state
        customerQueue = new CustomerQueue();
        kitchenQueue = new KitchenQueue();
        serviceQueue = new ServiceQueue();
        numberGenerator = new OrderNumberGenerator();
        orderToCustomer = new ConcurrentHashMap<>();
        allCustomers = new CopyOnWriteArrayList<>();

        // create roles
        orderTakers = new ArrayList<>();
        for (int i = 1; i <= config.getOrderTakerCount(); i++) {
            orderTakers.add(new OrderTaker(i, customerQueue, kitchenQueue, numberGenerator, orderToCustomer, config.getOrderTakingTimeMs()));
        }

        cooks = new ArrayList<>();
        for (int i = 1; i <= config.getCookCount(); i++) {
            cooks.add(new Cook(i, kitchenQueue, serviceQueue, config.getCookTimeMs()));
        }

        servers = new ArrayList<>();
        for (int i = 1; i <= config.getServerCount(); i++) {
            servers.add(new Server(i, serviceQueue, orderToCustomer));
        }

        customerGenerator = new CustomerGenerator(config.getCustomerArrivalIntervalMs(),
                customerQueue, allCustomers);

        orderTakerPool = Executors.newFixedThreadPool(config.getOrderTakerCount());
        cookPool = Executors.newFixedThreadPool(config.getCookCount());
        serverPool = Executors.newFixedThreadPool(config.getServerCount());
        snapshotScheduler = Executors.newSingleThreadScheduledExecutor();

        running = true;

        for (OrderTaker ot : orderTakers) orderTakerPool.submit(ot);
        for (Cook c : cooks) cookPool.submit(c);
        for (Server s : servers) serverPool.submit(s);
        customerGenerator.start();

        snapshotScheduler.scheduleAtFixedRate(
                this::buildAndPublishSnapshot,
                0, SNAPSHOT_INTERVAL_MS, TimeUnit.MILLISECONDS
        );

        System.out.println("[Engine] started: " + config);
    }

    public void stop() {
        if (!running) return;
        running = false;

        System.out.println("[Engine] stopping...");

        customerGenerator.stop();

        for (OrderTaker ot : orderTakers) ot.stop();
        for (Cook c : cooks) c.stop();
        for (Server s : servers) s.stop();

        orderTakerPool.shutdownNow();
        cookPool.shutdownNow();
        serverPool.shutdownNow();
        snapshotScheduler.shutdownNow();

        awaitQuietly(orderTakerPool, "orderTakerPool");
        awaitQuietly(cookPool, "cookPool");
        awaitQuietly(serverPool, "serverPool");
        awaitQuietly(snapshotScheduler, "snapshotScheduler");

        // Publish one final snapshot with running=false so the UI can react.
        buildAndPublishSnapshot();

        System.out.println("[Engine] stopped");
    }

    public SimulationSnapshot getSnapshot() {
        return snapshot;
    }

    public boolean isRunning() {
        return running;
    }

    private void buildAndPublishSnapshot() {
        try {
            if (customerQueue == null) return;

            List<Integer> kitchenNums = new ArrayList<>();
            for (var order : kitchenQueue.peekAll()) kitchenNums.add(order.getOrderNumber());

            List<Integer> serviceNums = new ArrayList<>();
            for (var order : serviceQueue.peekAll()) serviceNums.add(order.getOrderNumber());

            List<Integer> orderTakerStatus = new ArrayList<>();
            for (OrderTaker ot : orderTakers) orderTakerStatus.add(ot.getCurrentOrderNumber());

            List<Integer> cookStatus = new ArrayList<>();
            for (Cook c : cooks) cookStatus.add(c.getCurrentOrderNumber());

            List<Integer> serverStatus = new ArrayList<>();
            for (Server s : servers) serverStatus.add(s.getCurrentOrderNumber());

            int inServingLine = 0;
            List<SimulationSnapshot.CustomerSnapshot> customerSnaps = new ArrayList<>();
            for (Customer c : allCustomers) {
                if (c.getState() == Customer.CustomerState.WAITING_IN_SERVING_LINE) inServingLine++;

                Integer orderNum = c.getCurrentOrder() != null
                        ? c.getCurrentOrder().getOrderNumber()
                        : null;
                customerSnaps.add(new SimulationSnapshot.CustomerSnapshot(
                        c.getId(), c.getPositionX(), c.getPositionY(), c.getState(), orderNum));
            }

            snapshot = new SimulationSnapshot(
                    customerQueue.size(),
                    kitchenQueue.size(),
                    serviceQueue.size(),
                    kitchenNums,
                    serviceNums,
                    orderTakerStatus,
                    cookStatus,
                    serverStatus,
                    inServingLine,
                    customerSnaps,
                    running
            );
        } catch (Throwable t) {
            System.err.println("[Engine] snapshot build failed: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void awaitQuietly(ExecutorService pool, String name) {
        try {
            if (!pool.awaitTermination(SHUTDOWN_TIMEOUT_SEC, TimeUnit.SECONDS)) {
                System.err.println("[Engine] " + name + " did not terminate in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //exposed for UI if needed
    public CopyOnWriteArrayList<Customer> getAllCustomers() { return allCustomers; }
    public CustomerQueue getCustomerQueue() { return customerQueue; }
    public KitchenQueue getKitchenQueue() { return kitchenQueue; }
    public ServiceQueue getServiceQueue() { return serviceQueue; }
    public SimulationConfig getConfig() { return config; }
}