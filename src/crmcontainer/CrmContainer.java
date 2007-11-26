package crmcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 *
 * @author Bernd Ledig
 *
 */
public class CrmContainer {
	
	private final Map<Class, Class> implMap = new HashMap<Class, Class>();
	
	private final Map<Object, Object> instanceMap = new HashMap<Object, Object>();
	
	private Monitor monitor;

	public <T>void bind(Class<T> key, Class<? extends T> impl) {
		implMap.put(key, impl);
	}
	
	public <T>void bind(Class<T> key) {
		bind(key, key);
	}
	
	public void bind(Object key, Object instance) {
		instanceMap.put(key, instance);
	}
	
	public Object getInstance(Object key) {
		Object service = instanceMap.get(key);
		if (service!=null)
			return service;
		throw new ServiceNotBoundException(key);
	}
	
	public <T> T getInstance(Class <T> key) {
		T service = (T) instanceMap.get(key);
		if (service!=null)
			return service;
		
		Class<T> implClass = implMap.get(key);
		if (implClass==null) {
			throw new ServiceNotBoundException(key);
		}
		
		try {
			service = (T) createInstance(key, implClass);
		} catch (Exception e) {
			throw new ServiceCreationException(key, e);
		}
		return service;
	}
	
	private void log(String string) {
		if (monitor!=null)
			monitor.log(string);
	}

	private <T>T createInstance(Class <T> key, Class<? extends T> clazz) throws Exception {
		log("Creating service "+key+" with implementation "+ clazz);
		Constructor<? extends T> constr = clazz.getConstructor();
		T instance = constr.newInstance();
		instanceMap.put(key, instance);
		injectDependencies(instance);
		if (instance instanceof Startable) {
			Startable startable = (Startable) instance;
			log("Call start on "+key);
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
				log("Injecting dependencies: "+annoValue);
				param = getInstance(annoValue);
				if (param==null)
					throw new ServiceCreationException(annoValue);
			} else {
				log("Injecting dependencies: "+paraClass);
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
