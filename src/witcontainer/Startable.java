package witcontainer;

/**
 * Klassen, die dieses Interface implementieren, stellen eine start-Methode bereit, die nach Erzeugung und Injektion
 * der Dependencies aufgerufen wird
 *
 *
 * @author Bernd Ledig
 *
 */
public interface Startable {

	/**
	 * Called after all injections
	 *
	 */
	public void start();
}
