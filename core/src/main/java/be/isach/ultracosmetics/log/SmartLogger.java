package be.isach.ultracosmetics.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a smart logger.
 *
 * @author iSach
 * @since 05-15-2016
 */
public class SmartLogger {

    public enum LogLevel {
        INFO(Level.INFO),
        WARNING(Level.WARNING),
        ERROR(Level.SEVERE);
    	private Level level;
    	private LogLevel(Level level) {
    		this.level = level;
    	}
    }
    private Logger logger;
    public SmartLogger(Logger logger) {
    	this.logger = logger;
    }

    public void write(LogLevel logLevel, Object... objects) {
    	Level level = logLevel.level;
        if (objects.length == 0) {
            logger.log(level, "");
            return;
        }
        for (Object object : objects) {
            logger.log(level, object.toString());
        }
    }

    public void write(Object... objects) {
        write(LogLevel.INFO, objects);
    }
}
