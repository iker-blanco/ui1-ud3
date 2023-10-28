package utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger {

    public static Logger getLogger(Class<?> className) {
        // Instantiate a logger for the class
        Logger logger = Logger.getLogger(className.getName());
        // Check if handlers are already added
        if (logger.getHandlers().length == 0) {
            // If not, add a console handler
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            // Set the formatter to our custom formatter
            handler.setFormatter(new CustomLogFormatter());
            logger.addHandler(handler);
            // Disable the parent handlers
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
        }
        // Return the logger with all the custom settings

        return logger;
    }
}
