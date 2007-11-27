package crmcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sehr schnelle und minimalistische IoC -Container-Implementierung
 * basierend auf Injection per Annotations.
 * Derzeit gelten folgende Praemisse:
 *  - Nur Setter-Injection mit nur einem Parameter
 *  - erzeugte Instancen sind Singletons 
 *
 * thread-sicher im Bereich der Erzeugung (sprich getInstance).
 * Alle bind-Aufrufe sind nicht thread-sicher, da davon ausgegangen wird,
 * dass das Binden von einen einzelnen Prozess gemacht wird.
 *
 * @author Bernd Ledig, Thorsten Fehre
 *
 */
public class CrmContainer {
	
	/** 
	 * Map mit der zum jweiligen key
	 * gewueschten Implementierungsklasse 
	 * 
	 */
	private final Map<Object, Class> implMap;
	
	/**
	 * Map mit den schon erzeugten konkreten Instancen
	 */
	private final Map<Object, Object> instanceMap;
	
	/**
	 * optionaler Monitor, welcher die Erzuegung protokolliert
	 */
	private Monitor monitor;
	
	/**
	 * Lock fuer das thread-sichere Erzeugen der Instancen 
	 */
	private final Lock mapLock = new ReentrantLock();
	

	/**
	 * Erzeugt einen IoC-Container mit default Einstellungen
	 *
	 */
	public CrmContainer() {
		super();
		 implMap = new HashMap<Object, Class>();
		 instanceMap = new HashMap<Object, Object>();
	}

	/**
	 * Erzeugt einen IoC-Container
	 *
	 * @param initialCapacity Initial-Groesse der internen HashMap fuer Klassen/Instancen
	 */
	public CrmContainer(int initialCapacity) {
		super();
		 implMap = new HashMap<Object, Class>(initialCapacity);
		 instanceMap = new HashMap<Object, Object>(initialCapacity);
	}

	/**
	 * Binden eines Interfaces an eine Implementierung
	 *
	 * @param <T>	die gewueschte Ergebnisklasse
	 * @param key Interface-Klasse, wird Key zum Auffinden der Implementierung
	 * @param impl zugehoerige Implementierungsklasse
	 */
	public <T>void bind(Class<T> key, Class<? extends T> impl) {
		if(implMap.containsKey(key) || instanceMap.containsKey(key))
			throw new DuplicateBindException(key);
		implMap.put(key, impl);
	}
	
	/**
	 * Binden  einer Implementierungs-Klasse per Object-Key
	 *
	 * @param key  zum Auffinden der Implementierung
	 * @param impl zugehoerige Implementierungsklasse
	 */
	public void bind(Object key, Class impl) {
		if(implMap.containsKey(key) || instanceMap.containsKey(key))
			throw new DuplicateBindException(key);
		implMap.put(key, impl);
	}
	
	/**
	 * Binden einer einzelnen Implementierungsklasse,
	 * also einer Klasse zu der es kein Interface gibt.
	 *
	 * @param <T> die gewueschte Ergebnisklasse
	 * @param key zugehoerige Implementierungsklasse, wird gleichzeitig Key zum Auffinden der Implementierung
	 */
	public <T>void bind(Class<T> key) {
		bind(key, key);
	}
	
	/**
	 * Binden einer schon existieren konkreten Object-Instance unter eine Key.
	 * Hauptsaechlich dazu gedacht um Konstanten u.a. zu binden z.B.;
	 * <code>
	 *   bind("PATH_TMP", "/tmp");
	 *   bind(TIMEOUT, new Integer(200));
	 * </code>
	 * 
	 * @param key Key unter dem die Instance zu finden ist, darf nicht schon vergeben sein
	 * @param instance zu bindende konkrete Instance
	 */
	public void bind(Object key, Object instance) {
		if(instanceMap.containsKey(key))
			throw new DuplicateBindException(key);
		instanceMap.put(key, instance);
	}
	
	/**
	 * liefert zum angegebenen Key die konkrete instancierte
	 * Object-Instance
	 *
	 * @param key
	 * @return
	 */
	public Object getInstance(Object key) {
		Object instance = instanceMap.get(key);
		if (instance!=null)
			return instance;
		
		return getOrCreateInstance(key, 0);
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
	public <T> T getInstance(Class <T> key) {
		T instance = (T) getOrCreateInstance(key, 0);
		return instance;
	}
	
	/**
	 * liefert oder wenn nicht vorhanden
	 * erzeugt Instance-Object zum angegebenen key
	 *
	 * @param key
	 * @return
	 */
	private Object getOrCreateInstance(Object key, int level) {
		mapLock.lock();
		Object service = instanceMap.get(key);
		if (service!=null) {
			//	 es gibt also schon eine Instance zu dem key, dann also fertig
			mapLock.unlock();
			return service;	
		}
		
		Class implClass = implMap.get(key);
		if (implClass==null) {
			// es existiert fuer diesen key keine Implementierungsklasse,
			// raus mit Exception
			mapLock.unlock();
			throw new ServiceNotBoundException(key);
		}
		
		try {
			service = createInstance(key, implClass, level);
		} catch (Exception e) {
			throw new ServiceCreationException(key, e);
		} finally {
			mapLock.unlock();
		}
		return service;
	}

	/**
	 * erzeugt zum angebenen Key eine konkrete Instance
	 *
	 * @param key
	 * @param clazz
	 * @param level Aufruf-Level (nur fuer Monitorzwecke)
	 * @return
	 * @throws Exception
	 */
	private Object createInstance(Object  key, Class clazz, int level) throws Exception {
		if (monitor!=null) 	monitor.log(levelPrefix(level)+"Creating instance for key="+key+" with implementation "+ clazz);
			
		Constructor constr = clazz.getConstructor();
		Object instance = constr.newInstance();
		instanceMap.put(key, instance);

		injectDependencies(instance, level);
		if (instance instanceof Startable) {
			Startable startable = (Startable) instance;
			if (monitor!=null) 	monitor.log("Call start on "+key);
			startable.start();
		}
		return instance;
	}
	
	/**
	 * Injecten aller Abhaengikeiten (Setter), die durch die
	 * Annotation @Inject markiert sind
	 *
	 * @param instance
	 * @param level Aufruf-Level (nur fuer Monitorzwecke)
	 * @throws Exception
	 */
	private void injectDependencies(Object instance, int level) throws Exception {
		Class clazz = instance.getClass();
		for (Method method : clazz.getMethods()) {
			Inject anno = method.getAnnotation(Inject.class);
			if (anno==null)
				continue;
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length!=1) {
				throw new ServiceCreationException(clazz, method);
			}
			Class<?> paraClass = parameterTypes[0];
			Object param;
			String annoValue = anno.value();
			++level;
			if (annoValue.length()>0) {
				if (monitor!=null) 	monitor.log(levelPrefix(level)+"Injecting dependencies: "+annoValue);
				param = getOrCreateInstance(annoValue, level);
				if (param==null)
					throw new ServiceCreationException(annoValue);
			} else {
				if (monitor!=null) 	monitor.log(levelPrefix(level)+"Injecting dependencies: "+paraClass);
				param = getOrCreateInstance(paraClass, level);
				if (param==null)
					throw new ServiceCreationException(paraClass);
			}
			method.invoke(instance, new Object[] {param});
		}
	}

	private String levelPrefix(int level) {
		StringBuffer sb = new StringBuffer();
		while(--level>=0) sb.append("-");
			
		return "#"+sb+"# ";
	}

	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}
}
