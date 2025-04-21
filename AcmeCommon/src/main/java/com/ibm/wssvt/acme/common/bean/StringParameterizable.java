package com.ibm.wssvt.acme.common.bean;

import java.util.HashMap;
import java.util.Map;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class StringParameterizable implements Parameterizable<String, String> {
	private static final long serialVersionUID = -4289064323865338447L;
	private Map<String, String> params = new HashMap<String, String>();
	
	public void addParameter(String key, String value) {
		this.params.put(key, value);
	}

	public Map<String, String> getParameters() {
		return this.params;
	}

	public void removeParameter(String key) {
		this.params.remove(key);
	}

	public void clearAllParameters() {
		this.params.clear();
	}

	public String getParameterValue(String key) {
		return this.params.get(key);
	}
	
	public void addAllParams(Map<String, String> newParams) {
		if (newParams != null) {
			params.putAll(newParams);
		}
	}
}
