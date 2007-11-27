package crmcontainer;

import java.lang.reflect.Method;

/**
 *
 *
 * @author Bernd Ledig
 *
 */
public class ServiceCreationException extends RuntimeException {

	public ServiceCreationException(Object key, Exception e) {
		super("Internal Error in creation of instance "+key+":"+e.getMessage(), e);
	}

	public ServiceCreationException(Class<?> name) {
		super("Internal Error in creation of instance "+name.getCanonicalName());
	}

	public ServiceCreationException(Class clazz, Method method) {
		super("Internal Error creation of instance "+clazz+" Method "+method.getName()+" expects not one Parameter.");
	}

	public ServiceCreationException(String string) {
		super("Internal Error in creation of instance "+string);
	}

}
