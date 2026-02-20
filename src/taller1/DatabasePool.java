package taller1;

/**
 * Workshop 1: Software Design Patterns
 * Implementation of the Singleton Pattern using Double-Checked Locking.
 */
public class DatabasePool {

    private static volatile DatabasePool instance;
    private int availableConnections;
    private final int MAX_CONNECTIONS = 5;

    private DatabasePool() {
        this.availableConnections = MAX_CONNECTIONS;
        System.out.println("[SYSTEM] Initializing Database Pool with " + MAX_CONNECTIONS + " slots...");
    }

    public static DatabasePool getInstance() {
        if (instance == null) {
            synchronized (DatabasePool.class) {
                if (instance == null) {
                    instance = new DatabasePool();
                }
            }
        }
        return instance;
    }

    public void useConnection() {
        if (availableConnections > 0) {
            availableConnections--;
            System.out.println("[POOL] Connection granted. Remaining slots: " + availableConnections);
        } else {
            System.out.println("[ERROR] No connections available in the pool.");
        }
    }

    public int getAvailableConnections() {
        return availableConnections;
    }

    public static void main(String[] args) {
        System.out.println("--- Testing Singleton DatabasePool ---");
        DatabasePool pool = DatabasePool.getInstance();
        pool.useConnection();
        System.out.println("Remaining available slots: " + pool.getAvailableConnections());
    }
}