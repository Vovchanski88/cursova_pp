import gemstones.Gemstone;
import necklace.Necklace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NecklaceTest {
    private Necklace necklace;

    @BeforeEach
    void setUp() {
        necklace = new Necklace();
    }

    @Test
    void testAddAndGetGemstones() {
        Gemstone gem1 = new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0);
        Gemstone gem2 = new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0);

        necklace.addGemstone(gem1);
        necklace.addGemstone(gem2);

        List<Gemstone> gems = necklace.getGemstones();
        assertEquals(2, gems.size());
        assertSame(gem1, gems.get(0));
        assertSame(gem2, gems.get(1));
    }

    @Test
    void testCalculateTotalWeight() {
        necklace.addGemstone(new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0));
        necklace.addGemstone(new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0));

        assertEquals(1.8, necklace.calculateTotalWeight(), 0.001);
    }

    @Test
    void testCalculateTotalPrice() {
        necklace.addGemstone(new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0));
        necklace.addGemstone(new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0));

        assertEquals(150.0, necklace.calculateTotalPrice(), 0.001);
    }

    @Test
    void testGetSortedByTransparency() {
        Gemstone gem1 = new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0);
        Gemstone gem2 = new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0);
        Gemstone gem3 = new Gemstone("Дорогоцінний", "Sapphire", 1.2, 120.0, 90.0);

        necklace.addGemstone(gem1);
        necklace.addGemstone(gem2);
        necklace.addGemstone(gem3);

        List<Gemstone> sorted = necklace.getSortedByTransparency();
        assertEquals(3, sorted.size());
        assertSame(gem2, sorted.get(0)); // 70%
        assertSame(gem1, sorted.get(1)); // 80%
        assertSame(gem3, sorted.get(2)); // 90%
    }

    @Test
    void testRemoveGemstone() {
        Gemstone gem1 = new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0);
        gem1.setId(1);
        Gemstone gem2 = new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0);
        gem2.setId(2);

        necklace.addGemstone(gem1);
        necklace.addGemstone(gem2);

        assertTrue(necklace.removeGemstone(1));
        assertEquals(1, necklace.getGemstones().size());
        assertSame(gem2, necklace.getGemstones().get(0));

        assertFalse(necklace.removeGemstone(3));
        assertEquals(1, necklace.getGemstones().size());
    }

    @Test
    void testFindGemstonesByTransparency() {
        necklace.addGemstone(new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0));
        necklace.addGemstone(new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0));
        necklace.addGemstone(new Gemstone("Дорогоцінний", "Sapphire", 1.2, 120.0, 90.0));

        List<Gemstone> result = necklace.findGemstonesByTransparency(75.0, 85.0);
        assertEquals(1, result.size());
        assertEquals("Diamond", result.get(0).getName());

        result = necklace.findGemstonesByTransparency(70.0, 90.0);
        assertEquals(3, result.size());

        result = necklace.findGemstonesByTransparency(95.0, 100.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToString() {
        necklace.addGemstone(new Gemstone("Дорогоцінний", "Diamond", 1.0, 100.0, 80.0));
        necklace.addGemstone(new Gemstone("Напівкоштовний", "Amethyst", 0.8, 50.0, 70.0));

        String result = necklace.toString();
        assertTrue(result.contains("Намисто (ID: 0):"));
        assertTrue(result.contains("Diamond (1.00 карат, 100.00 грн, 80.00%)"));
        assertTrue(result.contains("Amethyst (0.80 карат, 50.00 грн, 70.00%)"));
    }

    @Test
    void testIdManagement() {
        assertEquals(0, necklace.getId());
        necklace.setId(5);
        assertEquals(5, necklace.getId());
    }
}