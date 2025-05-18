import commands.RemoveGemstoneCommand;
import gemstones.Gemstone;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import necklace.Necklace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RemoveGemstoneCommandTest extends JavaFXTestBase {
    private Necklace necklace;
    private TextArea outputArea;
    private RemoveGemstoneCommand command;

    @BeforeEach
    public void setup() throws Exception {
        runAndWait(() -> {
            outputArea = new TextArea();
            necklace = new Necklace();
            necklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));
            necklace.addGemstone(new Gemstone("Sapphire", 1.5, 150.0, 70));
            command = new RemoveGemstoneCommand(necklace, outputArea);
        });
    }

    @Test
    @Order(1)
    public void testExecute_removeExistingGemstone() throws Exception {
        runAndWait(() -> {
            TextInputDialog dialog = new TextInputDialog("1"); // ID першого каменя
            command.setDialog(dialog);
            command.execute();
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Камінь з ID 1 був видалений"));
        assertEquals(1, necklace.getGemstones().size());
    }

    @Test
    @Order(2)
    public void testExecute_removeNonExistingGemstone() throws Exception {
        runAndWait(() -> {
            TextInputDialog dialog = new TextInputDialog("999"); // Неіснуючий ID
            command.setDialog(dialog);
            command.execute();
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Камінь з ID 999 не знайдено"));
        assertEquals(2, necklace.getGemstones().size());
    }

    @Test
    @Order(3)
    public void testSetDialog() throws Exception {
        runAndWait(() -> {
            TextInputDialog dialog = new TextInputDialog();
            command.setDialog(dialog);
        });

        assertNotNull(command.inputDialog);
    }
}