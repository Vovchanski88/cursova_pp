import utils.LoggerUtil;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LoggerUtilTest {
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Logger logger = (Logger) context.getLogger(LoggerUtil.class);

        // Видалити всі апендери
        logger.getAppenders().forEach((name, appender) -> logger.removeAppender(appender));

        PatternLayout layout = PatternLayout.newBuilder()
            .withPattern("%level %msg%n")
            .build();

        OutputStreamAppender appender = OutputStreamAppender.newBuilder()
            .setName("TEST")
            .setTarget(printStream)
            .setLayout(layout)
            .build();
        appender.start();

        logger.addAppender(appender);
        logger.setLevel(Level.ALL);
    }


    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testLogInfo() {
        LoggerUtil.logInfo("Тестове інформаційне повідомлення");
        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("INFO Тестове інформаційне повідомлення"));
    }

    @Test
    void testLogError() {
        Exception exception = new RuntimeException("Тестовий виняток");
        LoggerUtil.logError("Тестове повідомлення про помилку", exception);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("ERROR Тестове повідомлення про помилку"));
        assertTrue(output.contains("RuntimeException: Тестовий виняток"));
    }

    @Test
    void testLogDebug() {
        LoggerUtil.logDebug("Тестове повідомлення налагодження");
        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("DEBUG Тестове повідомлення налагодження"));
    }

    @Test
    void testLogWarning() {
        LoggerUtil.logWarning("Тестове попереджувальне повідомлення");
        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("WARN Тестове попереджувальне повідомлення"));
    }
}