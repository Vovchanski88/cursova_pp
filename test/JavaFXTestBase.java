import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFXTestBase {
    private static final AtomicBoolean javafxInitialized = new AtomicBoolean(false);
    private static final CountDownLatch latch = new CountDownLatch(1);

    @BeforeAll
    public static void initJFX() throws InterruptedException {
        if (!javafxInitialized.getAndSet(true)) {
            Thread t = new Thread(() -> Application.launch(JavaFXTestApp.class));
            t.setDaemon(true);
            t.start();
            latch.await(5, TimeUnit.SECONDS);
        }
    }

    public static class JavaFXTestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            latch.countDown();
            primaryStage.hide();
        }
    }

    public static void runAndWait(Runnable action) throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            CountDownLatch fxLatch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    action.run();
                } finally {
                    fxLatch.countDown();
                }
            });
            fxLatch.await(60, TimeUnit.SECONDS);
        }
    }
}