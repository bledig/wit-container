package witcontainer;

public class DuplicateBindException extends RuntimeException {

	public DuplicateBindException(Object key) {
		super("Duplicate bind key="+key);
	}

}
