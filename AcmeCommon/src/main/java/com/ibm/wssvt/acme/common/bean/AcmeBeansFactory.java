package com.ibm.wssvt.acme.common.bean;

public class AcmeBeansFactory implements IBeansFactory {

	public Parameterizable<String, String> createParameterizable() {
		return new StringParameterizable();
	}

}
