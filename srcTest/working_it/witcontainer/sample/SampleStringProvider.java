package working_it.witcontainer.sample;

import working_it.witcontainer.Provider;

public class SampleStringProvider implements Provider<String> {


	public String get() {
		return "instance by provider";
	}

}
