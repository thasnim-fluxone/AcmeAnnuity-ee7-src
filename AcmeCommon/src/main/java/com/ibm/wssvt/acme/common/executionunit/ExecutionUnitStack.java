package com.ibm.wssvt.acme.common.executionunit;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.common.bean.Parameterizable;
import com.ibm.wssvt.acme.common.bean.StringParameterizable;

public class ExecutionUnitStack implements IExecutionUnitStack{
	private static final long serialVersionUID = 5735772380914550815L;
	private String description;
	private List<IExecutionUnit> stackExecutionUnits = new ArrayList<IExecutionUnit>();
	private Parameterizable<String, String> configuration = new StringParameterizable();
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<IExecutionUnit> getStackExecutionUnits() {
		return stackExecutionUnits;
	}
	public void setStackExecutionUnits(List<IExecutionUnit> stackExecutionUnits) {
		this.stackExecutionUnits = stackExecutionUnits;
	}
	public Parameterizable<String, String> getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(Parameterizable<String, String> config) {
		this.configuration = config;		
	}
	

}
