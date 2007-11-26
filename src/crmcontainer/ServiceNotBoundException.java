package crmcontainer;

/**
 *
 *
 * @author Bernd Ledig
 *
 */
public class ServiceNotBoundException extends RuntimeException {

	public ServiceNotBoundException(Class clazz) {
		super("Class not bound : "+clazz.getCanonicalName());
	}

	public ServiceNotBoundException(Object key) {
		super("Instance not bound : "+key);
	}


}
