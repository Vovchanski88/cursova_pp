import commands.ShowTotalWeightAndPriceCommand;
import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import necklace.Necklace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowTotalWeightAndPriceCommandTest extends JavaFXTestBase {
    private Necklace necklace;
    private TextArea outputArea;
    private ShowTotalWeightAndPriceCommand command;

    @BeforeEach
    void setUp() {
        necklace = new Necklace();
        outputArea = new TextArea();
        command = new ShowTotalWeightAndPriceCommand(necklace, outputArea);
    }

    @Test
    void execute_withEmptyNecklace_shouldShowZeroValues() {
        // Act
        command.execute();

        // Assert
        String result = outputArea.getText();
        assertTrue(result.contains("Загальна вага: 0.00 карат"));
        assertTrue(result.contains("Загальна ціна: 0.00"));
    }

    @Test
    void execute_withSingleGemstone_shouldCalculateCorrectly() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.5, 100.0, 50));

        // Act
        command.execute();

        // Assert
        String result = outputArea.getText();
        assertTrue(result.contains("Загальна вага: 1.50 карат"));
        assertTrue(result.contains("Загальна ціна: 100.00"));
    }

    @Test
    void execute_withMultipleGemstones_shouldSumCorrectly() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.5, 100.0, 50));
        necklace.addGemstone(new Gemstone("Sapphire", 2.0, 150.0, 70));

        // Act
        command.execute();

        // Assert
        String result = outputArea.getText();
        assertTrue(result.contains("Загальна вага: 3.50 карат"));
        assertTrue(result.contains("Загальна ціна: 250.00"));
    }

    @Test
    void constructor_shouldInitializeFields() {
        // Assert
        assertSame(necklace, command.necklace);
        assertSame(outputArea, command.outputArea);
    }
}