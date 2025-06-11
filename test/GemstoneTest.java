import org.junit.jupiter.api.Test;
import gemstones.Gemstone;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class GemstoneTest {
    @Test
    void testGemstoneProperties() {
        Gemstone gem = new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0);

        assertEquals("Дорогоцінний", gem.getType());
        assertEquals("Diamond", gem.getName());
        assertEquals(1.0, gem.getWeight());
        assertEquals(100.0, gem.getPrice());
        assertEquals(80.0, gem.getTransparency());

        gem.setId(1);
        gem.setType("Напівкоштовний");
        gem.setName("Ruby");
        gem.setWeight(0.8);
        gem.setPrice(80.0);
        gem.setTransparency(70.0);

        assertEquals(1, gem.getId());
        assertEquals("Напівкоштовний", gem.getType());
        assertEquals("Ruby", gem.getName());
        assertEquals(0.8, gem.getWeight());
        assertEquals(80.0, gem.getPrice());
        assertEquals(70.0, gem.getTransparency());
    }

    @Test
    void testToString() {
        Locale.setDefault(Locale.US); // Встановлюємо локаль для тесту
        Gemstone gem = new Gemstone("Дорогоцінний", "Sapphire", 1.2, 120.0, 90.0);
        String expected = "Sapphire (1.20 карат, 120.00 грн, 90.00%)";
        assertEquals(expected, gem.toString());
    }
}