
import java.util.*;

public class PedidoComida implements Prototipo<PedidoComida>, Personalizable {
    private final String orderId;
    private final String customerName;
    private final String mainDish;
    private final String size;
    private final List<String> extras;
    private final double basePrice;

    private PedidoComida(Builder builder) {
        this.orderId = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.customerName = builder.customerName;
        this.mainDish = builder.mainDish;
        this.size = builder.size;
        this.extras = Collections.unmodifiableList(new ArrayList<>(builder.extras));
        this.basePrice = builder.basePrice;
    }

    @Override
    public PedidoComida clone() {
        return this.cloneFor(this.customerName);
    }

    public PedidoComida cloneFor(String newCustomer) {
        return new Builder(newCustomer, this.mainDish)
                .size(this.size)
                .setExtras(new ArrayList<>(this.extras))
                .build();
    }

    @Override
    public String describe() {
        return String.format("Order #%s | Client: %-12s | Dish: %s (%s) | Price: $%.2f",
                orderId, customerName, mainDish, size, getPrice());
    }

    @Override
    public double getPrice() {
        return basePrice + (extras.size() * 1.50);
    }

    public static class Builder {
        private String customerName;
        private String mainDish;
        private String size = "medium";
        private List<String> extras = new ArrayList<>();
        private double basePrice = 10.0;

        public Builder(String customerName, String mainDish) {
            this.customerName = customerName;
            this.mainDish = mainDish;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Builder addExtra(String extra) {
            this.extras.add(extra);
            return this;
        }

        protected Builder setExtras(List<String> extras) {
            this.extras = extras;
            return this;
        }

        public PedidoComida build() {
            return new PedidoComida(this);
        }
    }
}