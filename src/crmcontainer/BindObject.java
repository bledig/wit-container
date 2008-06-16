package crmcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Internes Object zum Speicheren der
 * Zuordnung Key zur Impl-Kalsse,
 * evetuell schon existierenen Instance und ob es als Provider arbeitet
 *
 * @author Bernd Ledig
 *
 */
public class BindObject {

	private final Monitor monitor;
	private final CrmContainer crmContainer;
	
	private final Object key;
	
	private Class implClass;
	private Object instance;
	private Object providerKey;
	
	/**
	 * Lock fuer das thread-sichere Erzeugen der Instancen 
	 */
	private final Lock createLock = new ReentrantLock();
	
	
	/**
	 * Konstruktor
	 * 
	 * @param key key, unter dem dieses BindObject im bindOjectSpace abgeleget ist 
	 * @param crmContainer der uebergeordnete CrmContainer
	 */
	public BindObject(Object key, CrmContainer crmContainer) {
		super();
		this.key = key;
		this.crmContainer = crmContainer;
		this.monitor = crmContainer.getMonitor();
	}

	
	public void to(Class implClass) {
		this.implClass = implClass;
	}
	
	public void to(Object instance) {
		this.instance = instance;
	}


	public void toProvider(Object providerKey) {
		this.providerKey = providerKey;
	}


	public Object getInstance(int level) {
		if(instance==null) {
			createInstance(level);
		}
		if (providerKey!=null) {
			// dann ist Instance ein Provider
			Provider provider = (Provider) instance;
			Object object = provider.get();
			if(monitor!=null) monitor.log(instance.getClass()+" is a provider, provide: "+object.getClass());
			return object;
		}
		return instance;
	}

	private void createInstance(int level) {
		
		// Sonderfall Provider 
		if(providerKey!=null) {
			if (monitor != null) monitor.log(levelPrefix(level) + "Search provider key=" + providerKey);
			instance = crmContainer.getInstance(providerKey, level);
			return;
		}
		
		if (implClass == null) {
			// es existiert fuer diesen key keine Implementierungsklasse, raus mit Exception
			throw new ServiceCreationException(key, " no Implementation-Class");
		}
		createLock.lock();
		try {

			if (monitor != null)
				monitor.log(levelPrefix(level) + "Creating instance for key=" + key
						+ " with implementation " + implClass);

			Constructor constr = implClass.getConstructor();
			instance = constr.newInstance();

			injectDependencies(level+1);
			if (instance instanceof Startable) {
				Startable startable = (Startable) instance;
				if (monitor != null)
					monitor.log(levelPrefix(level) +"Call start on " + key);
				startable.start();
			}
		} catch (Exception e) {
			throw new ServiceCreationException(key, e);
		} finally {
			createLock.unlock();
		}
	}

	/**
	 * Injecten aller Abhaengikeiten (Setter), die durch die
	 * Annotation @Inject markiert sind
	 *
	 * @param level Aufruf-Level (nur fuer Monitorzwecke)
	 * @throws Exception
	 */
	private void injectDependencies(int level) throws Exception {
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
			
			// Such-Key ist entweder der Annotion-Value oder, wenn nicht gesetzt, die Parameterklasse 
			Object key = (annoValue.length()>0) ? annoValue : paraClass; 
			
			if (monitor!=null) 	monitor.log(levelPrefix(level)+"Injecting dependencies: "+key);
			try {
				param = crmContainer.getInstance(key, level);
			} catch (RuntimeException e) { param = null; }
			
			if (param!=null) {
				method.invoke(instance, new Object[] {param});
			} else if (!anno.optional()) {
				throw new ServiceCreationException(key, "no Instance found for Injection, key="+key);
			}
		}
	}

	/**
	 * Helper-Methode zum Ausgeben des Level-Praefixes
	 *
	 * @param level
	 * @return
	 */
	private String levelPrefix(int level) {
		StringBuffer sb = new StringBuffer();
		while(--level>=0) sb.append("-");
			
		return "#"+sb+"# ";
	}


}