package com.ibm.wssvt.acme.common.exception.handler;

import com.ibm.wssvt.acme.common.envconfig.ExceptionHandlerConfiguration;
import com.ibm.wssvt.acme.common.exception.ExceptionAction;
import com.ibm.wssvt.acme.common.exception.ExceptionHandlerResult;
import com.ibm.wssvt.acme.common.exception.IExceptionHandler;
import com.ibm.wssvt.acme.common.exception.IExceptionHandlerResult;

public class CountOnlyExceptionHandler implements IExceptionHandler {

	public IExceptionHandlerResult handleException(
			ExceptionHandlerConfiguration exceptionHandlerConfiguration, Exception exception) {
		
		IExceptionHandlerResult result = new ExceptionHandlerResult();		
		result.setAction(ExceptionAction.REPORT_AND_COUNT);
		result.setMessage("Exception:  " + exception.getClass().getName() + ", matching pattern: " + exceptionHandlerConfiguration.getExceptionClassName() + " was ignored, " +
				"but it was counted.  Complete error message: " + exception.getMessage() );
		return result;
	}

}
