# Food Delivery System - Design Patterns Implementation 🍔

This project is an academic workshop focused on implementing **Creational Design Patterns** in Java. It simulates a personalized food delivery platform where users can build complex orders and the system can clone pre-defined "Master Templates" to optimize efficiency.

## 🛠 Patterns Implemented

### 1. Builder Pattern
Located in `PedidoComida.java` (as an inner static class).
* **Purpose:** Allows the step-by-step construction of complex food orders.
* **Benefits:** Avoids "telescoping constructors" and makes the code more readable when adding extras or choosing sizes.

### 2. Prototype Pattern
Implemented through the `Prototipo` interface and the `clone()` methods.
* **Purpose:** Enables the system to create new orders by copying existing "Master Templates" instead of building them from scratch.
* **Benefits:** Significantly improves performance and simplifies the creation of popular combos.

### 3. Registry (Prototype Manager)
Located in `RegistroPrototipos.java`.
* **Purpose:** Stores and manages a collection of pre-defined food prototypes.
* **Benefits:** Decouples the client from the specific cloning logic.

## 📂 Project Structure

```text
src/taller2/
├── Main.java                # Application entry point & orchestration
├── PedidoComida.java        # Core logic (Builder + Prototype implementation)
├── RegistroPrototipos.java  # Prototype Manager
├── Prototipo.java           # Prototype Interface
└── Personalizable.java      # Utility Interface for descriptions and pricing
