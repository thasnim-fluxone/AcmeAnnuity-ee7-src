package com.ibm.wssvt.acme.common.envconfig;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.common.bean.Parameterizable;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnvConfigStringParameterizable", namespace = "http://common.acme.wssvt.ibm.com/envconfig/")
public class EnvConfigStringParameterizable implements Parameterizable<String, String> {
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
