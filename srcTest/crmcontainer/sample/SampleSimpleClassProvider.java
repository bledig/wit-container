package crmcontainer.sample;

import crmcontainer.Inject;
import crmcontainer.Provider;

public class SampleSimpleClassProvider implements Provider<SimpleClass> {

	private SimpleClass instance;
	
	private String msg;

	public SimpleClass get() {
		if(instance==null)
			instance = new SimpleClass(msg);
		return instance;
	}

	@Inject("message")
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
