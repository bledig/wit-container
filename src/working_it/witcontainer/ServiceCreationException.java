package working_it.witcontainer;

import java.lang.reflect.Method;

/**
 *
 *
 * @author Bernd Ledig
 *
 */
@SuppressWarnings("serial")
public class ServiceCreationException extends RuntimeException {

	public ServiceCreationException(Object key, Exception e) {
		super(e.getMessage()+" : Internal Error in creation of instance for key="+key, e);
	}

	public ServiceCreationException(Class<?> name) {
		super("Internal Error in creation of instance "+name.getCanonicalName());
	}

	public ServiceCreationException(Class clazz, Method method) {
		super("Internal Error creation of instance "+clazz+" Method "+method.getName()+" expects not one Parameter.");
	}

	public ServiceCreationException(Object key, String string) {
		super("Internal Error by creation of instance for key="+key+": "+string);
	}

}
