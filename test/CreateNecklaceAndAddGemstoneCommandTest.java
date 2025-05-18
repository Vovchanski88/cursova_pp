import commands.CreateNecklaceAndAddGemstoneCommand;
import javafx.scene.control.TextArea;
import necklace.Necklace;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateNecklaceAndAddGemstoneCommandTest extends JavaFXTestBase {
    private CreateNecklaceAndAddGemstoneCommand command;
    private TextArea outputArea;
    private Necklace necklace;

    @BeforeEach
    public void setup() {
        outputArea = new TextArea();
        necklace = new Necklace();
        command = new CreateNecklaceAndAddGemstoneCommand(necklace, outputArea);
    }

    @Test
    @Order(1)
    public void testExecute_addOneGemstone() throws Exception {
        outputArea.clear();

        runAndWait(() -> {
            command.execute();
        });

        String output = outputArea.getText();
        assertTrue(output.contains("Намисто") || output.contains("Gemstone"));
        assertNotNull(command.getNecklace());
        assertFalse(command.getNecklace().getGemstones().isEmpty());
        assertTrue(command.getNecklace().calculateTotalWeight() > 0);
        assertTrue(command.getNecklace().calculateTotalPrice() > 0);
    }

    @Test
    @Order(2)
    public void testExecute_addMultipleGemstones_withoutUI() {
        Necklace necklace = new Necklace();
        List<String> inputs = List.of(
            "1", "3. Сапфір", "1.5", "1000", "90", // Перший камінь
            "2", "1. Бірюза", "0.8", "500", "70",   // Другий камінь
            "-1" // Завершення вводу
        );
        TestableCommand cmd = new TestableCommand(necklace, outputArea, inputs);
        cmd.execute();

        assertNotNull(necklace);
        assertEquals(2, necklace.getGemstones().size(), "Має бути 2 камені");
        assertTrue(necklace.calculateTotalWeight() > 0, "Вага має бути більшою за 0");
        assertTrue(necklace.calculateTotalPrice() > 0, "Ціна має бути більшою за 0");
    }

    @Test
    @Order(3)
    public void testValidPreciousGemstone() {
        Necklace necklace = new Necklace();
        List<String> inputs = List.of(
            "1",            // Тип каменя: дорогоцінний
            "1. Алмаз",     // Вибір: Алмаз
            "1.5", "1000", "90", // Вага, ціна, прозорість
            "-1"            // Вихід
        );
        TestableCommand cmd = new TestableCommand(necklace, outputArea, inputs);
        cmd.execute();

        assertEquals(1, necklace.getGemstones().size());
    }

    @Test
    @Order(4)
    public void testValidSemiPreciousGemstone() {
        Necklace necklace = new Necklace();
        List<String> inputs = List.of(
            "2",            // Тип: напівкоштовний
            "2. Опал",      // Вибір: Опал
            "0.8", "500", "70",
            "-1"
        );
        TestableCommand cmd = new TestableCommand(necklace, outputArea, inputs);
        cmd.execute();

        assertEquals(1, necklace.getGemstones().size());
    }

    @Test
    @Order(5)
    public void testInvalidNumberInput() {
        Necklace necklace = new Necklace();
        List<String> inputs = List.of(
            "abc",    // Неправильне число
            "-1"
        );
        TestableCommand cmd = new TestableCommand(necklace, outputArea, inputs);
        cmd.execute();

        assertTrue(necklace.getGemstones().isEmpty());
    }

    @Test
    @Order(6)
    public void testInvalidGemType() {
        Necklace necklace = new Necklace();
        List<String> inputs = List.of(
            "3",       // Немає такого типу
            "-1"
        );
        TestableCommand cmd = new TestableCommand(necklace, outputArea, inputs);
        cmd.execute();

        assertTrue(necklace.getGemstones().isEmpty());
    }

    @Test
    @Order(7)
    public void testInvalidTransparency() {
        Necklace necklace = new Necklace();
        List<String> inputs = List.of(
            "1",            // Тип
            "2. Рубін",     // Вибір
            "1.2", "500", "abc", // прозорість некоректна
            "-1"
        );
        TestableCommand cmd = new TestableCommand(necklace, outputArea, inputs);
        cmd.execute();

        assertEquals(1, necklace.getGemstones().size());
    }
}