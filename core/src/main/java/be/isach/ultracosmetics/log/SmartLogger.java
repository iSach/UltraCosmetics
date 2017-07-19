package be.isach.ultracosmetics.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a smart logger.
 *
 * @author iSach
 * @since 05-15-2016
 */
public class SmartLogger {

	private enum LogLevel {
		INFO(Level.INFO),
		WARNING(Level.SEVERE),
		ERROR(Level.WARNING);

		LogLevel(Level level) {
			realLevel = level;
		}

		private Level realLevel;

		public Level getRealLevel() {
			return realLevel;
		}
	}

	private Logger log;

	public SmartLogger(Logger log) {
		this.log = log;
	}

	private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");


	public void write(LogLevel logLevel, Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			log.log(logLevel.getRealLevel(), objects[i].toString());
		}
	}
	public void write(Object... objects) {
		write(LogLevel.INFO, objects);
	}
}
