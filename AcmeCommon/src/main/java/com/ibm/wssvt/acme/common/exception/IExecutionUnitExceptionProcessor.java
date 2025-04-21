package com.ibm.wssvt.acme.common.exception;

import java.util.Map;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;

public interface IExecutionUnitExceptionProcessor {	
	public IExceptionHandlerResult processException (Map<String, ExceptionHandlerConfiguration> exceptionHandlerConfigMap, Exception exception, IExecutionUnit executionUnit);
}
