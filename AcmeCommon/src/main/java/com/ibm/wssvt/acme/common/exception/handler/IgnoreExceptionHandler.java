package com.ibm.wssvt.acme.common.exception.handler;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.exception.ExceptionAction;
import com.ibm.wssvt.acme.common.exception.ExceptionHandlerResult;
import com.ibm.wssvt.acme.common.exception.IExceptionHandler;
import com.ibm.wssvt.acme.common.exception.IExceptionHandlerResult;

public class IgnoreExceptionHandler implements IExceptionHandler {

	public IExceptionHandlerResult handleException(
			ExceptionHandlerConfiguration exceptionHandlerConfiguration, Exception exception) {
		
		IExceptionHandlerResult result = new ExceptionHandlerResult();
		result.setAction(ExceptionAction.IGNORE);
		result.setMessage("Exception:  " + exception.getClass().getName() + ", matching pattern: " 
				+ exceptionHandlerConfiguration.getExceptionClassName() + " was ignored." );
		return result;
	}

}
