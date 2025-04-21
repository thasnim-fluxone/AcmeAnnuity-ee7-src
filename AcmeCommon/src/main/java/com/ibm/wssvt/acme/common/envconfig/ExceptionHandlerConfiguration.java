package com.ibm.wssvt.acme.common.envconfig;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.Parameterizable;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

@XmlRootElement (name="ExceptionHandler")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExceptionHandlerConfiguration", namespace = "http://common.acme.wssvt.ibm.com/envconfig/")
public class ExceptionHandlerConfiguration implements Configrable<String, String>{
	
	private static final long serialVersionUID = 7447084428569069597L;
	@XmlAttribute (name="handlerClassName")
	private String handlerClassName;	
	@XmlAttribute (name="exceptionClassName")
	private String exceptionClassName;
	
	
	@XmlElement (type=EnvConfigStringParameterizable.class, name="configuration")
	private Parameterizable<String, String> configuration = new EnvConfigStringParameterizable();
	
	
	public String getHandlerClassName() {
		return handlerClassName;
	}
	public void setHandlerClassName(String action) {
		this.handlerClassName = action;
	}
	
	public String getExceptionClassName() {
		return exceptionClassName;
	}
	public void setExceptionClassName(String name) {
		this.exceptionClassName = name;
	}
	public Parameterizable<String, String> getConfiguration() {
		return this.configuration;
	}
	public void setConfiguration(Parameterizable<String, String> config) {
		this.configuration = config;
	}
	
	public List<String> getOverrides() {
		List<String> overrides = new ArrayList<String>();
		for (String key : getConfiguration().getParameters().keySet()) {
			if (key.toLowerCase().startsWith("override")){
				overrides.add(getConfiguration().getParameterValue(key));
			}
		}
		return overrides;
	}
	
	public long getMaxCount(){
		String val = getConfiguration().getParameterValue("maxCount");
		if (val == null || val.trim().length() <1) { 
			return 0;
		}
		
		try{
			return Long.parseLong(val);
		}catch (Throwable t) {			
			return 0;
		}
	}
}
