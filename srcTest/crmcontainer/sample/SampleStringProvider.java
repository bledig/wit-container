package crmcontainer.sample;

import crmcontainer.Provider;

public class SampleStringProvider implements Provider<String> {


	public String get() {
		return "instance by provider";
	}

}
