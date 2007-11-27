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
	 * Map mit
	 * InterfaceKlasse als Key und
	 * als Values die gewueschte Implementierungsklasse 
	 * 
	 */
	private final Map<Class, Class> implMap;
	
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
	 * Erzeugt einen Ioc-Container mit default Einstellungen
	 *
	 */
	public CrmContainer() {
		super();
		 implMap = new HashMap<Class, Class>();
		 instanceMap = new HashMap<Object, Object>();
	}

	/**
	 * Erzeugt einen Ioc-Container
	 *
	 * @param initialCapacity Initial-Groesse der internen HashMap fuer Klassen/Instancen
	 */
	public CrmContainer(int initialCapacity) {
		super();
		 implMap = new HashMap<Class, Class>(initialCapacity);
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
	 * Hauptsaechlich dazu gedacht um Konstanten u binden z.B.;
	 * 
	 *   bind("PATH_TMP", "/tmp");
	 *   bind(TIMEOUT, new Integer(200));
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
		Object service = instanceMap.get(key);
		if (service!=null)
			return service;
		throw new ServiceNotBoundException(key);
	}
	
	public <T> T getInstance(Class <T> key) {
		mapLock.lock();
		T service = (T) instanceMap.get(key);
		if (service!=null) {
			mapLock.unlock();
			return service;
		}
		
		Class<T> implClass = implMap.get(key);
		if (implClass==null) {
			mapLock.unlock();
			throw new ServiceNotBoundException(key);
		}
		
		try {
			service = (T) createInstance(key, implClass);
		} catch (Exception e) {
			throw new ServiceCreationException(key, e);
		} finally {
			mapLock.unlock();
		}
		return service;
	}
	

	private <T>T createInstance(Class <T> key, Class<? extends T> clazz) throws Exception {
		if (monitor!=null) 	monitor.log("Creating service "+key+" with implementation "+ clazz);
			
		Constructor<? extends T> constr = clazz.getConstructor();
		T instance = constr.newInstance();
		instanceMap.put(key, instance);

		injectDependencies(instance);
		if (instance instanceof Startable) {
			Startable startable = (Startable) instance;
			if (monitor!=null) 	monitor.log("Call start on "+key);
			startable.start();
		}
		return instance;
	}
	
	private void injectDependencies(Object instance) throws Exception {
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
			if (annoValue.length()>0) {
				if (monitor!=null) 	monitor.log("Injecting dependencies: "+annoValue);
				param = getInstance(annoValue);
				if (param==null)
					throw new ServiceCreationException(annoValue);
			} else {
				if (monitor!=null) 	monitor.log("Injecting dependencies: "+paraClass);
				param = getInstance(paraClass);
				if (param==null)
					throw new ServiceCreationException(paraClass);
			}
			method.invoke(instance, new Object[] {param});
		}
	}

	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}
}
