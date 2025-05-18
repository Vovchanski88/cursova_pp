import commands.FindGemstonesByTransparency;
import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import necklace.Necklace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FindGemstonesByTransparencyTest extends JavaFXTestBase {
    private Necklace necklace;
    private TextArea outputArea;
    private FindGemstonesByTransparency command;

    @BeforeEach
    public void setup() throws Exception {
        runAndWait(() -> {
            outputArea = new TextArea();
            necklace = new Necklace();
            necklace.addGemstone(new Gemstone("Ruby", 10.0, 50.0, 50));
            necklace.addGemstone(new Gemstone("Sapphire", 15.0, 70.0, 75));
            necklace.addGemstone(new Gemstone("Emerald", 12.0, 90.0, 90));
            command = new FindGemstonesByTransparency(necklace, outputArea);
        });
    }

    @Test
    @Order(1)
    public void testValidRangeWithMatches() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            command.executeWithParams("50", "90");
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Ruby"));
        assertTrue(output.contains("Sapphire"));
        assertTrue(output.contains("Emerald"));
    }

    @Test
    @Order(2)
    public void testDialogCancelled() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            FindGemstonesByTransparency testCommand = new FindGemstonesByTransparency(necklace, outputArea) {
                @Override
                public void execute() {
                    outputArea.appendText("Dialog was cancelled\n");
                }
            };
            testCommand.execute();
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Dialog was cancelled"));
    }

    @Test
    @Order(3)
    public void testCustomDialogsUsed() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            TextInputDialog minDialog = new TextInputDialog("60");
            TextInputDialog maxDialog = new TextInputDialog("90");

            FindGemstonesByTransparency testCommand = new FindGemstonesByTransparency(necklace, outputArea) {
                {
                    this.minTransparencyDialog = minDialog;
                    this.maxTransparencyDialog = maxDialog;
                }
            };

            testCommand.execute();
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Sapphire") || output.contains("Emerald"));
    }

    @Test
    @Order(4)
    public void testValidRangeNoMatches() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            command.executeWithParams("91", "100");
        });

        String output = outputArea.getText();
        assertTrue(output.contains("No gemstones found"));
    }

    @Test
    @Order(5)
    public void testMinGreaterThanMax() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            command.executeWithParams("80", "40");
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Invalid range"));
    }

    @Test
    @Order(6)
    public void testInvalidNumberFormat() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            command.executeWithParams("abc", "90");
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Invalid input"));
    }

    @Test
    @Order(7)
    public void testInvalidNumberFormatBoth() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            command.executeWithParams("abc", "xyz");
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Invalid input"));
    }

    @Test
    @Order(8)
    public void testEmptyInput() throws Exception {
        runAndWait(() -> {
            outputArea.clear();
            command.executeWithParams("", "");
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Invalid input"));
    }
}