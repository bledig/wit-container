package witcontainer.sample;

import witcontainer.Startable;

public class C implements Startable {

	private boolean started;



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
