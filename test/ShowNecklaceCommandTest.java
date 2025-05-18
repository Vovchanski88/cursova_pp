import commands.ShowNecklaceCommand;
import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import necklace.Necklace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowNecklaceCommandTest extends JavaFXTestBase {
    private Necklace necklace;
    private TextArea outputArea;
    private ShowNecklaceCommand command;

    @BeforeEach
    void setUp() {
        necklace = new Necklace();
        outputArea = new TextArea(); // Тепер працюватиме, бо JavaFX ініціалізовано
        command = new ShowNecklaceCommand(necklace, outputArea);
    }

    @Test
    void execute_withEmptyNecklace_shouldShowEmptyMessage() {
        // Act
        command.execute();
        necklace = new Necklace();
        // Assert
        assertTrue(outputArea.getText().contains("Намисто порожнє") ||
                 outputArea.getText().contains("Necklace is empty"));
    }

    @Test
    void execute_withSingleGemstone_shouldShowCorrectOutput() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));

        // Act
        command.execute();

        // Assert
        String output = outputArea.getText();
        assertTrue(output.contains("Ruby"));
        assertTrue(output.contains("1.0"));
        assertTrue(output.contains("100.0"));
        assertTrue(output.contains("50"));
    }

    @Test
    void execute_withMultipleGemstones_shouldShowAllGemstones() {
        // Arrange
        necklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));
        necklace.addGemstone(new Gemstone("Sapphire", 1.5, 150.0, 70));

        // Act
        command.execute();

        // Assert
        String output = outputArea.getText();
        assertTrue(output.contains("Ruby"));
        assertTrue(output.contains("Sapphire"));
        assertTrue(output.split("\n").length >= 2); // Принаймні 2 рядки
    }

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        // Act & Assert
        assertNotNull(command);
        assertSame(necklace, command.necklace);
        assertSame(outputArea, command.outputArea);
    }
}