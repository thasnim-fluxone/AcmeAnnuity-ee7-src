package com.ibm.wssvt.acme.common.envconfig;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement (name="AcmeEnvConfig")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcmeEnvConfigBean", namespace = "http://common.acme.wssvt.ibm.com/envconfig/")
public class AcmeEnvConfigBean  {
	
	@XmlElement(name="GlobalConfigurationParams")
	private EnvConfigStringParameterizable globalConfigurationParams = new EnvConfigStringParameterizable();
	
	@XmlElement(name= "ClientConfiguration")
	private ClientConfiguration clientConfiguration;
				
	@XmlElement (name="Adapter")
	private ArrayList<AdapterConfiguration> adaptersList = new ArrayList<AdapterConfiguration>();	
		
	@XmlElement (name="ExecutionUnit")
	private ArrayList<ExecutionUnitConfiguration> executionUnitList= new ArrayList<ExecutionUnitConfiguration>();
	
	@XmlElement (name="ExecutionUnitStack")
	private ArrayList<ExecutionUnitStackConfiguration> executionUnitStackList= new ArrayList<ExecutionUnitStackConfiguration>();		

	@XmlElement (name="ExceptionHandler")
	private ArrayList<ExceptionHandlerConfiguration> exceptionHandlerList = new ArrayList<ExceptionHandlerConfiguration>();
	

	public AcmeEnvConfigBean() {
	}
			
	public ArrayList<AdapterConfiguration> getAdaptersConfigurationList() {	
		return adaptersList;
	}


	public void setAdapterConfigurationList(ArrayList<AdapterConfiguration> adaptersList) {
		this.adaptersList = adaptersList;
	}

		
	public ClientConfiguration getClientConfiguration() {									
		return clientConfiguration;
	}


	public void setClientConfiguration(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
	}


	public ArrayList<ExceptionHandlerConfiguration> getExceptionHandlerConfigurationList() {
		return exceptionHandlerList;
	}


	public void setExceptionHandlerConfigurationList(
			ArrayList<ExceptionHandlerConfiguration> exceptionHandlerList) {
		this.exceptionHandlerList = exceptionHandlerList;
	}
	
	
	public ArrayList<ExecutionUnitConfiguration> getExecutionUnitConfigurationList() {
		return executionUnitList;
	}
	
	
	public void setExecutionUnitConfigurationList(
			ArrayList<ExecutionUnitConfiguration> executionUnitList) {
		this.executionUnitList = executionUnitList;
	}

	public EnvConfigStringParameterizable getGlobalConfigurationParams() {
		return globalConfigurationParams;
	}


	public void setGlobalConfigurationParams(
			EnvConfigStringParameterizable globalConfigurationParams) {
		this.globalConfigurationParams = globalConfigurationParams;
	}

	public ArrayList<ExecutionUnitStackConfiguration> getExecutionUnitStackList() {
		return executionUnitStackList;
	}

	public void setExecutionUnitStackList(
			ArrayList<ExecutionUnitStackConfiguration> executionUnitStackList) {
		this.executionUnitStackList = executionUnitStackList;
	}	
	
}
