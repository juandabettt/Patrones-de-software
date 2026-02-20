

import java.util.HashMap;
import java.util.Map;

public class RegistroPrototipos {
    private final Map<String, PedidoComida> registry = new HashMap<>();

    public void register(String key, PedidoComida prototype) {
        registry.put(key.toLowerCase(), prototype);
        System.out.println("[Registry] Template registered: " + key);
    }

    public PedidoComida getClone(String key, String newCustomer) {
        PedidoComida prototype = registry.get(key.toLowerCase());
        if (prototype == null) throw new IllegalArgumentException("Key not found");
        return prototype.cloneFor(newCustomer);
    }
}