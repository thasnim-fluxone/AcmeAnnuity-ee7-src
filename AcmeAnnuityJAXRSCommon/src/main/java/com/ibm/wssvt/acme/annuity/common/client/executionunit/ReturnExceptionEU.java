package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
/**
 * This EU will ALWAYS return an Exception.
 * The exception to be thrown is passed to the EU in the param: exceptionClassName
 * if that class is not found, or fails to load, then InvalidExecutionUnitParameterException will be returned
 * if the EU is able to load the Exception, the correct Exception will be returned. (by adding it to the events)
 * 
 *  One might ask: why you need such an EU?  this is to test the behavior of the framework for exceptions / exception handling.
 *  
 */
public class ReturnExceptionEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = 5751982103363852382L;
	
	@SuppressWarnings("unchecked")
	public void execute() {
		try {
			AcmeLogger logger = getLogger(getClass().getName());
			String className = getConfiguration().getParameterValue("exceptionClassName");
			logger.fine("exceptionClassName is: " + className);
			if (className == null ) {
				throw new InvalidExecutionUnitParameterException("The exceptionClassName param was null " + logger.getAllLogs());
			}
			if (className.trim().length() ==0 ) {
				throw new InvalidExecutionUnitParameterException("The exceptionClassName param was empty " + logger.getAllLogs());
			}
			logger.fine("className is: " + className);
			Class exClass = null;
			try {
				logger.fine("ready to load the class");
				exClass = Thread.currentThread().getContextClassLoader().loadClass(className);
				logger.fine("class loaded");
			} catch (ClassNotFoundException e) {
				String msg = "Failed to load the class: " + className + ".  Error: " +e.getMessage() + logger.getAllLogs();
				logger.info(msg);
				throw new InvalidExecutionUnitParameterException(msg, e);
			}
			Exception exception = null;
			try {
				logger.info("ready to get newInstance");
				exception = (Exception) exClass.newInstance();
				logger.info("got newInstance");
			} catch (IllegalAccessException e) {
				String msg = "Failed to create a new instance from the class: " + className + ".  Error: " +e.getMessage() + logger.getAllLogs();
				logger.info(msg);
				throw new InvalidExecutionUnitParameterException(msg, e);
			} catch (InstantiationException e) {
				String msg = "Failed to create a new instance from the class: " + className + ".  Error: " +e.getMessage() + logger.getAllLogs();
				logger.info(msg);
				throw new InvalidExecutionUnitParameterException(msg, e);
			}
			// the purpose of this eu is to throw the exception
			logger.fine("now throwing the exception: " + exception + " This is not an error, as this EU is designed to return the exception!");
			throw exception;
		} catch (Exception e) {			
			getExecutionUnitEvent().addException(e);			
		}		
		
	}

}
