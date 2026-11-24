package io.github.aachar0.inventoryapi.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovementTypeTest {

    // One test method, many cases: each CSV row is one run. Columns: type, current stock, amount, expected result.
    @ParameterizedTest
    @CsvSource({
            "IN,  10, 5, 15",
            "IN,   0, 1,  1",
            "OUT, 10, 4,  6",
            "OUT,  3, 5, -2"      // the enum returns the raw result; the SERVICE rejects negatives
    })
    void appliesMovementToStock(MovementType type, int current, int amount, int expected) {
        assertThat(type.apply(current, amount)).isEqualTo(expected);
    }

    @Test
    void inThrowsOnIntegerOverflow() {
        assertThatThrownBy(() -> MovementType.IN.apply(Integer.MAX_VALUE, 1))
                .isInstanceOf(ArithmeticException.class);
    }
}
