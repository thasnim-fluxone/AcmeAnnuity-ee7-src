package com.ibm.wssvt.acme.common.bean;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class StringConfigrable implements Configrable<String, String> {

	private static final long serialVersionUID = -1424738803778771963L;
	private Parameterizable<String, String> configs = new StringParameterizable();
	public Parameterizable<String, String> getConfiguration() {
		return this.configs;
	}

	public void setConfiguration(Parameterizable<String, String> config) {
		this.configs = config;
	}


}
