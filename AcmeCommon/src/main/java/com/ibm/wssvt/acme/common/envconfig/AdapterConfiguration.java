package com.ibm.wssvt.acme.common.envconfig;

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

@XmlRootElement (name="Adapter")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdapterConfiguration", namespace = "http://common.acme.wssvt.ibm.com/envconfig/")
public class AdapterConfiguration implements Configrable<String, String>{
	
	private static final long serialVersionUID = -5867029955599335999L;	
	@XmlAttribute(name="class")
	private String className;	
	@XmlAttribute(name="id")
	private String id;
		
	@XmlElement (type=EnvConfigStringParameterizable.class)
	private Parameterizable<String, String> configuration = new EnvConfigStringParameterizable();
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public Parameterizable<String, String> getConfiguration() {
		return this.configuration;
	}
	public void setConfiguration(Parameterizable<String, String> config) {
		this.configuration = config;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String type) {
		this.className = type;
	}
		
	
}
