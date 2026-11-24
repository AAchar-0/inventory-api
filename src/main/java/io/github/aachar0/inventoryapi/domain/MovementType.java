package io.github.aachar0.inventoryapi.domain;

/**
 * Direction of a stock change. Each constant has its own body (constant-specific method),
 * so the rule for "what is the new quantity" lives with the type: no if/else chains elsewhere.
 */
public enum MovementType {

    /** Goods received: quantity goes up. Math.addExact throws ArithmeticException on int overflow. */
    IN {
        @Override
        public int apply(int current, int amount) {
            return Math.addExact(current, amount);
        }
    },

    /** Goods sold or removed: quantity goes down (may become negative; the service rejects that). */
    OUT {
        @Override
        public int apply(int current, int amount) {
            return current - amount;
        }
    };

    /** Returns the stock level after applying this movement. */
    public abstract int apply(int current, int amount);
}
