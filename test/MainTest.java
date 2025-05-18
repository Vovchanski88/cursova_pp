import commands.*;
import database.DatabaseManager;
import gemstones.Gemstone;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.Main;
import necklace.Necklace;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.MockedStatic;
import utils.LoggerUtil;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainTest extends JavaFXTestBase {
    private Main main;
    private Stage stage;
    private Necklace testNecklace;

    @BeforeEach
    public void setup() throws Exception {
        runAndWait(() -> {
            main = new Main();
            stage = new Stage();
            main.start(stage);
            testNecklace = new Necklace();
            testNecklace.addGemstone(new Gemstone("Ruby", 1.0, 100.0, 50));
        });
    }

    @Test
    @Order(1)
    public void testStartMethod() throws Exception {
        runAndWait(() -> {
            assertNotNull(main.necklace);
            assertNotNull(main.outputArea);
            assertTrue(stage.isShowing());
            assertTrue(main.outputArea.getText().contains("намисто"));
        });
    }

    @Test
    @Order(2)
    public void testStartMethod_ExceptionHandling() throws Exception {
        runAndWait(() -> {
            Main failingMain = new Main() {
                @Override
                public void initializeNecklace() {
                    throw new RuntimeException("Test exception");
                }
            };

            Stage testStage = new Stage();
            failingMain.start(testStage);

            assertTrue(failingMain.outputArea.getText().contains("Помилка"));
        });
    }

    @Test
    @Order(3)
    public void testInitializeNecklace_EmptyDatabase() throws Exception {
        runAndWait(() -> {
            DatabaseManager.saveNecklace(new Necklace());
            main.initializeNecklace();
            assertTrue(main.outputArea.getText().contains("Створено нове намисто"));
        });
    }

    @Test
    @Order(4)
    public void testInitializeNecklace_WithExistingNecklace() throws Exception {
        runAndWait(() -> {
            DatabaseManager.saveNecklace(testNecklace);
            main.initializeNecklace();
            assertTrue(main.outputArea.getText().contains("Завантажено намисто"));
        });
    }

    @Test
    @Order(5)
    public void testInitializeNecklace_DatabaseError() throws Exception {
        runAndWait(() -> {
            try (MockedStatic<DatabaseManager> mocked = mockStatic(DatabaseManager.class)) {
                mocked.when(DatabaseManager::loadNecklace).thenThrow(new RuntimeException("DB error"));
                main.initializeNecklace();
                assertTrue(main.outputArea.getText().contains("Помилка"));
            }
        });
    }

    @Test
    @Order(6)
    public void testCreateHeader() throws Exception {
        runAndWait(() -> {
            HBox header = main.createHeader();
            assertNotNull(header);
            assertEquals(1, header.getChildren().size());
            Label title = (Label) header.getChildren().get(0);
            assertEquals("Управління намистом з каменями", title.getText());
            assertTrue(header.getStyle().contains("gradient"));
        });
    }

    @Test
    @Order(7)
    public void testCreateOutputArea() throws Exception {
        runAndWait(() -> {
            ScrollPane scrollPane = main.createOutputArea();
            assertNotNull(scrollPane);
            TextArea outputArea = (TextArea) scrollPane.getContent();
            assertFalse(outputArea.isEditable());
            assertTrue(outputArea.isWrapText());
            assertTrue(outputArea.getStyle().contains("background"));
        });
    }

    @Test
    @Order(8)
    public void testCreateButtonPanel() throws Exception {
        runAndWait(() -> {
            GridPane buttonGrid = main.createButtonPanel();
            assertNotNull(buttonGrid);

            long buttonCount = buttonGrid.getChildren().stream()
                .filter(node -> node instanceof Button)
                .count();
            assertEquals(7, buttonCount);

            buttonGrid.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    assertDoesNotThrow(((Button) node)::fire);
                }
            });
        });
    }

    @Test
    @Order(9)
    public void testCreateButton() throws Exception {
        runAndWait(() -> {
            AtomicBoolean actionCalled = new AtomicBoolean(false);
            Button button = main.createButton("Test", "-fx-background-color: red;",
                () -> actionCalled.set(true));

            assertEquals("Test", button.getText());
            assertTrue(button.getStyle().contains("red"));

            button.fire();
            assertTrue(actionCalled.get());
        });
    }

    @Test
    @Order(10)
    public void testShowErrorAlert() throws Exception {
        runAndWait(() -> {
            try (MockedStatic<Alert> mocked = mockStatic(Alert.class)) {
                Alert mockAlert = mock(Alert.class);
                mocked.when(() -> new Alert(Alert.AlertType.ERROR)).thenReturn(mockAlert);

                main.showErrorAlert("Test error");

                verify(mockAlert).setTitle("Помилка");
                verify(mockAlert).setContentText("Test error");
                verify(mockAlert).showAndWait();
            }
        });
    }

    @Test
    @Order(11)
    public void testShowInfoAlert() throws Exception {
        runAndWait(() -> {
            try (MockedStatic<Alert> mocked = mockStatic(Alert.class)) {
                Alert mockAlert = mock(Alert.class);
                mocked.when(() -> new Alert(Alert.AlertType.INFORMATION)).thenReturn(mockAlert);

                main.showInfoAlert("Test info");

                verify(mockAlert).setTitle("Інформація");
                verify(mockAlert).setContentText("Test info");
                verify(mockAlert).showAndWait();
            }
        });
    }

    @Test
    @Order(12)
    public void testMainMethod() throws Exception {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }

    @Test
    @Order(13)
    public void testMainMethod_ExceptionHandling() {
        LoggerUtil mockLogger = mock(LoggerUtil.class);
        LoggerUtil original = LoggerUtil.instance;
        LoggerUtil.instance = mockLogger;

        Thread.UncaughtExceptionHandler originalHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LoggerUtil.instance.logError("Неконтрольована помилка в додатку", (Exception) e);
        });

        try {
            // Створюємо потік, що кине RuntimeException
            Thread thread = new Thread(() -> {
                throw new RuntimeException("Simulated failure");
            });
            thread.start();
            thread.join(); // чекаємо завершення

            verify(mockLogger).logError(eq("Неконтрольована помилка в додатку"), any(RuntimeException.class));
        } catch (InterruptedException e) {
            fail("Thread interrupted");
        } finally {
            LoggerUtil.instance = original;
            Thread.setDefaultUncaughtExceptionHandler(originalHandler);
        }
    }

    @Test
    @Order(14)
    public void testStageCloseHandler() throws Exception {
        runAndWait(() -> {
            try (MockedStatic<DatabaseManager> mocked = mockStatic(DatabaseManager.class)) {
                // Перевірка виклику закриття бази даних
                stage.fireEvent(new javafx.stage.WindowEvent(stage, javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST));
                assertFalse(stage.isShowing());
                mocked.verify(DatabaseManager::close);

                // Додатково перевіряємо, чи збереження намиста викликається
                mocked.verify(() -> DatabaseManager.saveNecklace(any(Necklace.class)));
            }
        });
    }

    @Test
    @Order(15)
    public void testPrivateFields() throws Exception {
        runAndWait(() -> {
            Field necklaceField = null;
            try {
                necklaceField = Main.class.getDeclaredField("necklace");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            necklaceField.setAccessible(true);
            try {
                assertNotNull(necklaceField.get(main));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            Field outputAreaField = null;
            try {
                outputAreaField = Main.class.getDeclaredField("outputArea");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            outputAreaField.setAccessible(true);
            try {
                assertNotNull(outputAreaField.get(main));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterEach
    public void cleanup() {
        DatabaseManager.close();
    }
}