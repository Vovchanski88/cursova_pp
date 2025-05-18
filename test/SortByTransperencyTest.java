import commands.SortByTransperency;
import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import necklace.Necklace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SortByTransperencyTest extends JavaFXTestBase {
    private Necklace necklace;
    private TextArea outputArea;
    private SortByTransperency command;

    @BeforeEach
    void setUp() {
        necklace = new Necklace();
        outputArea = new TextArea();
        command = new SortByTransperency(necklace, outputArea);
    }

    @Test
    void execute_withEmptyNecklace_shouldShowHeaderOnly() {
        // Act
        command.execute();

        // Assert
        String result = outputArea.getText();
        assertTrue(result.startsWith("Камені в намисті відсортовані за прозорістю:"));
        assertEquals(1, result.split("\n").length);
    }

    @Test
    void execute_withSingleGemstone_shouldShowOneGemstone() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));

        // Act
        command.execute();

        // Assert
        String result = outputArea.getText();
        assertTrue(result.contains("Ruby"));
        assertTrue(result.contains("50"));
        assertEquals(2, result.split("\n").length);
    }

    @Test
    void execute_withMultipleGemstones_shouldSortCorrectly() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));
        necklace.addGemstone(new Gemstone("Sapphire", 1.5, 150.0, 75));
        necklace.addGemstone(new Gemstone("Emerald", 1.2, 120.0, 90));

        // Act
        command.execute();

        // Assert
        String result = outputArea.getText();
        String[] lines = result.split("\n");

        // Перевіряємо порядок сортованих каменів
        assertTrue(lines[1].contains("50") || lines[1].contains("Ruby"));
        assertTrue(lines[2].contains("75") || lines[2].contains("Sapphire"));
        assertTrue(lines[3].contains("90") || lines[3].contains("Emerald"));
        assertEquals(4, lines.length); // Заголовок + 3 камені
    }

    @Test
    void constructor_shouldInitializeFields() {
        // Assert
        assertSame(necklace, command.necklace);
        assertSame(outputArea, command.outputArea);
    }

    @Test
    void execute_shouldMaintainOriginalListUnchanged() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));
        necklace.addGemstone(new Gemstone("Sapphire", 1.5, 150.0, 75));
        int originalSize = necklace.getGemstones().size();

        // Act
        command.execute();

        // Assert
        assertEquals(originalSize, necklace.getGemstones().size());
    }
}