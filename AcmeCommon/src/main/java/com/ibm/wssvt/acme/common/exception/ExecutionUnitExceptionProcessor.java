package com.ibm.wssvt.acme.common.exception;

import java.util.Map;
import java.util.StringTokenizer;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;

public class ExecutionUnitExceptionProcessor implements IExecutionUnitExceptionProcessor {
	
	
	public IExceptionHandlerResult processException(Map<String, ExceptionHandlerConfiguration> exceptionHandlerConfigMap, Exception exception, IExecutionUnit executionUnit) {					
		IExceptionHandlerResult result = new ExceptionHandlerResult();		
		ExceptionHandlerConfiguration exceptionHandlerConfiguration = getExceptionHandlerConfig(exceptionHandlerConfigMap, exception);
		if (exceptionHandlerConfiguration == null) {
			result.setAction(ExceptionAction.STOP);
			result.setMessage("*** POSSIBLE DEFECT *** The client encountered an error that it did not expect."
				+ " Make sure that the Client Config Env contains all expected errors." 
				+ " No configuration for exception class: " + exception.getClass().getName()
				+ ".  Error Message is:" + exception.getMessage());
			return result;
		}
		
		try {
			IExceptionHandler exceptionHandler = (IExceptionHandler) Thread.currentThread().getContextClassLoader().loadClass(exceptionHandlerConfiguration.getHandlerClassName()).newInstance();
			return exceptionHandler.handleException(exceptionHandlerConfiguration, exception);
		} catch (IllegalAccessException e) {
			result.setAction(ExceptionAction.STOP);
			result.setMessage("Failed to load the Exception Handler: " + exceptionHandlerConfiguration.getExceptionClassName() 
					+ " with error: " + e);
			return result;
		} catch (InstantiationException e) {
			result.setAction(ExceptionAction.STOP);
			result.setMessage("Failed to load the Exception Handler: " + exceptionHandlerConfiguration.getExceptionClassName() 
					+ " with error: " + e);
			return result;
		} catch (ClassNotFoundException e) {
			result.setAction(ExceptionAction.STOP);
			result.setMessage("Failed to load the Exception Handler: " + exceptionHandlerConfiguration.getExceptionClassName() 
					+ " with error: " + e);
			return result;
		}		
	}

	private static ExceptionHandlerConfiguration getExceptionHandlerConfig(Map<String, ExceptionHandlerConfiguration> exceptionHandlerConfigMap, Exception error) {
		// first full class
		ExceptionHandlerConfiguration exHandlerConfig = exceptionHandlerConfigMap.get(error.getClass().getName());
		if (exHandlerConfig == null) {
			// not found, find it by class level.
			StringTokenizer st = new StringTokenizer(error.getClass().getName(), ".");
			String partialName = "";
			String multiPartialName ="";
			while (st.hasMoreTokens()){
				String token = st.nextToken();
				multiPartialName += token + ".";
				partialName = multiPartialName + "*";				
				exHandlerConfig = exceptionHandlerConfigMap.get(partialName);
				if (exHandlerConfig != null){
					// found it
					//logger.fine("found the exception config with generic class name: " + partialName);
					break;
				}
			}
		}else{
			//logger.fine("found the exception config with complete class name: " + error.getClass().getName());
		}
		return exHandlerConfig;
	}
	

}
