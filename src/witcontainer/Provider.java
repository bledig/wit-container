package witcontainer;

/**
 * Interface fuer Implementierungs-Klassen,
 * die als Provider agieren.
 * D.h. diese Klassen liefer beim Instanzieren nicht eine
 * Instance von sich selbst, sondern eine Instanz des Provider-Objects
 *
 * @author Bernd Ledig
 *
 */
public interface Provider<P> {

	/**
	 * liefert Instanz des gewuenschten Objectes
	 *
	 * @return
	 */
	public P get();
}
