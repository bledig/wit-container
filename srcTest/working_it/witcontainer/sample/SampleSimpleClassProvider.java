package working_it.witcontainer.sample;

import working_it.witcontainer.Inject;
import working_it.witcontainer.Provider;

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
