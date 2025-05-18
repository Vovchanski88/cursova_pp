package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    private static final Logger logger = LogManager.getLogger(LoggerUtil.class);
    public static LoggerUtil instance;

    static {
        System.out.println("LoggerUtil initialized. Log4j2 available: " +
            (logger != null ? "YES" : "NO"));
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void logDebug(String message) {
        logger.debug(message);
    }

    public static void logWarning(String message) {
        logger.warn(message);
    }
}