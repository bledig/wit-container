package crmcontainer.sample;

import crmcontainer.Inject;
import crmcontainer.Startable;

public class B implements Startable {

	private A a;
	private boolean started;

	/**
	 * @param a the a to set
	 */
	@Inject
	public void setA(A a) {
		this.a = a;
	}

	/**
	 * @return the a
	 */
	public A getA() {
		return a;
	}

	public void start() {
		started = true;
	}

	/**
	 * @return the started
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * @param started the started to set
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}
}
