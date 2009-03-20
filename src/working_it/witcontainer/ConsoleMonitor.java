package working_it.witcontainer;

/**
 * Monitor-Implemtierung
 * Gibt Meldungen auf STDOUT aus
 *
 * @author Bernd Ledig
 *
 */
public class ConsoleMonitor implements Monitor {

	/* (non-Javadoc)
	 * @see crmcontainer.Monitor#log(java.lang.String)
	 */
	public void log(String message) {
		System.out.println(message);
	}

}
