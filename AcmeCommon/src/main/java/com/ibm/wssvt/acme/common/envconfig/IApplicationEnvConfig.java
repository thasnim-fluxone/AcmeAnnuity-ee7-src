package com.ibm.wssvt.acme.common.envconfig;

import java.util.List;
import java.util.Map;

import com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitStack;

public interface IApplicationEnvConfig {

	public Map<String, ExceptionHandlerConfiguration> getExceptionHandlers();

	public ClientConfiguration getClientConfiguration();

	public int getExecutionUnitsCount();

	public List<IExecutionUnit> getExecutionUnits() throws ConfigurationException;
	
	public List<IExecutionUnitStack> getExecutionUnitStacks() throws ConfigurationException;
	public int getExecutionUnitStacksCount();

}