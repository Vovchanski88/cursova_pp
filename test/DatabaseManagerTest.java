import database.DatabaseManager;
import gemstones.Gemstone;
import necklace.Necklace;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseManagerTest {

    private Necklace testNecklace;

    @BeforeEach
    public void setUp() {
        testNecklace = new Necklace();
        testNecklace.addGemstone(new Gemstone("Sapphire", 1.5, 200.0, 80));
    }

    @Test
    @Order(1)
    public void testGetConnection_Success() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        assertNotNull(conn);
        assertDoesNotThrow(() -> conn.close());
    }

    @Test
    @Order(2)
    public void testSaveNecklace_Success() {
        assertDoesNotThrow(() -> DatabaseManager.saveNecklace(testNecklace));
    }

    @Test
    @Order(3)
    public void testSaveNecklace_NullInput() {
        assertDoesNotThrow(() -> DatabaseManager.saveNecklace(null));
    }

    @Test
    @Order(4)
    public void testSaveNecklace_ExceptionHandling() {
        try (MockedStatic<DatabaseManager> mocked = mockStatic(DatabaseManager.class, CALLS_REAL_METHODS)) {
            mocked.when(DatabaseManager::getConnection)
                    .thenThrow(new RuntimeException("Simulated DB failure"));

            assertThrows(RuntimeException.class, () -> DatabaseManager.saveNecklace(testNecklace));
        }
    }

    @Test
    @Order(5)
    public void testLoadNecklace_Success() {
        DatabaseManager.saveNecklace(testNecklace);
        Necklace loaded = DatabaseManager.loadNecklace();

        assertNotNull(loaded);
        assertEquals(1, loaded.getGemstones().size());
        assertEquals("Sapphire", loaded.getGemstones().get(0).getName());
    }

    @Test
    @Order(6)
    public void testLoadNecklace_EmptyDatabase() {
        DatabaseManager.saveNecklace(new Necklace());
        Necklace loaded = DatabaseManager.loadNecklace();

        assertNotNull(loaded);
        assertNotNull(loaded.getGemstones());
        assertTrue(loaded.getGemstones().isEmpty());
    }

    @Test
    @Order(7)
    public void testLoadNecklace_ExceptionHandling() {
        try (MockedStatic<DatabaseManager> mocked = mockStatic(DatabaseManager.class, CALLS_REAL_METHODS)) {
            mocked.when(DatabaseManager::getConnection)
                    .thenThrow(new RuntimeException("Simulated failure"));
            assertThrows(RuntimeException.class, DatabaseManager::loadNecklace);
        }
    }

    @Test
    @Order(8)
    public void testClose() {
        assertDoesNotThrow(DatabaseManager::close);
    }
}
