package crmcontainer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Monitor-Implementierung per Java-Logger
 *
 * @author Bernd Ledig
 *
 */
public class LoggerMonitor implements Monitor {
	
	private static Logger log = Logger.getLogger(LoggerMonitor.class.getCanonicalName());
	private final Level level;

	/**
	 * Konstruktor mit default Log-Level=Info
	 */
	public LoggerMonitor() {
		super();
		level = Level.INFO;
	}



	/**
	 * Konstruktor mit Vorgabe des Log-Levels
	 *
	 * @param level Log-Level der Log-Meldungen
	 */
	public LoggerMonitor(Level level) {
		super();
		this.level = level;
	}




	/* (non-Javadoc)
	 * @see crmcontainer.Monitor#log(java.lang.String)
	 */
	public void log(String message) {
		log.log(level , message);
	}

}
