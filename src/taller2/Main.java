

public class Main {
    public static void main(String[] args) {
        System.out.println("=== FOOD DELIVERY SYSTEM READY ===\n");

        // 1. Builder
        PedidoComida masterBurger = new PedidoComida.Builder("SYSTEM", "Master Burger")
                .size("large")
                .addExtra("Bacon")
                .build();

        // 2. Registry
        RegistroPrototipos registry = new RegistroPrototipos();
        registry.register("burger_promo", masterBurger);

        // 3. Prototype (Clone)
        PedidoComida johnOrder = registry.getClone("burger_promo", "John Wick");

        System.out.println("\nRESULTS:");
        System.out.println("Original: " + masterBurger.describe());
        System.out.println("Clone:    " + johnOrder.describe());
    }
}