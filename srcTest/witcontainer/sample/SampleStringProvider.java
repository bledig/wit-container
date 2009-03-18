package witcontainer.sample;

import witcontainer.Provider;

public class SampleStringProvider implements Provider<String> {


	public String get() {
		return "instance by provider";
	}

}
