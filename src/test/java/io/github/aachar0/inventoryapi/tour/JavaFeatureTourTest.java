package io.github.aachar0.inventoryapi.tour;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Plain JUnit 5 + AssertJ tests (no Spring): fast, because nothing needs to start. */
class JavaFeatureTourTest {

    @Test
    void everyLessonRunsAndReturnsALine() throws Exception {
        List<String> lines = JavaFeatureTour.run();
        assertThat(lines).hasSize(12);
        assertThat(lines.get(0)).startsWith("1. ");
        assertThat(lines.get(11)).startsWith("12. ");
    }

    @Test
    void concurrencyLessonGivesDeterministicResults() throws Exception {
        assertThat(JavaFeatureTour.run())
                .anyMatch(line -> line.contains("atomic=10 squares=30 words={even=2, odd=2} combined=42"));
    }

    @Test
    void streamLessonComputesStockValue() throws Exception {
        assertThat(JavaFeatureTour.run()).anyMatch(line -> line.contains("stockValue=310.00"));
    }

    @Test
    void sealedShapesComputeAreas() {
        assertThat(JavaFeatureTour.area(new JavaFeatureTour.Rect(2, 3))).isEqualTo(6.0);
        assertThat(JavaFeatureTour.area(new JavaFeatureTour.Square(4))).isEqualTo(16.0);
        assertThat(JavaFeatureTour.area(new JavaFeatureTour.Circle(1))).isEqualTo(Math.PI);
    }

    @Test
    void recordCompactConstructorRejectsNegativeSide() {
        assertThatThrownBy(() -> new JavaFeatureTour.Square(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("side");
    }
}
