package com.ibm.wssvt.acme.common.envconfig;

import java.util.ArrayList;

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

@XmlRootElement (name="ExecutionUnitStack")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecutionUnitStackConfiguration", namespace = "http://common.acme.wssvt.ibm.com/envconfig/")
public class ExecutionUnitStackConfiguration implements Configrable<String, String>{
	
	private static final long serialVersionUID = -5867029955599335999L;
		
	@XmlAttribute(name="description")
	private String description;
	
	@XmlAttribute(name="repeat")
	private int repeat;	
			
	@XmlElement (type=EnvConfigStringParameterizable.class)
	private Parameterizable<String, String> configuration = new EnvConfigStringParameterizable();
	
	@XmlElement (name="ExecutionUnit")
	private ArrayList<ExecutionUnitConfiguration> stackExecutionUnitsList= new ArrayList<ExecutionUnitConfiguration>();
	
	public Parameterizable<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Parameterizable<String, String> configuration) {
		this.configuration = configuration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public ArrayList<ExecutionUnitConfiguration> getStackExecutionUnitsList() {
		return stackExecutionUnitsList;
	}

	public void setStackExecutionUnitsList(
			ArrayList<ExecutionUnitConfiguration> stackExecutionUnitsList) {
		this.stackExecutionUnitsList = stackExecutionUnitsList;
	}

			
	
}
