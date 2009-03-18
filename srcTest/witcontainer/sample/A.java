package witcontainer.sample;

import witcontainer.Inject;

public class A {

	private String name;
	private B b;
	
	@Inject
	public void setB(B b) {
		this.b = b;
	}
	
	@Inject("db_name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dbName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the b
	 */
	public B getB() {
		return b;
	}
	
}
