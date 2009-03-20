package working_it.witcontainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Sehr schnelle und minimalistische IoC -Container-Implementierung
 * basierend auf Injection per Annotations.
 * Derzeit gelten folgende Praemisse:
 *  - Nur Setter-Injection mit nur einem Parameter
 *  - erzeugte Instancen sind Singletons 
 *  - Unterstuetzt Provider
 *  
 * thread-sicher im Bereich der Erzeugung (sprich getInstance).
 * Alle bind-Aufrufe sind nicht thread-sicher, da davon ausgegangen wird,
 * dass das Binden von einen einzelnen Prozess gemacht wird.
 *
 * @author Bernd Ledig, Thorsten Fehre
 *
 */
public class WitContainer {
	
	/**  Map mit den BindObjects   */
	private final Map<Object, BindObject> bindObjects;
	
	
	/**
	 * optionaler Monitor, welcher die Erzeugung protokolliert
	 */
	private Monitor monitor;
	

	/**
	 * Erzeugt einen IoC-Container mit default Einstellungen
	 *
	 */
	public WitContainer() {
		super();
		bindObjects = new HashMap<Object, BindObject>();
	}

	/**
	 * Erzeugt einen IoC-Container
	 *
	 * @param initialCapacity Initial-Groesse der internen HashMap fuer Klassen/Instancen
	 */
	public WitContainer(int initialCapacity) {
		super();
		bindObjects = new HashMap<Object, BindObject>(initialCapacity);
	}

	/**
	 * Binden  einer Implementierungs-Klasse per Object-Key
	 *
	 * @param key  zum Auffinden der Implementierung
	 */
	public BindObject bind(Object key) {
		if(bindObjects.containsKey(key))
			throw new DuplicateBindException(key);
		BindObject bindObject = new BindObject(key, this);
		bindObjects.put(key, bindObject);
		return bindObject;
	}
	
	/**
	 * Binden einer einzelnen Implementierungsklasse,
	 * z.B. einer Klasse zu der es kein Interface gibt.
	 * 
	 * @param implClass zugehoerige Implementierungsklasse, wird gleichzeitig Key zum Auffinden der Implementierung
	 */
	@SuppressWarnings("unchecked")
	public BindObject bind(Class implClass) {
		BindObject bindObject = bind((Object) implClass);
		bindObject.to(implClass);
		return bindObject;
	}
	
	
	/**
	 * liefert zum angegebenen Key die konkrete instancierte
	 * Object-Instance
	 *
	 * @param key
	 * @return
	 */
	public Object getInstance(Object key) {
		return getInstance(key, 0);
	}

	/**
	 * liefert zur angegebenen Interface-Klasse 
	 * eine Instance der zugehoerigen Implementierung.
	 * wobei die Interface-Klasse auch die Implementierungsklasse sein darf,
	 * wenn z.B. dies kein Inteface hat.
	 *
	 * @param <T> Type der Interface-Klasse
	 * @param key die Klasse als Key fuer die zugehoerige Instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class <T> key) {
		T instance = (T) getInstance((Object) key);
		return instance;
	}

	/**
	 * liefert zum angegebenen Key die konkrete instancierte
	 * Object-Instance
	 *
	 * @param key
	 * @return
	 */
	protected Object getInstance(Object key, int level) {
		BindObject bindObject = bindObjects.get(key);
		if(bindObject==null) {
			throw new ServiceNotBoundException(key);
		}
		return bindObject.getInstance(level);
	}


	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public Monitor getMonitor() {
		return monitor;
	}
}
