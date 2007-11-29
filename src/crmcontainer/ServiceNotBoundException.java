package crmcontainer;

/**
 *
 *
 * @author Bernd Ledig
 *
 */
public class ServiceNotBoundException extends RuntimeException {


	public ServiceNotBoundException(Object key) {
		super("Key '"+key+"' not bound ! ");
	}


}
